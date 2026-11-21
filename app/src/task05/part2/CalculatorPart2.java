package task05.part2;

import javax.swing.*;
import shared.ui.SingleFileTaskFrame;

public class CalculatorPart2 extends SingleFileTaskFrame {

    public CalculatorPart2() {
        super("課題⑤-2：メソッドの呼び出しを理解する", 900, 700);
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
        return "目的: 既に用意された add メソッドを main から呼びだす方法を学びます。\n"
                + "ヒント: 例えば 「メソッド名(第１引数, 第２引数);」 のように呼び出します。\n"
                + "戻り値を受け取ったら System.out.println(result); で表示します。";
    }
    @Override
    protected String getHelpImagePath() {
        return "task05_part2.png";
    }

    private static String getTemplate() {
        return "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: 下のメソッドを呼び出して、結果を変数に入れてから出力してください。\n"
                + "        // ここを実装してください\n"
                + "        \n"
                + "    }\n\n"
                + "    private static int add(int num1, int num2) {\n"
                + "        return num1 + num2;\n"
                + "    }\n\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorPart2::new);
    }
}
