import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UserCodeExecutor {

    private File writeTempFile(String fileName, String code) throws IOException {
        File dir = new File("temp");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("ディレクトリを作成できません: " + dir.getPath());
        }

        File file = new File(dir, fileName);
        // 明示的に UTF-8 で書き込む
        Files.write(Paths.get(file.getAbsolutePath()), code.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return file;
    }

    private boolean compileFiles(File... files) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("JDKが必要です（JREではコンパイルできません）");
            return false;
        }

        // コンパイラにソースのエンコーディングを明示的に渡す
        String[] args = new String[2];
        args[0] = "-encoding";
        args[1] = "UTF-8";
        System.arraycopy(new String[0], 0, args, 2, 0);
        int result = compiler.run(null, null, null, args);
        return result == 0;
    }

    private void runMainClass(OutputHandler handler) throws Exception {

        String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String tempPath = new File("temp").getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(
                javaPath,
                "-Dfile.encoding=UTF-8",
                "-cp",
                tempPath,
                "Main"
        );
        pb.redirectErrorStream(true);

        Process p = pb.start();

        try (InputStream is = p.getInputStream()) {
            ByteArrayOutputStream lineBuf = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) != -1) {
                lineBuf.write(b);
                if (b == '\n') {
                    byte[] lineBytes = lineBuf.toByteArray();
                    String chosen = chooseBestDecoding(lineBytes);
                    String trimmed = chosen.replaceFirst("\r?\n$", "");
                    handler.onOutput(trimmed);
                    lineBuf.reset();
                }
            }
            if (lineBuf.size() > 0) {
                byte[] lineBytes = lineBuf.toByteArray();
                String chosen = chooseBestDecoding(lineBytes);
                handler.onOutput(chosen.replaceFirst("\r?\n$", ""));
            }
        }

        int exitCode = p.waitFor();
        handler.onOutput("プロセス終了（終了コード " + exitCode + "）");
    }

    private String chooseBestDecoding(byte[] bytes) {
        String os = System.getProperty("os.name");
        boolean isWindows = os != null && os.toLowerCase().contains("win");

        if (isWindows) {
            // Windows では多くの場合 CP932（Windows-31J）が正しい
            Charset cp932Charset = Charset.forName("Windows-31J");
            return new String(bytes, cp932Charset);
        } else {
            // 非Windowsでは厳密に UTF-8 としてデコードできるか確認し、失敗したら CP932
            try {
                java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(bytes);
                java.nio.charset.CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
                decoder.onMalformedInput(java.nio.charset.CodingErrorAction.REPORT);
                decoder.onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT);
                java.nio.CharBuffer cb = decoder.decode(bb);
                return cb.toString();
            } catch (java.nio.charset.CharacterCodingException e) {
                Charset cp932Charset = Charset.forName("Windows-31J");
                return new String(bytes, cp932Charset);
            }
        }
    }

    public void execute(String mainCode, String humanCode, OutputHandler handler) {
        try {
            File mainFile = writeTempFile("Main.java", mainCode);
            File humanFile = writeTempFile("Human.java", humanCode);

            boolean success = compileFiles(mainFile, humanFile);

            if (!success) {
                handler.onOutput("コンパイルエラーがあります");
                return;
            }

            runMainClass(handler);

        } catch (Exception e) {
            handler.onOutput("エラー: " + e.getMessage());
        }
    }

    public interface OutputHandler {
        void onOutput(String text);
    }
}
