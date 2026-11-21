package launcher;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.*;

public class ReadmeDialog extends JDialog {
    private static final String README_FALLBACK = """
        ここに README の内容を直接書くか、ビルド時に resource として追加してください。
        複数行の文章をそのまま埋め込めます。
        """;

    public ReadmeDialog(JFrame parent) {
        super(parent, "はじめに", false);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textArea.setText(loadReadme());

        JScrollPane scroll = new JScrollPane(textArea);
        add(scroll, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            textArea.setCaretPosition(0);
            JScrollBar vs = scroll.getVerticalScrollBar();
            if (vs != null) {
                vs.setValue(vs.getMinimum());
            }
        });

        JPanel buttons = new JPanel();
        JButton close = new JButton("閉じる");
        close.addActionListener(e -> dispose());
        buttons.add(close);
        add(buttons, BorderLayout.SOUTH);

        setSize(480, 700);
        setLocationRelativeTo(parent);
    }

    private static String loadReadme() {
        try (InputStream in = ReadmeDialog.class.getResourceAsStream("/readme.txt")) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }

        try {
            URL loc = ReadmeDialog.class.getProtectionDomain().getCodeSource().getLocation();
            File code = new File(loc.toURI());
            File base = code.isFile() ? code.getParentFile() : code;
            File f = new File(base, "readme.txt");
            if (f.exists()) {
                return Files.readString(f.toPath(), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {
        }

        // ワーキングディレクトリから readme.txt を探す
        try {
            File f = new File("readme.txt");
            if (f.exists()) {
                return Files.readString(f.toPath(), StandardCharsets.UTF_8);
            }
            // ワーキングディレクトリが app の親の場合
            f = new File("app/readme.txt");
            if (f.exists()) {
                return Files.readString(f.toPath(), StandardCharsets.UTF_8);
            }
            // build フォルダ内で実行されている可能性
            f = new File("../readme.txt");
            if (f.exists()) {
                return Files.readString(f.toPath(), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {
        }

        return README_FALLBACK;
    }

    public static void showReadme(JFrame parent) {
        ReadmeDialog dialog = new ReadmeDialog(parent);
        dialog.setVisible(true);
    }
}
