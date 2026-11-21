package shared.ui;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import shared.UserCodeExecutor;

public abstract class TaskEditorFrame extends JFrame {

    private final Map<String, CodeAreaPanel> codePanels = new LinkedHashMap<>();
    private final JTextArea outputArea = new JTextArea();
    private final JButton saveButton = new JButton("保存");
    private final JButton restoreButton = new JButton("復元");
    private final UserCodeExecutor executor = new UserCodeExecutor();
    private final String mainClassName;

    protected TaskEditorFrame(String title, int width, int height, String mainClassName) {
        super(title);
        this.mainClassName = mainClassName;
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    protected void addSourceEditor(String fileName, String initialCode) {
        CodeAreaPanel panel = new CodeAreaPanel(fileName, initialCode, getEditorFont());
        codePanels.put(fileName, panel);
    }

    protected JTextArea getEditor(String fileName) {
        CodeAreaPanel panel = codePanels.get(fileName);
        return panel == null ? null : panel.getTextArea();
    }

    protected void buildUI() {
        JPanel codePanel = new JPanel(new GridLayout(1, codePanels.size()));
        for (CodeAreaPanel panel : codePanels.values()) {
            codePanel.add(panel);
        }

        JButton runButton = new JButton(getRunButtonLabel());
        runButton.addActionListener(e -> new Thread(this::runCode, "UserCode-Runner").start());
        saveButton.addActionListener(e -> saveSourceFiles());
        restoreButton.addActionListener(e -> restoreSourceFiles());

        JButton helpButton = new JButton("?");
        helpButton.setToolTipText("説明を表示");
        helpButton.addActionListener(e -> showHelp());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(runButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(restoreButton);
        buttonPanel.add(helpButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setTabSize(4);
        bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codePanel, bottomPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(getDividerLocation());
        splitPane.setResizeWeight(getResizeWeight());

        JComponent mainPane = splitPane;
        JComponent rightPanel = createRightPanel();
        if (rightPanel != null) {
            rightPanel.setMinimumSize(new Dimension(380, 220));
            JSplitPane leftRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, rightPanel);
            int rightPanelWidth = Math.max(380, getRightPanelPreferredWidth());
            int dividerLocation = Math.max(200, getWidth() - rightPanelWidth);
            leftRight.setDividerLocation(dividerLocation);
            leftRight.setResizeWeight(0.75);
            leftRight.setOneTouchExpandable(true);
            mainPane = leftRight;
        }

        add(mainPane, BorderLayout.CENTER);
    }

    protected JComponent createRightPanel() {
        return new AIChatPanel(this::collectEditorSources);
    }

    private Map<String, String> collectEditorSources() {
        Map<String, String> sourceFiles = new LinkedHashMap<>();
        for (Map.Entry<String, CodeAreaPanel> entry : codePanels.entrySet()) {
            sourceFiles.put(entry.getKey(), entry.getValue().getTextArea().getText());
        }
        return sourceFiles;
    }

    protected Font getEditorFont() {
        return new Font(Font.MONOSPACED, Font.PLAIN, 12);
    }

    protected String getHelpText() {
        return null;
    }

    protected String getHelpImagePath() {
        return null;
    }

    protected String getRunButtonLabel() {
        return "実行";
    }

    protected double getDividerLocation() {
        return 0.6;
    }

    protected double getResizeWeight() {
        return 0.6;
    }

    protected int getRightPanelPreferredWidth() {
        return 460;
    }

    private void runCode() {
        outputArea.setText("");
        Map<String, String> sourceFiles = new LinkedHashMap<>();
        for (Map.Entry<String, CodeAreaPanel> entry : codePanels.entrySet()) {
            sourceFiles.put(entry.getKey(), entry.getValue().getTextArea().getText());
        }

        executor.execute(sourceFiles, mainClassName, text -> SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
        }));
    }

    protected String getSaveId() {
        return getClass().getSimpleName();
    }

    protected Path getSaveDirectory() {
        return Paths.get("saved", getSaveId());
    }

    private void showHelp() {
        TaskHelpSupport.showHelpDialog(this, getClass(), getEditorFont(), getHelpText(), getHelpImagePath(), true);
    }

    private void saveSourceFiles() {
        try {
            Path directory = getSaveDirectory();
            Files.createDirectories(directory);
            for (Map.Entry<String, CodeAreaPanel> entry : codePanels.entrySet()) {
                Path file = directory.resolve(entry.getKey());
                Files.writeString(file, entry.getValue().getTextArea().getText(), StandardCharsets.UTF_8);
            }
            JOptionPane.showMessageDialog(this, "ソースを保存しました: " + directory, "保存完了", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存に失敗しました: " + e.getMessage(), "保存エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restoreSourceFiles() {
        try {
            Path directory = getSaveDirectory();
            if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                JOptionPane.showMessageDialog(this, "保存されたソースが見つかりません。", "復元エラー", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean restoredAny = false;
            for (Map.Entry<String, CodeAreaPanel> entry : codePanels.entrySet()) {
                Path file = directory.resolve(entry.getKey());
                if (Files.exists(file)) {
                    String content = Files.readString(file, StandardCharsets.UTF_8);
                    entry.getValue().getTextArea().setText(content);
                    restoredAny = true;
                }
            }
            if (restoredAny) {
                JOptionPane.showMessageDialog(this, "保存されたソースを復元しました: " + directory, "復元完了", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "保存されたソースファイルが見つかりません。", "復元エラー", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "復元に失敗しました: " + e.getMessage(), "復元エラー", JOptionPane.ERROR_MESSAGE);
        }
    }
}
