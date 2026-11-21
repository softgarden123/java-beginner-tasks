package task04;

import javax.swing.*;
import shared.ui.SingleFileTaskFrame;

public class Loop extends SingleFileTaskFrame {

    public Loop() {
        super("課題④：繰り返し処理（コード実行）", 900, 700);
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
        return "目的: for文とwhile文を使い繰り返し処理を実装します。\n\n"
                + "実装ヒント:\n"
                + "for文: 繰り返す回数が決まっている場合に使用します。\n"
                + "while文: 条件がtrueの間繰り返し処理を実行します。\n\n"
                + "書き方の見本:\n"
                + "for (初期化; 条件式; 更新式) {\n"
                + "    // 繰り返したい処理\n"
                + "}\n\n"
                + "初期化;\n"
                + "while (条件式) {\n"
                + "    // 繰り返したい処理\n"
                + "    更新処理;\n"
                + "}";
    }

    private static String getTemplate() {
        return "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: 繰り返す回数を表す変数 count を int 型で宣言し、好きな値を入れてください\n"
                + "        int count = 5;\n\n"
                + "        // ------------------------------\n"
                + "        // for文を用いて1～countまで1ずつ繰り上げながら出力してください\n"
                + "        // ※出力はSystem.out.println()を利用してください\n"
                + "        // ------------------------------\n\n"
                + "        // ------------------------------\n"
                + "        // while文を用いて1～countまで1ずつ繰り上げながら出力してください\n"
                + "        // ※出力はSystem.out.println()を利用してください\n"
                + "        // ------------------------------\n\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Loop::new);
    }
}
