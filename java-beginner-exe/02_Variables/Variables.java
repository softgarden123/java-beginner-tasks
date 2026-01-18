import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Variables extends JFrame implements ActionListener {

    private JTextArea codeArea;
    private JTextArea outputArea;
    private JTextArea errorArea;
    private JButton runButton;

    public Variables() {
        setTitle("課題②：変数とデータ型（コード実行）");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // -----------------------------
        // 初期テンプレート（型を意識させる構造）
        // -----------------------------
        String template =
                "public class UserCode {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        // --------------------------------\n" +
                        "        // ここに変数を宣言、値を入力して以下の式の処理が通るように実装してください\n" +
                        "        // --------------------------------\n\n\n" +

                        "        // --------------------------------\n" +
                        "        // ※以下は変更しないこと\n" +
                        "        // --------------------------------\n" +

                        "        // ① 年齢に10を足して futureAge に代入（int）\n" +
                        "        int futureAge = age + 10;\n\n" +

                        "        // ② 身長(cm)をメートルに変換して heightMeter に代入（double）\n" +
                        "        double heightMeter = height / 100;\n\n" +

                        "        // ③ 名前の文字数を nameLength に代入（int）\n" +
                        "        int nameLength = name.length();\n\n" +

                        "        // ④ 学生かどうかでメッセージを切り替える（boolean → if）\n" +
                        "        String studentMessage;\n" +
                        "        if (isStudent) {\n" +
                        "            studentMessage = \"学生です\";\n" +
                        "        } else {\n" +
                        "            studentMessage = \"学生ではありません\";\n" +
                        "        }\n\n" +

                        "        // --------------------------------\n" +
                        "        // 結果出力\n\n" +
                        "        // --------------------------------\n" +
                        "        System.out.println(\"10年後の年齢: \" + futureAge);\n" +
                        "        System.out.println(\"身長(メートル): \" + heightMeter);\n" +
                        "        System.out.println(\"名前の文字数: \" + nameLength);\n" +
                        "        System.out.println(\"学生判定: \" + studentMessage);\n" +
                        "    }\n" +
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

        // 上：コード入力
        JScrollPane codeScroll = new JScrollPane(codeArea);

        // 中：実行結果
        JScrollPane outputScroll = new JScrollPane(outputArea);

        // 下：エラー表示
        JScrollPane errorScroll = new JScrollPane(errorArea);

        // 上下分割
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
            // -----------------------------
            // ソースコードを保存
            // -----------------------------
            File file = new File("UserCode.java");
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(codeArea.getText());
            }

            // -----------------------------
            // コンパイル
            // -----------------------------
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            int result = compiler.run(null, null, err, file.getPath());

            if (result != 0) {
                errorArea.setText("コンパイルエラー:\n" + err.toString());
                outputArea.setText("");
                return;
            }

            errorArea.setText("");

            // -----------------------------
            // 実行
            // -----------------------------
            Process p = Runtime.getRuntime().exec("java UserCode");
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "Shift_JIS"));
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
        new Variables();
    }
}