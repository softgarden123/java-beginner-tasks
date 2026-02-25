import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Array extends JFrame implements ActionListener {

    private JTextArea codeArea;
    private JTextArea outputArea;
    private JTextArea errorArea;
    private JButton runButton;

    public Array() {
        setTitle("課題⑧：メソッドの定義と呼び出し");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // -----------------------------
        // 初期テンプレート（条件分岐）
        // -----------------------------
        String template =
                """
                        import java.util.ArrayList;
                        public class UserCode {
                            public static void main(String[] args) {
                              // サイズが3の配列(String型)、fluteArrayを作成してください
                              
                              fluteArray[0] = "リンゴ";
                              fluteArray[1] = "ブドウ";
                              fluteArray[2] = "レモン";
                              
                              System.out.println("1つ目の果物は" + fluteArray[0] + "です。");
                              System.out.println("2つ目の果物は" + fluteArray[1] + "です。");
                              System.out.println("3つ目の果物は" + fluteArray[2] + "です。");
                              System.out.println("配列のサイズは" + fluteArray.length + "です。");
                              
                              // ArrayList(String型)、nameListを作成してください。
                              
                              // nameListに、任意の数の名前を入れてください。
                              // addメソッドを使用することで、オブジェクトを追加することができます。
                              
                              for (int i = 0; i < nameList.size(); i++) {
                                String name = nameList.get(i);
                                System.out.println(i + "人目の名称は" + name + "です");
                              }
                              System.out.println("名前のリストのサイズは" + nameList.size() + "です");                              
                            }
                        }
                        """;

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
        new Array();
    }
}
