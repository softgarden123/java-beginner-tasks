// java
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.File;
import java.net.URL;

public class ReadmeDialog extends JDialog {
    // 埋め込み版（必要ならここにreadmeの全文を入れる）
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

        JPanel buttons = new JPanel();
        JButton close = new JButton("閉じる");
        close.addActionListener(e -> dispose());
        buttons.add(close);
        add(buttons, BorderLayout.SOUTH);

        setSize(480, 700);
        setLocationRelativeTo(parent);
    }

    private static String loadReadme() {
        // 1) クラスパス上のリソースを試す（jar に /readme.txt を入れる）
        try (InputStream in = ReadmeDialog.class.getResourceAsStream("/readme.txt")) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }

        // 2) JAR と同じフォルダにあるファイルを試す（従来の外部ファイル互換）
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

        // 3) ソース内に埋めたフォールバックを返す
        if (README_FALLBACK != null && !README_FALLBACK.isBlank()) {
            return README_FALLBACK;
        }

        // 見つからなければ案内メッセージ
        return "説明ファイルが見つかりません。jar に `/readme.txt` を含めるか、`README_FALLBACK` を埋め込んでください。";
    }

    public static void showReadme(JFrame parent) {
        ReadmeDialog dialog = new ReadmeDialog(parent);
        dialog.setVisible(true);
    }
}
