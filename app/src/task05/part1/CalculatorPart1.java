package task05.part1;

import javax.swing.*;
import shared.ui.SingleFileTaskFrame;

public class CalculatorPart1 extends SingleFileTaskFrame {

    public CalculatorPart1() {
        super("課題⑤-1：メソッドの構造を理解する", 900, 700);
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
        return "目的: メソッドの構造（宣言の読み方）を理解します。\n\n"
            + "実際の例（番号付き）:\n"
            + "①private ②static ③int ④add(⑤int num1, ⑤int num2) { ... }\n\n"
            + "番号ごとの説明:\n"
            + "① private: 同じクラスの中からだけ呼び出せる指定です。\n"
            + "  （public にすると他のクラスからも呼び出しやすくなります）\n"
            + "② static: オブジェクトを作らなくても呼び出せる指定です。\n"
            + "③ int: 戻り値の型です。処理結果として整数を返すことを表します。\n"
            + "④ add: メソッド名です。何をする処理かが分かる名前を付けます。\n"
            + "⑤ int num1, int num2: 引数です。メソッドに渡す入力データです。\n\n"
            + "この課題では、宣言は用意されているので、メソッド本体の処理を完成させてください。";
    }

    @Override
    protected String getHelpImagePath() {
        return "task05_part1.png";
    }

    private static String getTemplate() {
        return "public class UserCode {\n"
            + "    public static void main(String[] args) {\n"
            + "        int result = add(3, 4);\n"
            + "        System.out.println(result);\n"
            + "    }\n\n"
            + "    private static int add(int num1, int num2) {\n"
            + "        // TODO: num1 と num2 の足し算を行い、resultに設定して返却してください\n"
            + "    }\n"
            + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorPart1::new);
    }
}
