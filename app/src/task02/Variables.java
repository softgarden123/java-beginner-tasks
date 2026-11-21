package task02;

import javax.swing.*;
import shared.ui.SingleFileTaskFrame;

public class Variables extends SingleFileTaskFrame {

    public Variables() {
        super("課題②：変数とデータ型（コード実行）", 900, 700);
        buildUI();
        setVisible(true);
    }

    @Override
    protected String getInitialCode() {
        return getTemplate();
    }

    @Override
    protected String getRunButtonLabel() {
        return "コンパイルして実行";
    }

    @Override
    protected String getHelpText() {
        return "目的: 主要なデータ型の変数を宣言してデータを保存し、出力する\n\n"
                + "ヒント: 代入するにはイコール (=) を使用します。\n"
                + "ヒント: 変数は「入れ物」を作るイメージです。\n"
                + "ヒント: 書く順番は、型（何を入れるか）→ 名前（入れ物の名前）→ = → 値（入れる中身）です。\n"
                + "ヒント: 文の終わりにはセミコロン (;) を忘れずに付けます。\n"
                + "ヒント: 整数（int）には -10, 0, 25 のような整数が入ります。\n"
                + "ヒント: 小数（double）には 3.14, -0.5 のような小数が入ります。\n"
                + "ヒント: 真偽値（boolean）には true または false の2つだけが入ります。\n"
                + "ヒント: 1文字（char）には 'A', 'あ', '1' のように1文字だけが入ります。\n"
                + "ヒント: 文字列（String）には \"Hello\", \"田中\" のような文字の並びが入ります。\n"
                + "ヒント: 出力には System.out.println() を使用できます。\n\n"
                + "書き方の例題（1つ）:\n"
                + "整数を1つ宣言して出力してみましょう。\n"
                + "例: int score = 80;\n"
                + "例: System.out.println(score);";
    }

    @Override
    protected boolean hasErrorArea() {
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Variables::new);
    }

    private static String getTemplate() {
        return "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: 以下の種類の値を入れる変数を宣言し、値を入れてください\n"
                + "        // 整数\n"
                + "        // 小数\n"
                + "        // 真偽値\n"
                + "        // 1文字\n"
                + "        // 文字列\n\n"
                + "        // TODO: ここに各変数の宣言と代入を書く\n\n"
                + "        // TODO: System.out.println() を使って各変数を出力してください\n"
                + "    }\n"
                + "}\n";
    }
}
