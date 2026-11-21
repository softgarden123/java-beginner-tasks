package task08.part2;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task08Part2 extends TaskEditorFrame {

    public Task08Part2() {
        super("課題⑧-2：ArrayListを理解する", 900, 700, "UserCode");
        addSourceEditor("UserCode.java", getTemplate());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: ArrayList は、配列と異なり、サイズを動的に変更できるデータ構造です。\n"
                + "要素の追加・削除が簡単にできます。\n"
                + "例: ArrayList<String> nameList = new ArrayList<>(); のように作成し、\n"
                + "nameList.add(\"要素\"); で要素を追加、nameList.get(0) でアクセスします。\n\n"
                + "実装ヒント:\n"
                + "- import java.util.ArrayList; で ArrayList クラスをインポートしてください。\n"
                + "- ArrayList<型> 変数名 = new ArrayList<>(); で作成します。\n"
                + "- add() で追加、get(インデックス) でアクセス、size() でサイズを取得できます。";
    }

    @Override
    protected String getRunButtonLabel() {
        return "コンパイルして実行";
    }

    private String getTemplate() {
        return "import java.util.ArrayList;\n"
                + "\n"
                + "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: ArrayList<String> を作成してください\n"
                + "        ArrayList<String> nameList = new ArrayList<>();\n"
                + "\n"
                + "        nameList.add(\"佐藤\");\n"
                + "        nameList.add(\"田中\");\n"
                + "        nameList.add(\"鈴木\");\n"
                + "\n"
                + "        System.out.println(\"最初の名前は\" + nameList.get(0) + \"です。\");\n"
                + "        System.out.println(\"リストのサイズは\" + nameList.size() + \"です。\");\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task08Part2::new);
    }
}
