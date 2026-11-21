package task06.part1;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task06Part1 extends TaskEditorFrame {

    public Task06Part1() {
        super("課題⑥-1：クラスの構造を理解する", 1100, 800, "Main");
        addSourceEditor("Main.java", getDefaultMainCode());
        addSourceEditor("Human.java", getDefaultHumanCode());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: クラスとは、関連する複数のフィールド（データ）をひとまとめにするための型です。\n"
                + "この課題では、Human クラスに 名前(name) と 年齢(age) というフィールドを定義します。\n\n"
                + "実装ヒント:\n"
                + "- private String name = \"\"; と private int age = 0; をフィールドとして定義してください。\n"
                + "- フィールドは、クラスの body の中に定義します。";
    }

    @Override
    protected String getHelpImagePath() {
        return "task06_part1.png";
    }

    private static String getDefaultHumanCode() {
        return "// Human クラスは名前と年齢を保持します。\n"
                + "\n"
                + "public class Human {\n"
                + "\n"
                + "    // TODO: 名前を保持するフィールドです。名前を変更してみてください\n"
                + "    private String name = \"\";\n"
                + "\n"
                + "    // TODO: 年齢を保持するフィールドです。年齢を変更してみてください\n"
                + "    private int age = 0;\n"
                + "\n"
                + "    // このクラスは名前と年齢というデータを持ちます。\n"
                + "\n"
                + "    public void print() {\n"
                + "        System.out.println(\"名前: \" + name + \", 年齢: \" + age);\n"
                + "    }\n"
                + "}\n";
    }

    private static String getDefaultMainCode() {
        return "// メイン処理\n"
                + "// この課題では、Human クラスの構造を理解します。\n"
                + "\n"
                + "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "\n"
                + "        // Human クラスのインスタンスを作成\n"
                + "        Human human = new Human();\n"
                + "        System.out.println(\"Human クラスを作成しました\");\n"
                + "        \n"
                + "        human.print();\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task06Part1::new);
    }
}
