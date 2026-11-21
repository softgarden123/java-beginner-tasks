package task06.part3;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task06Part3 extends TaskEditorFrame {

    public Task06Part3() {
        super("課題⑥-3：setter を作成する", 1100, 800, "Main");
        addSourceEditor("Main.java", getDefaultMainCode());
        addSourceEditor("Human.java", getDefaultHumanCode());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: setter メソッドは、オブジェクトのフィールド値を外部から変更するためのメソッドです。\n"
                + "例: human.setName(\"新しい名前\") のように呼び出して、名前を変更します。\n\n"
                + "実装ヒント:\n"
                + "- Human クラスに setName(String name) と setAge(int age) を作成しましょう。\n"
                + "- 引数で受け取った値をフィールドに反映し、設定後の値を標準出力へ表示してください。\n"
                + "- this は『このオブジェクト自身』を指し、this.name はそのオブジェクトの name フィールドを意味します。\n"
                + "- System.out.println を書かない場合は実行結果に『(出力なし)』と表示されます。";
    }

    private static String getDefaultHumanCode() {
        return "// Human クラスは名前と年齢を保持します。\n"
                + "\n"
                + "public class Human {\n"
                + "\n"
                + "    private String name = \"\";\n"
                + "    private int age = 0;\n"
                + "\n"
                + "    // TODO: 名前を変更する setName メソッドを作成してください\n"
                + "    public void setName(String name) {\n"
                + "        // TODO: 引数 name を name フィールドへ反映する\n"
                + "        System.out.println(\"名前: \" + this.name);\n"
                + "    }\n"
                + "\n"
                + "    // TODO: 年齢を変更する setAge メソッドを作成してください\n"
                + "    public void setAge(int age) {\n"
                + "        // TODO: 引数 age を age フィールドへ反映する\n"
                + "        System.out.println(\"年齢: \" + this.age);\n"
                + "    }\n"
                + "}\n";
    }

    private static String getDefaultMainCode() {
        return "// メイン処理\n"
            + "// setter メソッドを使って Human の情報を変更します。\n"
            + "// 実装後は設定した名前と年齢が出力されることを確認しましょう。\n"
                + "\n"
                + "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "\n"
                + "        Human human = new Human();\n"
                + "        human.setName(\"Ito.h\");\n"
                + "        human.setAge(25);\n"
                + "\n"
                + "        // 期待される出力例:\n"
                + "        // 名前: Ito.h\n"
                + "        // 年齢: 25\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task06Part3::new);
    }
}
