package task08.part3;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task08Part3 extends TaskEditorFrame {

    public Task08Part3() {
        super("課題⑧-3：ArrayListとfor文の組み合わせ", 900, 700, "UserCode");
        addSourceEditor("UserCode.java", getTemplate());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: ArrayList と for 文を組み合わせて、複数のデータを繰り返し処理する練習です。\n"
                + "ArrayList に複数の要素を格納し、for 文で全要素にアクセスします。\n\n"
                + "実装ヒント:\n"
                + "- for (int i = 0; i < nameList.size(); i++) { ... } でループしてください。\n"
                + "- nameList.get(i) で i 番目の要素を取得します。\n"
                + "- size() メソッドで ArrayList のサイズを取得できます。";
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
                + "        ArrayList<String> nameList = new ArrayList<>();\n"
                + "\n"
                + "        // TODO: nameList に名前を3つ追加してください\n"
                + "        nameList.add(\"佐藤\");\n"
                + "        nameList.add(\"田中\");\n"
                + "        nameList.add(\"鈴木\");\n"
                + "\n"
                + "        for (int i = 0; i < nameList.size(); i++) {\n"
                + "            String name = nameList.get(i);\n"
                + "            System.out.println((i + 1) + \"人目の名称は\" + name + \"です\");\n"
                + "        }\n"
                + "\n"
                + "        System.out.println(\"名前のリストのサイズは\" + nameList.size() + \"です\");\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task08Part3::new);
    }
}
