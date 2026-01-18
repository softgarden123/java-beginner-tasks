import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Loop extends JFrame implements ActionListener {

    private JTextArea codeArea;
    private JTextArea outputArea;
    private JTextArea errorArea;
    private JButton runButton;

    public Loop() {
        setTitle("課題④：繰り返し処理（コード実行）");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // -----------------------------
        // 初期テンプレート（繰り返し処理）
        // -----------------------------
        String template =
                "public class UserCode {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        // TODO: 繰り返す回数を表す変数 count を int 型で宣言し、好きな値を入れてください\n" +
                        "        int count = 5;\n\n" +

                        "        // ------------------------------\n" +
                        "        // for文を用いて1～countまで1ずつ繰り上げながら出力してください\n" +
                        "        // ※出力はSystem.out.println()を利用してください\n" +
                        "        // ------------------------------\n\n\n" +

                        "        // ------------------------------\n" +
                        "        // while文を用いて1～countまで1ずつ繰り上げながら出力してください\n" +
                        "        // ※出力はSystem.out.println()を利用してください\n" +
                        "        // ------------------------------\n\n\n" +
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
        new Loop();
    }
}