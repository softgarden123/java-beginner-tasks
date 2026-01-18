// java
import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.Arrays;

public class HelloWorld extends JFrame {
    private final JTextArea inputArea = new JTextArea(6, 40);
    private final JTextArea outputArea = new JTextArea(10, 40);
    private final JButton runButton = new JButton("実行");

    public HelloWorld() {
        super("JavaCompiler 実行デモ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        inputArea.setText("");
        JScrollPane inScroll = new JScrollPane(inputArea);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("System.out.println(...) を用いて文字列を出力してみよう"), BorderLayout.NORTH);
        topPanel.add(inScroll, BorderLayout.CENTER);

        outputArea.setEditable(false);
        JScrollPane outScroll = new JScrollPane(outputArea);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("実行結果"), BorderLayout.NORTH);
        bottomPanel.add(outScroll, BorderLayout.CENTER);

        JPanel control = new JPanel();
        control.add(runButton);

        add(topPanel, BorderLayout.NORTH);
        add(control, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        runButton.addActionListener(e -> runUserCode());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void runUserCode() {
        runButton.setEnabled(false);
        outputArea.setText("");

        String userCode = inputArea.getText();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                Path tempDir = null;
                URLClassLoader classLoader = null;
                try {
                    // 一意なクラス名を生成
                    String className = "UserProgram" + System.currentTimeMillis();
                    String source = buildSource(className, userCode);

                    tempDir = Files.createTempDirectory("javac_run_");
                    Path srcFile = tempDir.resolve(className + ".java");
                    Files.writeString(srcFile, source);

                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                    if (compiler == null) {
                        appendOutput("Error: 実行環境に JDK が必要です（JRE のみでは動作しません）。");
                        return null;
                    }

                    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                    StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, null, null);
                    Iterable<? extends JavaFileObject> units = fm.getJavaFileObjectsFromFiles(Arrays.asList(srcFile.toFile()));
                    boolean ok = compiler.getTask(null, fm, diagnostics, Arrays.asList("-d", tempDir.toString()), null, units).call();
                    fm.close();

                    if (!ok) {
                        StringBuilder sb = new StringBuilder("コンパイルエラー:\n");
                        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
                            sb.append(d.getKind()).append(": ").append(d.getMessage(null))
                                    .append(" at ").append(d.getSource() == null ? "?" : d.getSource().getName())
                                    .append(" line ").append(d.getLineNumber()).append("\n");
                        }
                        appendOutput(sb.toString());
                        return null;
                    }

                    classLoader = new URLClassLoader(new URL[]{tempDir.toUri().toURL()});
                    Class<?> cls = classLoader.loadClass(className);
                    Method main = cls.getMethod("main", String[].class);

                    // 標準出力をキャプチャ
                    PrintStream oldOut = System.out;
                    PrintStream oldErr = System.err;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(baos);
                    System.setOut(ps);
                    System.setErr(ps);

                    try {
                        main.invoke(null, (Object) new String[0]);
                    } catch (Throwable invocationEx) {
                        invocationEx.printStackTrace(ps);
                    } finally {
                        ps.flush();
                        System.setOut(oldOut);
                        System.setErr(oldErr);
                    }

                    appendOutput(baos.toString());
                } catch (Exception ex) {
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    appendOutput("例外が発生しました:\n" + sw.toString());
                } finally {
                    try { if (classLoader != null) classLoader.close(); } catch (IOException ignored) {}
                    // 一時ファイルは残す/削除どちらでも可。ここでは削除を試みる
                    if (tempDir != null) {
                        try {
                            Files.walk(tempDir)
                                    .sorted((a, b) -> b.compareTo(a))
                                    .forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignored) {} });
                        } catch (IOException ignored) {}
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
            }
        }.execute();
    }

    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> outputArea.append(text + "\n"));
    }

    private String buildSource(String className, String userStatements) {
        // シンプルなテンプレートにユーザ文をそのまま埋める（必要ならサニタイズや検査を追加）
        return "public class " + className + " {\n" +
                "    public static void main(String[] args) {\n" +
                "        try {\n" +
                "            " + userStatements + "\n" +
                "        } catch (Throwable t) {\n" +
                "            t.printStackTrace();\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HelloWorld::new);
    }
}