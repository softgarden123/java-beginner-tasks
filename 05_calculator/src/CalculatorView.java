import javax.swing.*;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class CalculatorView {
    private static final String ERROR_MESSAGE = "入力が正しくありません。";

    public static void main(String[] args) {
        JFrame frame = new JFrame("簡易電卓");

        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JTextField inputField1 = new JTextField(5);
        JTextField inputField2 = new JTextField(5);

        JTextArea outputArea = new JTextArea();

        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("足し算");
        JButton subtractButton = new JButton("引き算");
        JButton multiplyButton = new JButton("掛け算");
        JButton divideButton = new JButton("割り算");
        setActionListener(addButton, inputField1, inputField2, outputArea, Type.ADD);
        setActionListener(subtractButton, inputField1, inputField2, outputArea, Type.SUBTRACT);
        setActionListener(multiplyButton, inputField1, inputField2, outputArea, Type.MULTIPLY);
        setActionListener(divideButton, inputField1, inputField2, outputArea, Type.DIVIDE);
        buttonPanel.add(addButton);
        buttonPanel.add(subtractButton);
        buttonPanel.add(multiplyButton);
        buttonPanel.add(divideButton);

        outputArea.setEditable(false);

        // 入力パネルのレイアウト
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("数字1:"));
        inputPanel.add(inputField1);
        inputPanel.add(new JLabel("数字2:"));
        inputPanel.add(inputField2);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void setActionListener(JButton button, JTextField inputField1, JTextField inputField2, JTextArea outputArea, Type type) {
        button.addActionListener(e -> {
            try {
                int input1 = Integer.parseInt(inputField1.getText());
                int input2 = Integer.parseInt(inputField2.getText());

                int result = Calculator.calculate(input1, input2, type);
                outputArea.setText(performAddition(result));
            } catch (NumberFormatException ex) {
                outputArea.setText(ERROR_MESSAGE);
            }
        });
    }

    private static String performAddition(int result) {
        return "結果: " + result;
    }
}