import javax.swing.*;
import java.awt.*;

public class ProfileViewer {
    public static void main(String[] args) {
        JFrame frame = new JFrame("プロフィール表示アプリ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);

        // 入力欄の定義
        JTextField nameField = new JTextField(15);
        JTextField ageField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        JTextField studentField = new JTextField(5); // true / false
        JTextField gradeField = new JTextField(2);   // A〜F

        JButton showButton = new JButton("表示する");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);

        // イベント処理を別クラスに委譲
        showButton.addActionListener(e -> {
            ProfileHandler profileHandler = new ProfileHandler(nameField, ageField, heightField, studentField, gradeField);
            Profile profile = profileHandler.createProfile();
            TextAreaRenderer renderer = new TextAreaRenderer();
            renderer.render(profile, resultArea);
        });

        // 入力パネルのレイアウト
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.add(new JLabel("名前:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("年齢:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("身長(cm):"));
        inputPanel.add(heightField);
        inputPanel.add(new JLabel("学生ですか？（true/false）:"));
        inputPanel.add(studentField);
        inputPanel.add(new JLabel("評価（A〜F）:"));
        inputPanel.add(gradeField);
        inputPanel.add(showButton);

        // フレームに追加
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(resultArea), BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
