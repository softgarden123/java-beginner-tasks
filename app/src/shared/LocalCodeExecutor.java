package shared;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class LocalCodeExecutor {

    private static final long EXECUTION_TIMEOUT_SECONDS = 30;

    public static class ExecutionResult {
        public final boolean success;
        public final String output;
        public final String error;

        public ExecutionResult(boolean success, String output, String error) {
            this.success = success;
            this.output = output;
            this.error = error;
        }
    }

    public ExecutionResult execute(String fileName, String sourceCode, String mainClassName) {
        Path tempDir = null;
        StandardJavaFileManager fileManager = null;
        try {
            tempDir = Files.createTempDirectory("local_java_exec_");
            Path sourceFile = tempDir.resolve(fileName);
            Files.writeString(sourceFile, sourceCode, StandardCharsets.UTF_8);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                return new ExecutionResult(false, "", "JDKが必要です（JREではコンパイルできません）。");
            }

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFile.toFile());
            boolean compiled = compiler.getTask(null, fileManager, diagnostics, List.of("-d", tempDir.toString(), "-encoding", "UTF-8"), null, compilationUnits).call();
            if (!compiled) {
                StringBuilder sb = new StringBuilder();
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    sb.append(diagnostic.getKind())
                            .append(": ")
                            .append(diagnostic.getMessage(null))
                            .append(" at ")
                            .append(diagnostic.getSource() == null ? "?" : diagnostic.getSource().getName())
                            .append(" line ")
                            .append(diagnostic.getLineNumber())
                            .append("\n");
                }
                return new ExecutionResult(false, "", sb.toString());
            }

            return runInSubprocess(tempDir, mainClassName);
        } catch (IOException | RuntimeException e) {
            String message = e.getMessage();
            if (message == null) {
                message = e.getClass().getName();
            }
            return new ExecutionResult(false, "", "実行中にエラーが発生しました: " + message);
        } finally {
            if (fileManager != null) {
                try { fileManager.close(); } catch (IOException ignored) {}
            }
            if (tempDir != null) {
                try {
                    Files.walk(tempDir)
                            .sorted((a, b) -> b.compareTo(a))
                            .forEach(path -> {
                                try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                            });
                } catch (IOException ignored) {
                }
            }
        }
    }

    private ExecutionResult runInSubprocess(Path tempDir, String mainClassName) {
        String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        File outputFile = tempDir.resolve("run-output.log").toFile();

        ProcessBuilder processBuilder = new ProcessBuilder(
                javaPath,
                "-Dfile.encoding=UTF-8",
                "-Dsun.stdout.encoding=UTF-8",
                "-Dsun.stderr.encoding=UTF-8",
                "-cp",
                tempDir.toAbsolutePath().toString(),
                mainClassName
        );
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(outputFile);

        try {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
                String output = readOutputBestEffort(outputFile.toPath());
                return new ExecutionResult(false, output, "実行がタイムアウトしました (" + EXECUTION_TIMEOUT_SECONDS + "秒)");
            }

            int exitCode = process.exitValue();
            String output = readOutputBestEffort(outputFile.toPath());
            if (exitCode != 0) {
                return new ExecutionResult(false, output, "実行が異常終了しました (exit code: " + exitCode + ")");
            }
            return new ExecutionResult(true, output, "");
        } catch (IOException e) {
            return new ExecutionResult(false, "", "実行プロセスの起動に失敗しました: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ExecutionResult(false, "", "実行が中断されました。");
        }
    }

    private String readOutputBestEffort(Path outputPath) {
        try {
            if (!Files.exists(outputPath)) {
                return "";
            }
            return Files.readString(outputPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
