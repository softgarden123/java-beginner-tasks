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
import shared.LocalCodeExecutor;
import shared.LocalCodeExecutor.ExecutionResult;

public abstract class SingleFileTaskFrame extends JFrame {

    private final JTextArea codeArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private final JTextArea errorArea = new JTextArea();
    private final JButton runButton = new JButton();
    private final JButton saveButton = new JButton("保存");
    private final JButton restoreButton = new JButton("復元");
    private final LocalCodeExecutor executor = new LocalCodeExecutor();

    protected SingleFileTaskFrame(String title, int width, int height) {
        super(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    protected abstract String getInitialCode();

    protected String getFileName() {
        return "UserCode.java";
    }

    protected String createMainClassName() {
        String fileName = getFileName();
        return fileName.endsWith(".java") ? fileName.substring(0, fileName.length() - 5) : fileName;
    }

    protected String prepareSource(String sourceCode, String mainClassName) {
        return sourceCode;
    }

    protected boolean hasErrorArea() {
        return true;
    }

    protected String getRunButtonLabel() {
        return "コンパイルして実行";
    }

    /**
     * タスク固有のヘルプ文字列を返す。デフォルトは null（共通 README を表示）。
     */
    protected String getHelpText() {
        return null;
    }

    private void showHelp() {
        TaskHelpSupport.showHelpDialog(this, getClass(), getEditorFont(), getHelpText(), getHelpImagePath(), true);
    }

    protected void showHelpDialog() {
        showHelp();
    }

    /**
     * タスクごとのヘルプ画像ファイル名を返します。
     * 返り値が "sample.png" の場合、以下を順に探索します。
     * 1) クラスパス上の /<task-package>/img/sample.png
     * 2) クラスパス上の /img/sample.png
     * 3) 起動ディレクトリの src/<task-package>/img/sample.png
     * 4) 起動ディレクトリの <task-package>/img/sample.png
     */
    protected String getHelpImagePath() {
        return null;
    }

    protected JComponent createRightPanel() {
        return new AIChatPanel(() -> {
            Map<String, String> files = new LinkedHashMap<>();
            files.put(getFileName(), getEditorText());
            return files;
        });
    }

    protected Font getEditorFont() {
        return new Font(Font.MONOSPACED, Font.PLAIN, 12);
    }

    protected final void buildUI() {
        codeArea.setText(getInitialCode());
        codeArea.setFont(getEditorFont());
        codeArea.setTabSize(4);

        outputArea.setEditable(false);
        outputArea.setTabSize(4);
        errorArea.setEditable(false);
        errorArea.setTabSize(4);

        runButton.setText(getRunButtonLabel());
        runButton.addActionListener(e -> runUserCode());
        saveButton.addActionListener(e -> saveSource());
        restoreButton.addActionListener(e -> restoreSource());

        JScrollPane codeScroll = new JScrollPane(codeArea);
        JPanel codePanel = new JPanel(new BorderLayout());

        // ヘルプボタン（各タスクで利用可能）
        JButton helpButton = new JButton("?");
        helpButton.setToolTipText("説明を表示");
        helpButton.addActionListener(e -> showHelp());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(new JLabel(" "), BorderLayout.CENTER);
        JPanel helpWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        helpWrap.add(helpButton);
        topBar.add(helpWrap, BorderLayout.EAST);
        codePanel.add(topBar, BorderLayout.NORTH);

        codePanel.add(codeScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(runButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(restoreButton);
        codePanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane outputScroll = new JScrollPane(outputArea);
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JLabel("実行結果"), BorderLayout.NORTH);
        outputPanel.add(outputScroll, BorderLayout.CENTER);

        JComponent bottomCenter = outputPanel;
        if (hasErrorArea()) {
            JScrollPane errorScroll = new JScrollPane(errorArea);
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(new JLabel("エラー"), BorderLayout.NORTH);
            errorPanel.add(errorScroll, BorderLayout.CENTER);

            JSplitPane outputErrorSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputPanel, errorPanel);
            outputErrorSplit.setDividerLocation(0.65);
            outputErrorSplit.setResizeWeight(0.65);
            bottomCenter = outputErrorSplit;
        }

        JSplitPane topBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codePanel, bottomCenter);
        topBottom.setDividerLocation(0.6);
        topBottom.setResizeWeight(0.6);
        topBottom.setOneTouchExpandable(true);

        JComponent mainPane = topBottom;
        JComponent rightPanel = createRightPanel();
        if (rightPanel != null) {
            rightPanel.setMinimumSize(new Dimension(380, 220));
            JSplitPane leftRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, topBottom, rightPanel);
            int rightPanelWidth = Math.max(380, getRightPanelPreferredWidth());
            int dividerLocation = Math.max(200, getWidth() - rightPanelWidth);
            leftRight.setDividerLocation(dividerLocation);
            leftRight.setResizeWeight(0.75);
            leftRight.setOneTouchExpandable(true);
            mainPane = leftRight;
        }

        add(mainPane, BorderLayout.CENTER);
    }

    protected void appendOutput(String text) {
        outputArea.append(text + "\n");
    }

    protected void appendError(String text) {
        errorArea.append(text + "\n");
    }

    protected String getEditorText() {
        return codeArea.getText();
    }

    protected int getRightPanelPreferredWidth() {
        return 460;
    }

    protected String getSaveId() {
        return getClass().getSimpleName();
    }

    protected Path getSaveDirectory() {
        return Paths.get("saved", getSaveId());
    }

    protected Path getSaveFilePath() {
        return getSaveDirectory().resolve(getFileName());
    }

    protected Path getLegacySaveFilePath() {
        return Paths.get("saved", getSaveId() + "-" + getFileName());
    }

    private void saveSource() {
        try {
            Path file = getSaveFilePath();
            Files.createDirectories(file.getParent());
            Files.writeString(file, getEditorText(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this, "ソースを保存しました: " + file, "保存完了", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存に失敗しました: " + e.getMessage(), "保存エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restoreSource() {
        try {
            Path file = getSaveFilePath();
            if (!Files.exists(file)) {
                Path legacyFile = getLegacySaveFilePath();
                if (Files.exists(legacyFile)) {
                    file = legacyFile;
                }
            }
            if (!Files.exists(file)) {
                JOptionPane.showMessageDialog(this, "保存されたソースが見つかりません。", "復元エラー", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String content = Files.readString(file, StandardCharsets.UTF_8);
            codeArea.setText(content);
            JOptionPane.showMessageDialog(this, "保存されたソースを復元しました: " + file, "復元完了", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "復元に失敗しました: " + e.getMessage(), "復元エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runUserCode() {
        runButton.setEnabled(false);
        outputArea.setText("");
        errorArea.setText("");

        SwingWorker<ExecutionResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ExecutionResult doInBackground() {
                String mainClassName = createMainClassName();
                String fileName = mainClassName + ".java";
                String sourceCode = prepareSource(codeArea.getText(), mainClassName);
                return executor.execute(fileName, sourceCode, mainClassName);
            }

            @Override
            protected void done() {
                try {
                    ExecutionResult result = get();
                    if (!result.error.isEmpty()) {
                        appendError(result.error);
                    }
                    if (!result.output.isEmpty()) {
                        appendOutput(result.output);
                    }
                } catch (Exception e) {
                    appendError("実行中にエラーが発生しました: " + e.getMessage());
                } finally {
                    runButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
