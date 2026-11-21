package task03.part2;

import javax.swing.*;
import shared.ui.SingleFileTaskFrame;

public class ConditionsPart2 extends SingleFileTaskFrame {

    public ConditionsPart2() {
        super("課題③-2：switch / case の分岐理解", 900, 700);
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
        return "目的: switch / case を使い分岐処理を実装します。\n\n"
                + "実装ヒント:\n"
                + "switch: 1つの値に対して、どの case に一致するかを判定します。\n"
                + "case: 一致したときに実行する処理を書きます。\n"
                + "break: その case の処理を終えて switch を抜けます。\n"
                + "注意: break を書かないと次の case の処理も続けて実行されます。\n"
                + "default: どの case にも一致しないときの処理を書きます。\n\n"
                + "書き方の見本:\n"
                + "switch (値) {\n"
                + "    case パターンA:\n"
                + "        処理A;\n"
                + "        break;\n"
                + "    case パターンB:\n"
                + "        処理B;\n"
                + "        break;\n"
                + "    default:\n"
                + "        それ以外の処理;\n"
                + "}";
    }

    private static String getTemplate() {
        return "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: モードを表す変数 mode を int 型で宣言し、好きな値を入れてください\n"
                + "        （ここにモードを宣言）\n\n"
                + "        String message;\n\n"
                + "        // --------------------------------\n"
                + "        // switch case を用いて以下の分岐を実装してください。\n"
                + "        // --------------------------------\n\n"
                + "        // ① mode が 0 なら「出生おめでとう！」\n"
                + "        // ② mode が 1 なら「成人おめでとう！」\n"
                + "        // ③ mode が 2 なら「就職おめでとう！」\n"
                + "        // ④ それ以外は「お疲れ様でした！」\n\n"
                + "        // TODO: ここに switch / case の分岐を実装してください\n\n"
                + "        // --------------------------------\n"
                + "        // 結果表示\n"
                + "        // ※以下は変更しないこと\n"
                + "        // --------------------------------\n\n"
                + "        System.out.println(\"モード: \" + mode);\n"
                + "        System.out.println(\"メッセージ: \" + message);\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConditionsPart2::new);
    }
}
