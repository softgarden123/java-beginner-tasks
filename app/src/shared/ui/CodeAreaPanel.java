package shared.ui;

import java.awt.*;
import javax.swing.*;

public class CodeAreaPanel extends JPanel {

    private final JTextArea textArea;

    public CodeAreaPanel(String title, String initialText, Font font) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel(title);
        add(label, BorderLayout.NORTH);

        textArea = new JTextArea(initialText);
        textArea.setFont(font);
        textArea.setTabSize(4);

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public JTextArea getTextArea() {
        return textArea;
    }
}
