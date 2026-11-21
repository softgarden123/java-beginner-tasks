package task07.part3;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task07Part3 extends TaskEditorFrame {

    public Task07Part3() {
        super("課題⑦-3：コンストラクタとオーバーロードの結果を出力する", 1100, 800, "Main");
        addSourceEditor("Main.java", getDefaultMainCode());
        addSourceEditor("Human.java", getDefaultHumanCode());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: この課題は、コンストラクタとオーバーロードを組み合わせて、フィールドの変更結果を表示する練習です。\n"
                + "showInfo() メソッドを作成して、setHumanInfo() でフィールドを変更するたびに値を表示します。\n\n"
                + "実装ヒント:\n"
                + "- public void showInfo() { System.out.println(...); } でフィールド値を表示します。\n"
                + "- this.name と this.age でフィールドにアクセスして、値を出力してください。";
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
                + "    public void setHumanInfo(String name) {\n"
                + "        this.name = name;\n"
                + "    }\n"
                + "\n"
                + "    public void setHumanInfo(int age) {\n"
                + "        this.age = age;\n"
                + "    }\n"
                + "\n"
                + "    public void setHumanInfo(String name, int age) {\n"
                + "        this.name = name;\n"
                + "        this.age = age;\n"
                + "    }\n"
                + "\n"
                + "    // TODO: フィールドの値を使って名前と年齢を表示する showInfo() メソッドを作成してください\n"
                + "}\n";
    }

    private static String getDefaultMainCode() {
        return "// メイン処理\n"
                + "// この課題では、コンストラクタとオーバーロードの結果をフィールドから出力します。\n"
                + "\n"
                + "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "\n"
                + "        Human human = new Human(\"Sato.y\", 35);\n"
                + "        human.showInfo();\n"
                + "\n"
                + "        human.setHumanInfo(\"Ito.h\");\n"
                + "        human.showInfo();\n"
                + "\n"
                + "        human.setHumanInfo(25);\n"
                + "        human.showInfo();\n"
                + "\n"
                + "        human.setHumanInfo(\"Sato.y\", 35);\n"
                + "        human.showInfo();\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task07Part3::new);
    }
}
