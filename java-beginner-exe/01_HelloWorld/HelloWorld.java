import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HelloWorld extends JFrame implements ActionListener {
    private JButton helloButton;
    private JLabel messageLabel;

    public HelloWorld() {
        setTitle("Hello World アプリ");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        helloButton = new JButton("あいさつする");
        messageLabel = new JLabel("");

        helloButton.addActionListener(this);

        add(helloButton);
        add(messageLabel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        messageLabel.setText("Hello, Java!");
    }

    public static void main(String[] args) {
        new HelloWorld();
    }
}
