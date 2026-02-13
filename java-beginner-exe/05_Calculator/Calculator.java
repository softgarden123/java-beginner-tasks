import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Calculator extends JFrame implements ActionListener {

    private JTextArea codeArea;
    private JTextArea outputArea;
    private JTextArea errorArea;
    private JButton runButton;

    public Calculator() {
        setTitle("課題⑤：メソッドの定義と呼び出し");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // -----------------------------
        // 初期テンプレート（条件分岐）
        // -----------------------------
        String template =
                "public class UserCode {\n" +
                        "    public static void main(String[] args) {\n" +
                        "       // TODO: 「add」メソッドを呼び出して、結果をaddResultに入れてください\n" +
                        "\n" +
                        "       System.out.println(\\\"足し算の結果: \\\" + addResult);\\n\" +\n" +
                        "\n" +
                        "       // TODO: 「sub」メソッドを呼び出して、結果をsubResultに入れてください\n" +
                        "\n" +
                        "       System.out.println(\\\"引き算の結果: \\\" + subResult);\\n\" +\n" +
                        "\n" +
                        "       // TODO: 「multi」メソッドを呼び出して、結果をmultiResultに入れてください\n" +
                        "\n" +
                        "       System.out.println(\\\"掛け算の結果: \\\" + multiResult);\\n\" +\n" +
                        "\n" +
                        "       // TODO: 「division」メソッドを呼び出して、結果をdivisionResultに入れてください\n" +
                        "\n" +
                        "       System.out.println(\\\"割り算の結果: \\\" + divisionResult);\\n\" +\n" +
                        "    }\n" +

                        "   private static int add(int num1, int num2) {\n" +
                        "       // TODO: num1 と num2 の足し算を行い、resultに設定して返却してください" +
                        "       return result;\n" +
                        "   }\n" +
                        "\n" +
                        "// TODO: 引数に整数型の値を2つ取り、引き算を行って返却する「sub」メソッドを作成してください\n" +
                        "// TODO: 引数に整数型の値を2つ取り、掛け算を行って返却する「multi」メソッドを作成してください\n" +
                        "// TODO: 引数に整数型の値を2つ取り、割り算を行って返却する「division」メソッドを作成してください\n" +
                        "}";

        // -----------------------------
        // GUI コンポーネント
        // -----------------------------
        codeArea = new JTextArea(template);
        outputArea = new JTextArea();
        errorArea = new JTextArea();

        outputArea.setEditable(false);
        errorArea.setEditable(false);

        runButton = new JButton("コンパイルして実行");
        runButton.addActionListener(this);

        JScrollPane codeScroll = new JScrollPane(codeArea);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        JScrollPane errorScroll = new JScrollPane(errorArea);

        JSplitPane topBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScroll, outputScroll);
        topBottom.setDividerLocation(350);

        JSplitPane allSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topBottom, errorScroll);
        allSplit.setDividerLocation(550);

        add(runButton, BorderLayout.NORTH);
        add(allSplit, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // ソースコード保存
            File file = new File("UserCode.java");
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(codeArea.getText());
            }

            // コンパイル
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            int result = compiler.run(null, null, err, file.getPath());

            if (result != 0) {
                errorArea.setText("コンパイルエラー:\n" + err.toString());
                outputArea.setText("");
                return;
            }

            errorArea.setText("");

            // 実行（Shift_JIS で読み取る）
            Process p = Runtime.getRuntime().exec("java UserCode");
            p.waitFor();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), "Shift_JIS")
            );

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            outputArea.setText(sb.toString());

        } catch (Exception ex) {
            errorArea.setText("エラー: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Calculator();
    }
}
