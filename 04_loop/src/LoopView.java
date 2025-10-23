import javax.swing.*;

import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class LoopView {
    private static final String ERROR_MESSAGE = "入力が正しくありません。";

    public static void main(String[] args) {
        JFrame frame = new JFrame("繰り返し処理");

        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JTextField minField = new JTextField(5);
        JTextField maxField = new JTextField(5);

        JTextArea outputArea = new JTextArea();

        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        JButton forButton = new JButton("forで表示");
        JButton whileButton = new JButton("whileで表示");
        forButton.addActionListener(e -> {
            try {
                int min = Integer.parseInt(minField.getText());
                int max = Integer.parseInt(maxField.getText());

                outputArea.setText(LoopHandler.getForLoopOutput(min, max));
            } catch (NumberFormatException ex) {
                outputArea.setText(ERROR_MESSAGE);
            }
        });
        whileButton.addActionListener(e -> {
            try {
                int min = Integer.parseInt(minField.getText());
                int max = Integer.parseInt(maxField.getText());

                outputArea.setText(LoopHandler.getWhileLoopOutput(min, max));
            } catch (NumberFormatException ex) {
                outputArea.setText(ERROR_MESSAGE);
            }
        });
        buttonPanel.add(forButton);
        buttonPanel.add(whileButton);


        outputArea.setEditable(false);

        // 入力パネルのレイアウト
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("最小値:"));
        inputPanel.add(minField);
        inputPanel.add(new JLabel("最大値:"));
        inputPanel.add(maxField);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

    }
}