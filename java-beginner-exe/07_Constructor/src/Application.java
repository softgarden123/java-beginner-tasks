import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    private JTextArea humanArea;
    private JTextArea mainArea;
    private JTextArea outputArea;

    public Application() {
        setTitle("コンストラクタとオーバーロード");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel codePanel = new JPanel(new GridLayout(1, 2));

        mainArea = new JTextArea(getDefaultMainCode());
        humanArea = new JTextArea(getDefaultHumanCode());
        // タブ幅をスペース4つ分に設定し、コード表示は等幅フォントにする
        Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        mainArea.setFont(mono);
        humanArea.setFont(mono);
        mainArea.setTabSize(4);
        humanArea.setTabSize(4);

        codePanel.add(createScrollPanel("Main.java", mainArea));
        codePanel.add(createScrollPanel("Human.java", humanArea));

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JButton runButton = new JButton("実行");
        bottomPanel.add(runButton, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        // 出力エリアもタブ幅を4にする（等幅にする必要があればコメントを外す）
        outputArea.setTabSize(4);
        // outputArea.setFont(mono);
        bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // サイズ調整用に上下分割
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codePanel, bottomPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(0.7);
        splitPane.setResizeWeight(0.7);

        add(splitPane, BorderLayout.CENTER);

        runButton.addActionListener(e -> runUserCode());
    }

    private JPanel createScrollPanel(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title), BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private void runUserCode() {
        outputArea.setText("");

        String mainCode = mainArea.getText();
        String humanCode = humanArea.getText();

        UserCodeExecutor executor = new UserCodeExecutor();
        // 実行はバックグラウンドスレッドで行い、出力は EDT で append する
        new Thread(() -> {
            executor.execute(mainCode, humanCode, text -> SwingUtilities.invokeLater(() -> {
                outputArea.append(text + "\n");
            }));
        }, "UserCode-Runner").start();
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.home"));

        SwingUtilities.invokeLater(() -> {
            new Application().setVisible(true);
        });
    }

    private static String getDefaultHumanCode() {
        return  "// 一人の年齢、名前の情報を持つクラス。\n" +
                "\n" +
                "public class Human {\n" +
                "\n" +
                "    private String name = \"\";\n" +
                "    private int age = 0;\n" +

                "    // フィールド変数を初期化するコンストラクタ Human(String name, int age) を作成\n" +
                "\n" +
                "    // 名前のみを変更する setHumanInfo(String name) メソッドを作成\n" +
                "\n" +
                "    // 年齢を変更する setHumanInfo(int age) メソッドを作成\n" +
                "\n" +
                "    // 名前、年齢を変更する setHumanInfo(String name, int age) メソッドを作成\n" +
                "\n" +
                "    // 年齢、名前を出力する showInfo() メソッドを作成\n" +
                "}\n";

    }

    private static String getDefaultMainCode() {
        return "// メイン処理\n" +
                "// この課題では、Mainを変更する必要はありません。" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "\n" +
                "        // Human のインスタンスを作成\n" +
                "        Human human = new Human(\"Sato.y\", 35);\n" +
                "\n" +
                "        // 年齢と名前を出力(コンストラクタで初期化した値を出力)\n" +
                "        human.showInfo();\n" +
                "\n" +
                "        // 年齢と名前を設定し、出力(個別で設定)\n" +
                "        human.setHumanInfo(\"Ito.h\");\n" +
                "        human.showInfo();\n" +
                "        human.setHumanInfo(25);\n" +
                "        human.showInfo();\n" +
                "\n" +
                "        // 年齢と名前を設定(一括で設定)\n" +
                "        human.setHumanInfo(\"Sato.y\", 35);\n" +
                "\n" +
                "        // 年齢と名前を出力\n" +
                "        human.showInfo();\n" +
                "    }\n" +
                "}\n";

    }
}