package task01;

import javax.swing.*;
import shared.ui.SingleFileTaskFrame;

public class HelloWorld extends SingleFileTaskFrame {

    public HelloWorld() {
        super("課題①：Hello World", 900, 700);
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
        return "目的: 指定された文字列を画面に表示する\n\n"
            + "ヒント: 文字列はダブルクォート (\") で囲みます。\n"
            + "ヒント: System.out.println() で1行出力できます。";
    }

    private static String getTemplate() {
        return "public class UserCode {\n"
                + "    public static void main(String[] args) {\n"
                + "        // TODO: System.out.println を使用して \"Hello, World!\" を出力してください\n"
                + "    }\n"
                + "}\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HelloWorld::new);
    }
}
