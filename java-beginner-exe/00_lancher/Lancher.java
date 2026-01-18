import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Lancher extends JFrame implements ActionListener {
    public Lancher() {
        setTitle("Java課題ランチャー");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Task[] tasks = Task.values();
        int taskCount = tasks.length;
        setLayout(new GridLayout(taskCount, 1));

        for (Task task : tasks) {
            JButton btn = new JButton(task.getName());
            btn.setActionCommand(task.getExePath());
            btn.addActionListener(this);
            add(btn);
        }

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String path = e.getActionCommand();

        // 「はじめに」を別クラスで表示
        if ("Readme".equals(Task.INTRO.getExePath())) {
            ReadmeDialog.showReadme(this);
            return;
        }

        try {
            File exe = new File(path);
            if (exe.exists()) {
                Runtime.getRuntime().exec(path);
            } else {
                JOptionPane.showMessageDialog(this, "実行ファイルが見つかりません: " + path);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "起動に失敗しました: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Lancher();
    }
}
