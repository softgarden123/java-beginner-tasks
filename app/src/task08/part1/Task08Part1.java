package task08.part1;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task08Part1 extends TaskEditorFrame {

    public Task08Part1() {
        super("課題⑧-1：配列を理解する", 900, 700, "UserCode");
        addSourceEditor("UserCode.java", getTemplate());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: 配列とは、同じ型の複数の値を一つの変数で管理するデータ構造です。\n"
                + "例: String[] fruitArray = new String[3]; のように、3個の文字列を格納できる配列を作成します。\n"
                + "各要素には fruitArray[0]、fruitArray[1]、fruitArray[2] のようにインデックス（番号）でアクセスします。\n\n"
                + "実装ヒント:\n"
                + "- String[] fruitArray = new String[3]; で配列を作成します。\n"
                + "- fruitArray[0] = \"値\"; のようにインデックスを指定して値を代入します。\n"
                + "- fruitArray.length で配列のサイズを取得できます。";
    }

    @Override
    protected String getRunButtonLabel() {
        return "コンパイルして実行";
    }

    private String getTemplate() {
        return "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: サイズが3のString配列を作成してください\n"
                + "        String[] fruitArray = new String[3];\n"
                + "\n"
                + "        fruitArray[0] = \"リンゴ\";\n"
                + "        fruitArray[1] = \"ブドウ\";\n"
                + "        fruitArray[2] = \"レモン\";\n"
                + "\n"
                + "        System.out.println(\"1つ目の果物は\" + fruitArray[0] + \"です。\");\n"
                + "        System.out.println(\"2つ目の果物は\" + fruitArray[1] + \"です。\");\n"
                + "        System.out.println(\"3つ目の果物は\" + fruitArray[2] + \"です。\");\n"
                + "        System.out.println(\"配列のサイズは\" + fruitArray.length + \"です。\");\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task08Part1::new);
    }
}
