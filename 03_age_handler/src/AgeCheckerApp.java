import javax.swing.*;
import java.awt.*;

public class AgeCheckerApp {
    private static final String ERROR_MESSAGE = "年齢は数字で入力してください。";

    public static void main(String[] args) {
        JFrame frame = new JFrame("年齢判定アプリ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JTextField ageField = new JTextField(5);
        JButton checkButton = new JButton("判定する");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        checkButton.addActionListener(e -> {
            try {
                int age = Integer.parseInt(ageField.getText());
                String message = AgeHandler.getMessage(age);
                resultArea.setText(message);
            } catch (NumberFormatException ex) {
                resultArea.setText(ERROR_MESSAGE);
            }
            ageField.getText();
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("年齢を入力:"));
        panel.add(ageField);
        panel.add(checkButton);

        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(resultArea, BorderLayout.CENTER);

        frame.setVisible(true);

    }
}
