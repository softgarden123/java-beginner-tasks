package task03.part1;

import javax.swing.*;
import shared.ui.SingleFileTaskFrame;

public class ConditionsPart1 extends SingleFileTaskFrame {

    public ConditionsPart1() {
        super("課題③-1：if / else if / else の分岐理解", 900, 700);
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
        return "目的: if / else if / else を使い分岐処理を実装します。\n\n"
                + "条件:\n"
                + "実装ヒント:\n"
                + "if: 最初に判定したい条件を書きます。条件が true のときだけ実行されます。\n"
                + "else if: 最初の if が false の場合に、追加の条件を順番に判定します。\n"
                + "else: どの if / else if にも当てはまらなかった場合の処理を書きます。\n\n"
                + "書き方の例:\n"
                + "if (条件1) {\n"
                + "    処理A;\n"
                + "} else if (条件2) {\n"
                + "    処理B;\n"
                + "} else {\n"
                + "    それ以外の処理;\n"
                + "}";
    }

    private static String getTemplate() {
        return "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: 年齢を表す変数 age を int 型で宣言し、好きな値を入れてください\n"
                + "        （ここにageを宣言）\n\n"
                + "        String result;\n\n"
                + "        // --------------------------------\n"
                + "        // ifとelse ifを用いて以下の分岐を実装してください。\n"
                + "        // --------------------------------\n\n"
                + "        // ① age が 0 未満なら「不正な値です」\n"
                + "        // ② age が 20 未満なら「未成年です」\n"
                + "        // ③ age が 65 未満なら「成人です」\n"
                + "        // ④ それ以外は「高齢者です」\n\n"
                + "        // TODO: ここに if / else if / else の分岐を実装してください\n\n"
                + "        // --------------------------------\n"
                + "        // 結果表示\n"
                + "        // ※以下は変更しないこと\n"
                + "        // --------------------------------\n\n"
                + "        System.out.println(\"年齢: \" + age);\n"
                + "        System.out.println(\"判定: \" + result);\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConditionsPart1::new);
    }
}
