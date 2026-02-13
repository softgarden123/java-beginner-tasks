import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    private JTextArea itemArea;
    private JTextArea cartArea;
    private JTextArea mainArea;
    private JTextArea outputArea;

    public Application() {
        setTitle("簡易レジアプリ");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel codePanel = new JPanel(new GridLayout(1, 3));

        itemArea = new JTextArea(getDefaultItemCode());
        cartArea = new JTextArea(getDefaultCartCode());
        mainArea = new JTextArea(getDefaultMainCode());

        codePanel.add(createScrollPanel("Item.java", itemArea));
        codePanel.add(createScrollPanel("Cart.java", cartArea));
        codePanel.add(createScrollPanel("Main.java", mainArea));

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JButton runButton = new JButton("実行");
        bottomPanel.add(runButton, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
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

        String itemCode = itemArea.getText();
        String cartCode = cartArea.getText();
        String mainCode = mainArea.getText();

        UserCodeExecutor executor = new UserCodeExecutor();
        executor.execute(mainCode, itemCode, cartCode, text -> {
            outputArea.append(text + "\n");
        });
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.home"));

        SwingUtilities.invokeLater(() -> {
            new Application().setVisible(true);
        });
    }

    private static String getDefaultItemCode() {
        return "// 商品を表すクラス\n" +
                "// 名前(name) と 価格(price) をフィールドとして定義してください。\n" +
                "// コンストラクタで name と price を受け取り、フィールドにセットします。\n" +
                "// getName(), getPrice() の getter を作成してください。\n" +
                "// toString() をオーバーライドして「りんご 100円」のように表示できるようにします。\n" +
                "\n" +
                "public class Item {\n" +
                "\n" +
                "    // フィールド（name, price）を定義\n" +
                "\n" +
                "    // コンストラクタを作成\n" +
                "\n" +
                "    // getter を作成（getName, getPrice）\n" +
                "\n" +
                "    // toString() をオーバーライド\n" +
                "}\n";

    }

    private static String getDefaultCartCode() {
        return "import java.util.ArrayList;\n" +
                "\n" +
                "// カート（買い物かご）を表すクラスです。\n" +
                "// ArrayList<Item> を使って商品を複数管理します。\n" +
                "// addItem(Item item) で商品を追加します。\n" +
                "// showItems() でカート内の商品をすべて表示します。\n" +
                "// getTotal() で合計金額を計算して返します。\n" +
                "\n" +
                "public class Cart {\n" +
                "\n" +
                "    // ArrayList<Item> をフィールドとして定義\n" +
                "\n" +
                "    // 商品を追加する addItem(Item item) メソッドを作成\n" +
                "\n" +
                "    // カート内の商品を表示する showItems() メソッドを作成\n" +
                "\n" +
                "    // 合計金額を返す getTotal() メソッドを作成\n" +
                "}\n";

    }

    private static String getDefaultMainCode() {
        return "import java.util.Scanner;\n" +
                "\n" +
                "// 簡易レジアプリのメイン処理です。\n" +
                "// 商品一覧を作り、ユーザに番号で選んでもらい、カートに追加していきます。\n" +
                "// 0 が入力されたら会計へ進みます。\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "\n" +
                "        System.out.println(\"=== 簡易レジへようこそ ===\");\n" +
                "\n" +
                "        // 商品一覧を作成（Item の配列または ArrayList）\n" +
                "        // 例：りんご100円、バナナ150円、みかん80円\n" +
                "\n" +
                "        // Cart のインスタンスを作成\n" +
                "\n" +
                "        Scanner scanner = new Scanner(System.in);\n" +
                "\n" +
                "        while (true) {\n" +
                "            // 商品一覧を表示\n" +
                "\n" +
                "            // 番号を入力してもらう\n" +
                "\n" +
                "            // 0 が入力されたら break で会計へ\n" +
                "\n" +
                "            // 入力された番号に応じて商品をカートに追加\n" +
                "\n" +
                "            // 不正な番号ならメッセージを表示\n" +
                "        }\n" +
                "\n" +
                "        // カート内の商品を表示\n" +
                "\n" +
                "        // 合計金額を表示\n" +
                "\n" +
                "        System.out.println(\"ご利用ありがとうございました！\");\n" +
                "    }\n" +
                "}\n";

    }
}