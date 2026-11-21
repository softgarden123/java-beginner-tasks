package task06.part4;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task06Part4 extends TaskEditorFrame {

    public Task06Part4() {
        super("課題⑥-4：フィールド情報を出力する", 1100, 800, "Main");
        addSourceEditor("Main.java", getDefaultMainCode());
        addSourceEditor("Human.java", getDefaultHumanCode());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: getter メソッド（またはデータ表示メソッド）は、オブジェクトのフィールド値を取得・表示するためのメソッドです。\n"
                + "この課題では showInfo() メソッドでフィールドの値を表示します。\n\n"
                + "実装ヒント:\n"
                + "- public void showInfo() { ... } のように定義します。\n"
                + "- this.name や this.age でフィールドにアクセスして値を表示してください。";
    }

    private static String getDefaultHumanCode() {
        return "// Human クラスは名前と年齢を保持します。\n"
                + "\n"
                + "public class Human {\n"
                + "\n"
                + "    private String name = \"\";\n"
                + "    private int age = 0;\n"
                + "\n"
                + "    public void setName(String name) {\n"
                + "        this.name = name;\n"
                + "    }\n"
                + "\n"
                + "    public void setAge(int age) {\n"
                + "        this.age = age;\n"
                + "    }\n"
                + "\n"
                + "    // TODO: フィールドの値を表示する showInfo メソッドを作成してください\n"
                + "    public void showInfo() {\n"
                + "        // ここに name と age を表示する処理を書いてください\n"
                + "    }\n"
                + "}\n";
    }

    private static String getDefaultMainCode() {
        return "// メイン処理\n"
                + "// フィールドの情報を使って出力する練習です。\n"
                + "\n"
                + "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "\n"
                + "        Human human = new Human();\n"
                + "\n"
                + "        // showInfo メソッドを完成させると、ここで情報を表示できます。\n"
                + "        human.showInfo();\n"
                + "\n"
                + "        human.setName(\"Ito.h\");\n"
                + "        human.setAge(25);\n"
                + "\n"
                + "        // showInfo メソッドを完成させると、設定した値を表示できます。\n"
                + "        human.showInfo();\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task06Part4::new);
    }
}
