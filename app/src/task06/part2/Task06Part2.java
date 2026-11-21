package task06.part2;

import javax.swing.*;
import shared.ui.TaskEditorFrame;

public class Task06Part2 extends TaskEditorFrame {

    public Task06Part2() {
        super("課題⑥-2：インスタンス化を理解する", 1100, 800, "Main");
        addSourceEditor("Main.java", getDefaultMainCode());
        addSourceEditor("Human.java", getDefaultHumanCode());
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getHelpText() {
        return "目的: クラスのインスタンス化とは、クラスを基に具体的なオブジェクト（インスタンス）を作成することです。\n"
                + "この課題では、Human を new した瞬間にコンストラクタの表示が出るので、作成できたことを確認できます。\n\n"
                + "実装ヒント:\n"
                + "- new キーワードの後に Human() と書いてインスタンスを作成します。\n"
                + "- 作成されたことは、Human クラスのコンストラクタで表示されるメッセージで確認できます。";
    }

    private static String getDefaultHumanCode() {
        return "// Human クラスは名前と年齢を保持します。\n"
                + "\n"
                + "public class Human {\n"
                + "\n"
                + "    private String name = \"\";\n"
                + "    private int age = 0;\n"
                + "\n"
                + "    public Human() {\n"
                + "        System.out.println(\"Human インスタンスを作成しました\");\n"
                + "    }\n"
                + "\n"
                + "    // このクラスは Human 型のオブジェクトを作るための型です。\n"
                + "}\n";
    }

    private static String getDefaultMainCode() {
        return "// メイン処理\n"
                + "// Human クラスのインスタンスを作成する練習をします。\n"
                + "\n"
                + "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "\n"
                + "        // TODO: Human のインスタンスを作成してください\n"
                + "        Human human;\n"
                + "\n"
                + "        // Human を new すると、作成メッセージが表示されます。\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task06Part2::new);
    }
}
