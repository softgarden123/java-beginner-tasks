package task05.part3;

import javax.swing.*;
import shared.ui.SingleFileTaskFrame;

public class CalculatorPart3 extends SingleFileTaskFrame {

    public CalculatorPart3() {
        super("課題⑤-3：メソッドの作成と呼び出し", 900, 700);
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
        return "目的: 自分で add, sub, multi, division のメソッドを定義し、\n"
                + "そのメソッドを main から呼び出して結果を表示する練習です。\n\n"
                + "補足: 四則演算は +, -, *, / の記号を用いても計算できます。\n\n"
                + "ヒント: まず private static int add(int a, int b) のようなメソッドを作り、\n"
                + "内部で return a + b; を行います。\n"
                + "その後、main で int result = add(3, 4); と呼び出して表示します。";
    }

    private static String getTemplate() {
        return "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: 以下のメソッドを自分で作成し、呼び出して結果を表示してください。\n"
                + "        // - add(int a, int b) : 足し算\n"
                + "        // - sub(int a, int b) : 引き算\n"
                + "        // - multi(int a, int b): 掛け算\n"
                + "        // - division(int a, int b): 割り算\n\n"
                + "        // ここに呼び出しを記述してください\n"
                + "    }\n\n"
                + "    // ここにメソッドを定義してください\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorPart3::new);
    }
}
