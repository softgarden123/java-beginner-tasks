import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class Calculator {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Java実行ツール");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        // コード入力欄
        JTextArea codeArea = new JTextArea("""
public class Calculator {
    // 足し算、引き算、掛け算、割り算の４つのメソッドを実装してください
    // 実装後、下部のメソッド名と引数を指定して「実行」ボタンを押してください
}
""");
        JScrollPane codeScroll = new JScrollPane(codeArea);

        // 出力欄
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);

        // メソッド名・引数入力欄
        JTextField methodField = new JTextField("add");
        JTextField argsField = new JTextField("3,5");

        // 実行ボタン
        JButton runButton = new JButton("実行");

        // コントロールパネル（下部）
        JPanel controlPanel = new JPanel(new GridLayout(3, 2));
        controlPanel.add(new JLabel("メソッド名:"));
        controlPanel.add(methodField);
        controlPanel.add(new JLabel("引数（カンマ区切り）:"));
        controlPanel.add(argsField);
        controlPanel.add(runButton);

        // SplitPaneで左右分割
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codeScroll, outputScroll);
        splitPane.setResizeWeight(0.5);

        // 実行処理
        String uniqueId = "Calculator" + System.currentTimeMillis();
        runButton.addActionListener(e -> {
            try {
                // 動的クラス名生成
                String code = codeArea.getText().replaceAll("class\\s+Calculator", "class " + uniqueId);

                // コンパイル
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                JavaFileObject file = new InMemory(uniqueId, code);
                Iterable<? extends JavaFileObject> compilationUnits = List.of(file);
                JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

                if (task.call()) {
                    // クラスロード
                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File(".").toURI().toURL() });
                    Class<?> clazz = Class.forName(uniqueId, true, classLoader);

                    // メソッド名と引数取得
                    String methodName = methodField.getText().trim();
                    String[] argStrings = argsField.getText().split(",");
                    int[] intArgs = Arrays.stream(argStrings).map(String::trim).mapToInt(Integer::parseInt).toArray();

                    // 引数型と値の準備
                    Class<?>[] paramTypes = new Class<?>[intArgs.length];
                    Arrays.fill(paramTypes, int.class);
                    Object[] boxedArgs = Arrays.stream(intArgs).boxed().toArray();

                    // インスタンス生成＋メソッド呼び出し
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod(methodName, paramTypes);
                    Object result = method.invoke(instance, boxedArgs);

                    outputArea.setText("戻り値: " + result);
                } else {
                    StringBuilder error = new StringBuilder();
                    for (Diagnostic<?> d : diagnostics.getDiagnostics()) {
                        error.append(d.getMessage(null)).append("\n");
                    }
                    outputArea.setText("コンパイルエラー:\n" + error);
                }
            } catch (Exception ex) {
                outputArea.setText("実行エラー:\n" + ex.getMessage());
            }
            File classFile = new File("./" + uniqueId + ".class");
            if (classFile.exists()) {
                classFile.delete();
            }

            outputArea.revalidate();
            outputArea.repaint();
        });

        // レイアウト構成
        frame.setLayout(new BorderLayout());
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}