package shared;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class UserCodeExecutor {

    public interface OutputHandler {
        void onOutput(String text);
    }

    private static final String TEMP_DIR_NAME = "temp";
    private static final long EXECUTION_TIMEOUT_SECONDS = 30;

    public void execute(Map<String, String> sourceFiles, OutputHandler handler) {
        execute(sourceFiles, "Main", handler);
    }

    public void execute(Map<String, String> sourceFiles, String mainClassName, OutputHandler handler) {
        File runDir = null;
        try {
            runDir = createRunDirectory();
            List<File> files = writeSourceFiles(sourceFiles, runDir);
            String compileOutput = compileFiles(files.toArray(File[]::new));
            if (!compileOutput.isEmpty()) {
                handler.onOutput("コンパイルエラーがあります:\n" + compileOutput);
                return;
            }
            runMainClass(mainClassName, runDir, handler);
        } catch (Exception e) {
            handler.onOutput("エラー: " + e.getMessage());
        } finally {
            deleteDirectoryQuietly(runDir);
        }
    }

    private File createRunDirectory() throws IOException {
        Path tempRoot = new File(TEMP_DIR_NAME).toPath();
        Files.createDirectories(tempRoot);
        return Files.createTempDirectory(tempRoot, "run_").toFile();
    }

    private List<File> writeSourceFiles(Map<String, String> sourceFiles, File dir) throws IOException {
        List<File> files = new ArrayList<>();
        for (Map.Entry<String, String> entry : sourceFiles.entrySet()) {
            File file = new File(dir, entry.getKey());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
            }
            files.add(file);
        }
        return files;
    }

    private String compileFiles(File... files) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return "JDKが必要です（JREではコンパイルできません）";
        }

        String[] filePaths = new String[files.length + 2];
        filePaths[0] = "-encoding";
        filePaths[1] = "UTF-8";
        for (int i = 0; i < files.length; i++) {
            filePaths[i + 2] = files[i].getPath();
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int result = compiler.run(null, baos, baos, filePaths);
            String output = decodeBestEffort(baos.toByteArray());
            return result == 0 ? "" : (output.isEmpty() ? "コンパイルに失敗しました（詳細メッセージなし）" : output);
        } catch (IOException e) {
            return "コンパイル中にエラーが発生しました: " + e.getMessage();
        }
    }

    private void runMainClass(String mainClassName, File runDir, OutputHandler handler) throws Exception {
        String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classPath = runDir.getAbsolutePath();
        File outputFile = new File(runDir, "run-output.log");
        ProcessBuilder pb = new ProcessBuilder(
                javaPath,
                "-Dfile.encoding=UTF-8",
                "-Dsun.stdout.encoding=UTF-8",
                "-Dsun.stderr.encoding=UTF-8",
                "-cp",
                classPath,
                mainClassName
        );
        pb.redirectErrorStream(true);
        pb.redirectOutput(outputFile);

        Process process = pb.start();
        try {
            boolean finished = process.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
                emitOutputLines(handler, readOutputBytes(outputFile));
                handler.onOutput("実行がタイムアウトしました (" + EXECUTION_TIMEOUT_SECONDS + "秒)");
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }

        byte[] outputBytes = readOutputBytes(outputFile);
        boolean hasOutput = emitOutputLines(handler, outputBytes);
        if (!hasOutput) {
            handler.onOutput("(出力なし)");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            handler.onOutput("実行が異常終了しました (exit code: " + exitCode + ")");
        }
    }

    private byte[] readOutputBytes(File outputFile) throws IOException {
        if (!outputFile.exists()) {
            return new byte[0];
        }
        return Files.readAllBytes(outputFile.toPath());
    }

    private boolean emitOutputLines(OutputHandler handler, byte[] outputBytes) {
        String outputText = new String(outputBytes, StandardCharsets.UTF_8);
        if (outputText.isBlank()) {
            return false;
        }

        String[] lines = outputText.split("\\R", -1);
        for (String line : lines) {
            if (!line.isEmpty()) {
                handler.onOutput(line);
            }
        }
        return true;
    }

    private String decodeBestEffort(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        String utf8 = new String(bytes, StandardCharsets.UTF_8);
        Charset windows31j = Charset.forName("Windows-31J");
        String sjis = new String(bytes, windows31j);

        return scoreText(sjis) > scoreText(utf8) ? sjis : utf8;
    }

    private int scoreText(String text) {
        int score = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\uFFFD') {
                score -= 5;
                continue;
            }
            if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.HIRAGANA
                    || Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.KATAKANA
                    || Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                score += 2;
                continue;
            }
            if (ch == '\n' || ch == '\r' || ch == '\t' || (ch >= 0x20 && ch <= 0x7E)) {
                score += 1;
                continue;
            }
            if (Character.isISOControl(ch)) {
                score -= 2;
            }
        }
        return score;
    }

    private void deleteDirectoryQuietly(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }

        try {
            Files.walk(dir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
