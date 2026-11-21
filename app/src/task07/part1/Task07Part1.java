package task07.part1;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task07Part1 extends TaskEditorFrame {

    public Task07Part1() {
        super("課題⑦-1：コンストラクタを理解する", 1100, 800, "Main");
        addSourceEditor("Main.java", getDefaultMainCode());
        addSourceEditor("Human.java", getDefaultHumanCode());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: コンストラクタは、インスタンスが生成されるときに自動的に呼び出される特別なメソッドです。\n"
                + "フィールドの初期化（最初の値をセット）に使います。\n"
                + "例: new Human(\"Sato.y\", 35) のように引数を渡すと、コンストラクタが呼ばれます。\n\n"
                + "実装ヒント:\n"
                + "- public Human(String name, int age) { ... } のように、クラス名と同じ名前で定義します。\n"
                + "- コンストラクタ内で this.name = name; と this.age = age; と書いてフィールドを初期化します。";
    }

    private static String getDefaultHumanCode() {
        return "// Human クラスは名前と年齢の情報を持ちます。\n"
                + "\n"
                + "public class Human {\n"
                + "\n"
                + "    private String name = \"\";\n"
                + "    private int age = 0;\n"
                + "\n"
                + "    // TODO: フィールドを初期化するコンストラクタ Human(String name, int age) を作成してください\n"
                + "}\n";
    }

    private static String getDefaultMainCode() {
        return "// メイン処理\n"
                + "// この課題では、コンストラクタを理解します。\n"
                + "\n"
                + "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "\n"
                + "        // TODO: 引数 name, age を渡して Human のインスタンスを作成してください\n"
                + "        Human human = new Human(\"Sato.y\", 35);\n"
                + "\n"
                + "        System.out.println(\"Human を作成しました。\");\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task07Part1::new);
    }
}
