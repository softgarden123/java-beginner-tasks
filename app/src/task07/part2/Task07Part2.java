package task07.part2;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task07Part2 extends TaskEditorFrame {

    public Task07Part2() {
        super("課題⑦-2：オーバーロードを理解する", 1100, 800, "Main");
        addSourceEditor("Main.java", getDefaultMainCode());
        addSourceEditor("Human.java", getDefaultHumanCode());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: オーバーロードとは、同じ名前のメソッドを複数定義することです。\n"
                + "引数の型や数が異なれば、同じ名前でも別のメソッドと認識されます。\n"
                + "例: setHumanInfo(String name) と setHumanInfo(int age) は同じ名前でも異なるメソッドです。\n\n"
                + "実装ヒント:\n"
                + "- setHumanInfo(String name)、setHumanInfo(int age)、setHumanInfo(String name, int age) の3つを作成してください。\n"
                + "- 呼び出し時に渡された引数の型で、どのメソッドが実行されるかが自動判定されます。";
    }

    private static String getDefaultHumanCode() {
        return "// Human クラスは名前と年齢の情報を持ちます。\n"
                + "\n"
                + "public class Human {\n"
                + "\n"
                + "    private String name = \"\";\n"
                + "    private int age = 0;\n"
                + "\n"
                + "    public Human(String name, int age) {\n"
                + "        this.name = name;\n"
                + "        this.age = age;\n"
                + "    }\n"
                + "\n"
                + "    // TODO: 同じ名前のメソッド setHumanInfo を複数定義して、引数の型や数を変えてください\n"
                + "    // 1) setHumanInfo(String name)\n"
                + "    // 2) setHumanInfo(int age)\n"
                + "    // 3) setHumanInfo(String name, int age)\n"
                + "}\n";
    }

    private static String getDefaultMainCode() {
        return "// メイン処理\n"
                + "// この課題では、オーバーロードを理解します。\n"
                + "\n"
                + "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "\n"
                + "        Human human = new Human(\"Sato.y\", 35);\n"
                + "\n"
                + "        // TODO: setHumanInfo を呼び出して、名前や年齢を変更してください\n"
                + "        human.setHumanInfo(\"Ito.h\");\n"
                + "        human.setHumanInfo(25);\n"
                + "        human.setHumanInfo(\"Sato.y\", 35);\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task07Part2::new);
    }
}
