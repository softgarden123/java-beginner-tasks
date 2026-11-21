package shared.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import launcher.ReadmeDialog;

final class TaskHelpSupport {

    private TaskHelpSupport() {
    }

    static void showHelpDialog(JFrame owner, Class<?> contextClass, Font editorFont,
                               String helpText, String imagePath, boolean fallbackToReadme) {
        boolean hasHelpText = helpText != null && !helpText.isBlank();
        boolean hasHelpImage = imagePath != null && !imagePath.isBlank();
        if (!hasHelpText && !hasHelpImage) {
            if (fallbackToReadme) {
                ReadmeDialog.showReadme(owner);
            }
            return;
        }

        JDialog dialog = new JDialog(owner, "説明", true);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        if (hasHelpText) {
            JTextArea text = new JTextArea(helpText);
            text.setEditable(false);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setFont(editorFont);
            text.setBackground(UIManager.getColor("Panel.background"));
            text.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            text.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(text);
        }

        if (hasHelpImage) {
            ImageIcon icon = loadHelpImage(contextClass, imagePath);
            if (icon != null) {
                JLabel imageLabel = new JLabel(icon);
                imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(Box.createRigidArea(new Dimension(0, 12)));
                content.add(imageLabel);
            } else {
                JTextArea warning = new JTextArea("ヘルプ画像の読み込みに失敗しました: " + imagePath);
                warning.setEditable(false);
                warning.setForeground(Color.RED);
                warning.setBackground(UIManager.getColor("Panel.background"));
                warning.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                warning.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(Box.createRigidArea(new Dimension(0, 12)));
                content.add(warning);
            }
        }

        dialog.add(new JScrollPane(content), BorderLayout.CENTER);

        JPanel footer = new JPanel();
        JButton close = new JButton("閉じる");
        close.addActionListener(e -> dialog.dispose());
        footer.add(close);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setSize(560, 700);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static ImageIcon loadHelpImage(Class<?> contextClass, String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        String normalizedPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
        String packagePath = contextClass.getPackageName().replace('.', '/');

        String[] resourceCandidates = new String[] {
                "/" + packagePath + "/img/" + normalizedPath,
                "/img/" + normalizedPath,
                "/" + normalizedPath
        };

        for (String candidate : resourceCandidates) {
            URL resourceUrl = contextClass.getResource(candidate);
            if (resourceUrl != null) {
                return scaleIcon(new ImageIcon(resourceUrl));
            }
        }

        File directFile = new File(imagePath);
        if (directFile.isAbsolute() && directFile.exists()) {
            return scaleIcon(new ImageIcon(directFile.getAbsolutePath()));
        }

        File base = getRuntimeBaseDirectory(contextClass);
        if (base != null) {
            String taskImgPath = packagePath + "/img/" + normalizedPath;
            File candidate = new File(base, taskImgPath);
            if (candidate.exists()) {
                return scaleIcon(new ImageIcon(candidate.getAbsolutePath()));
            }
            candidate = new File(base, "src" + File.separator + taskImgPath.replace('/', File.separatorChar));
            if (candidate.exists()) {
                return scaleIcon(new ImageIcon(candidate.getAbsolutePath()));
            }
        }

        File relativeFile = new File(System.getProperty("user.dir"), packagePath + "/img/" + normalizedPath);
        if (relativeFile.exists()) {
            return scaleIcon(new ImageIcon(relativeFile.getAbsolutePath()));
        }

        File relativeSrcFile = new File(System.getProperty("user.dir"), "src/" + packagePath + "/img/" + normalizedPath);
        if (relativeSrcFile.exists()) {
            return scaleIcon(new ImageIcon(relativeSrcFile.getAbsolutePath()));
        }

        return null;
    }

    private static ImageIcon scaleIcon(ImageIcon icon) {
        if (icon == null) {
            return null;
        }

        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        int maxWidth = 520;
        int maxHeight = 400;
        if (width <= maxWidth && height <= maxHeight) {
            return icon;
        }

        double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
        int scaledWidth = Math.max(1, (int) (width * scale));
        int scaledHeight = Math.max(1, (int) (height * scale));
        Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private static File getRuntimeBaseDirectory(Class<?> contextClass) {
        try {
            URL location = contextClass.getProtectionDomain().getCodeSource().getLocation();
            if (location == null) {
                return null;
            }
            File code = new File(location.toURI());
            return code.isFile() ? code.getParentFile() : code;
        } catch (URISyntaxException | SecurityException ignored) {
            return null;
        }
    }
}