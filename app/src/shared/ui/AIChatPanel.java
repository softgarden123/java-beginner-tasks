package shared.ui;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.DefaultEditorKit;

public class AIChatPanel extends JPanel {

    private static final MutableDataSet MARKDOWN_OPTIONS = createMarkdownOptions();
    private static final Parser MARKDOWN_PARSER = Parser.builder(MARKDOWN_OPTIONS).build();
    private static final HtmlRenderer MARKDOWN_RENDERER = HtmlRenderer.builder(MARKDOWN_OPTIONS).build();
    private static final Pattern RESPONSE_FIELD_PATTERN = Pattern.compile("\\\"response\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"");
    private static final Pattern CONTENT_FIELD_PATTERN = Pattern.compile("\\\"content\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"");
    private static final String CODE_OPTION_NONE = "__NONE__";
    private static final String CODE_OPTION_ALL = "__ALL__";
    private static final int INPUT_FIELD_ROWS = 6;
    private static final int MIN_INPUT_AREA_HEIGHT = 110;
    private static final int PREFERRED_INPUT_AREA_HEIGHT = 170;
    private static final int MAX_INPUT_AREA_HEIGHT = 280;

    private final JEditorPane chatPane = new JEditorPane();
    private final StringBuilder chatMarkdownBuffer = new StringBuilder();
    private final JTextArea inputField = new JTextArea(INPUT_FIELD_ROWS, 20);
    private final JButton sendButton = new JButton("送信");
    private final JComboBox<CodeOption> codeSelector = new JComboBox<>();
    private final Supplier<Map<String, String>> editorFilesSupplier;
    private AIConfig config;

    public AIChatPanel(Supplier<Map<String, String>> editorFilesSupplier) {
        this.editorFilesSupplier = editorFilesSupplier;
        this.config = AIConfig.load();
        setLayout(new BorderLayout());
        JPanel north = new JPanel(new BorderLayout());
        north.add(new JLabel("AI チャット（Ollama）　※Beta版"), BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        controlsPanel.add(new JLabel("コード"));
        controlsPanel.add(codeSelector);
        JButton clearChatButton = new JButton("クリア");
        controlsPanel.add(clearChatButton);
        JButton settings = new JButton("設定");
        controlsPanel.add(settings);
        settings.addActionListener(e -> showSettingsDialog());
        clearChatButton.addActionListener(e -> clearChatHistory());
        rebuildCodeSelectorOptions();

        chatPane.setContentType("text/html");
        chatPane.setEditable(false);
        chatPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        chatPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ignored) {
                    // Ignore link-open failures to keep chat usable.
                }
            }
        });
        chatPane.setText(toHtml(""));
        JScrollPane chatScrollPane = new JScrollPane(chatPane);

        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputField);
        inputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setMinimumSize(new Dimension(0, MIN_INPUT_AREA_HEIGHT));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlsPanel, BorderLayout.NORTH);
        bottomPanel.add(inputPanel, BorderLayout.SOUTH);
        bottomPanel.setMinimumSize(new Dimension(0, MIN_INPUT_AREA_HEIGHT));

        JSplitPane contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chatScrollPane, bottomPanel);
        contentSplitPane.setResizeWeight(1.0);
        contentSplitPane.setContinuousLayout(true);
        contentSplitPane.setOneTouchExpandable(true);
        contentSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> clampBottomAreaHeight(contentSplitPane));
        add(contentSplitPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> setBottomAreaHeight(contentSplitPane, PREFERRED_INPUT_AREA_HEIGHT));

        sendButton.addActionListener(e -> sendQuestion());
        configureInputActions();
    }

    private void configureInputActions() {
        InputMap inputMap = inputField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = inputField.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ctrl ENTER"), "sendQuestion");
        inputMap.put(KeyStroke.getKeyStroke("shift ENTER"), DefaultEditorKit.insertBreakAction);

        actionMap.put("sendQuestion", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sendQuestion();
            }
        });
    }

    private void clampBottomAreaHeight(JSplitPane splitPane) {
        int currentHeight = getBottomAreaHeight(splitPane);
        int clampedHeight = Math.max(MIN_INPUT_AREA_HEIGHT, Math.min(MAX_INPUT_AREA_HEIGHT, currentHeight));
        if (clampedHeight != currentHeight) {
            SwingUtilities.invokeLater(() -> setBottomAreaHeight(splitPane, clampedHeight));
        }
    }

    private int getBottomAreaHeight(JSplitPane splitPane) {
        return splitPane.getHeight() - splitPane.getDividerLocation() - splitPane.getDividerSize();
    }

    private void setBottomAreaHeight(JSplitPane splitPane, int targetHeight) {
        int availableHeight = splitPane.getHeight();
        if (availableHeight <= 0) {
            return;
        }

        int clampedHeight = Math.max(MIN_INPUT_AREA_HEIGHT, Math.min(MAX_INPUT_AREA_HEIGHT, targetHeight));
        int dividerLocation = Math.max(0, availableHeight - clampedHeight - splitPane.getDividerSize());
        splitPane.setDividerLocation(dividerLocation);
    }

    private boolean isOllamaInstalled() {
        try {
            HttpRequest check = HttpRequest.newBuilder()
                    .uri(URI.create(config.endpoint + "/api/tags"))
                    .timeout(Duration.ofSeconds(config.timeoutSeconds))
                    .GET()
                    .build();
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(config.timeoutSeconds)).build()
                    .send(check, HttpResponse.BodyHandlers.ofString());
            return true;
        } catch (Exception e) {
            appendChat("Ollama が見つかりません。AI 機能は OFF になります。\n");
            return false;
        }
    }

    private void sendQuestion() {
        String question = inputField.getText().trim();
        if (question.isEmpty()) return;
        inputField.setText("");
        appendChat("\n\n---\n\n**あなた:**\n\n" + question + "\n\n---\n\n**AI:**\n\n");
        rebuildCodeSelectorOptions();
        String code = buildPromptCode();
        String codeSection = code.isBlank() ? "" : code;
        String prompt = buildPrompt(question, codeSection);

        new Thread(() -> {
            if (!isOllamaInstalled()) return;
            try {
                sendToOllama(prompt);
            } catch (Exception ex) {
                appendChat("AI 送信に失敗しました: " + ex.getMessage() + "\n");
            }
        }).start();
    }

    private static String buildPrompt(String question, String codeSection) {
        StringBuilder sb = new StringBuilder();
        if (!codeSection.isEmpty()) {
            sb.append("あなたはJava初心者向けメンターです。簡潔・正確に答えてください。\n")
                .append("説明は短く要点中心にしてください。\n")
                .append("必要なときだけ最小のコード例を示してください。\n")
                .append("不明点は推測せず、不足前提を1行で示してください。\n\n")
                .append("【現在のコード】\n").append(codeSection).append("\n\n");
        }

        sb.append("【質問】\n").append(question).append("\n\n")
            .append("必ず日本語で回答してください。\n")
            .append("必ずMarkdown形式で回答してください。\n");

            System.out.println("Prompt built:\n" + sb.toString());
        return sb.toString();
    }

    private void rebuildCodeSelectorOptions() {
        Object selectedItem = codeSelector.getSelectedItem();
        String selectedId = selectedItem instanceof CodeOption ? ((CodeOption) selectedItem).id : CODE_OPTION_ALL;

        DefaultComboBoxModel<CodeOption> model = new DefaultComboBoxModel<>();
        model.addElement(new CodeOption(CODE_OPTION_NONE, "なし"));
        model.addElement(new CodeOption(CODE_OPTION_ALL, "すべて"));

        for (String fileName : getCurrentEditorFiles().keySet()) {
            model.addElement(new CodeOption(fileName, fileName));
        }

        codeSelector.setModel(model);
        selectCodeOptionById(selectedId);
    }

    private void selectCodeOptionById(String id) {
        ComboBoxModel<CodeOption> model = codeSelector.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            CodeOption option = model.getElementAt(i);
            if (option.id.equals(id)) {
                codeSelector.setSelectedItem(option);
                return;
            }
        }
        codeSelector.setSelectedIndex(Math.min(1, Math.max(0, model.getSize() - 1)));
    }

    private String buildPromptCode() {
        Map<String, String> files = getCurrentEditorFiles();
        Object selected = codeSelector.getSelectedItem();
        String selectedId = selected instanceof CodeOption ? ((CodeOption) selected).id : CODE_OPTION_ALL;

        if (CODE_OPTION_NONE.equals(selectedId)) {
            return "";
        }
        if (CODE_OPTION_ALL.equals(selectedId)) {
            return formatAllFiles(files);
        }

        String singleCode = files.get(selectedId);
        if (singleCode == null) {
            return formatAllFiles(files);
        }
        return formatSingleFile(selectedId, singleCode);
    }

    private Map<String, String> getCurrentEditorFiles() {
        Map<String, String> files = new LinkedHashMap<>();
        if (editorFilesSupplier == null) {
            return files;
        }

        Map<String, String> supplied = editorFilesSupplier.get();
        if (supplied == null) {
            return files;
        }

        for (Map.Entry<String, String> entry : supplied.entrySet()) {
            String name = entry.getKey();
            if (name == null || name.isBlank()) {
                continue;
            }
            files.put(name, entry.getValue() == null ? "" : entry.getValue());
        }
        return files;
    }

    private static String formatAllFiles(Map<String, String> files) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : files.entrySet()) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            sb.append(formatSingleFile(entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }

    private static String formatSingleFile(String fileName, String code) {
        return "// File: " + fileName + "\n" + code;
    }

    private void sendToOllama(String prompt) {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String json = "{" +
                        "\"model\":\"" + config.model + "\"," +
                        "\"prompt\":\"" + escapeJson(prompt) + "\"," +
                        "\"stream\":true" +
                        "}";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(config.endpoint + "/api/generate"))
                        .timeout(Duration.ofSeconds(config.timeoutSeconds))
                        .header("Content-Type", "application/json; charset=utf-8")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<java.io.InputStream> response = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(config.timeoutSeconds))
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofInputStream());

                if (response.statusCode() >= 400) {
                    String errorBody;
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                        StringBuilder err = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            err.append(line).append('\n');
                        }
                        errorBody = err.toString();
                    }
                    throw new IOException("HTTP " + response.statusCode() + " - " + trimForDisplay(errorBody));
                }

                boolean hasChunk = false;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty()) {
                            continue;
                        }
                        if (trimmed.startsWith("data:")) {
                            trimmed = trimmed.substring(5).trim();
                        }
                        if ("[DONE]".equals(trimmed)) {
                            break;
                        }

                        String chunk = aggregateFieldValues(trimmed, RESPONSE_FIELD_PATTERN);
                        if (chunk.isBlank()) {
                            chunk = aggregateFieldValues(trimmed, CONTENT_FIELD_PATTERN);
                        }
                        if (!chunk.isBlank()) {
                            publish(chunk);
                            hasChunk = true;
                        }

                        if (trimmed.contains("\"done\":true")) {
                            break;
                        }
                    }
                }

                if (!hasChunk) {
                    throw new IOException("AI応答を解析できませんでした");
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String c : chunks) {
                    appendChat(c);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    appendChat("\n");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    appendChat("AI 送信に失敗しました: " + e.getMessage() + "\n");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    String message = cause == null ? e.getMessage() : cause.getMessage();
                    appendChat("AI 送信に失敗しました: " + message + "\n");
                }
            }
        };
        worker.execute();
    }

    private static String aggregateFieldValues(String jsonText, Pattern pattern) {
        Matcher matcher = pattern.matcher(jsonText);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String part = unescapeJsonString(matcher.group(1));
            if (!part.isEmpty()) {
                sb.append(part);
            }
        }
        return sb.toString();
    }

    private static String trimForDisplay(String text) {
        if (text == null) {
            return "";
        }
        String oneLine = text.replace('\n', ' ').replace('\r', ' ').trim();
        return oneLine.length() > 200 ? oneLine.substring(0, 200) + "..." : oneLine;
    }

    private void appendChat(String text) {
        SwingUtilities.invokeLater(() -> {
            chatMarkdownBuffer.append(normalizeChatText(text));
            chatPane.setText(toHtml(chatMarkdownBuffer.toString()));
            chatPane.setCaretPosition(chatPane.getDocument().getLength());
        });
    }

    private static String normalizeChatText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace("\\n", "\n");
    }

    private void showSettingsDialog() {
        JTextField endpointField = new JTextField(config.endpoint);
        JTextField modelField = new JTextField(config.model);
        JTextField timeoutField = new JTextField(String.valueOf(config.timeoutSeconds));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Endpoint (例: http://localhost:11434)"));
        panel.add(endpointField);
        panel.add(new JLabel("Model (例: qwen2.5:3b)"));
        panel.add(modelField);
        panel.add(new JLabel("Timeout seconds"));
        panel.add(timeoutField);

        int res = JOptionPane.showConfirmDialog(this, panel, "AI 設定", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                config.endpoint = endpointField.getText().trim();
                config.model = modelField.getText().trim();
                config.timeoutSeconds = Integer.parseInt(timeoutField.getText().trim());
                config.save();
                appendChat("設定を保存しました。\n");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "設定を保存できませんでした: " + e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearChatHistory() {
        if (chatMarkdownBuffer.toString().isBlank()) {
            return;
        }
        int result = JOptionPane.showConfirmDialog(
                this,
                "チャット履歴を削除します。よろしいですか？",
                "チャット履歴のクリア",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            chatMarkdownBuffer.setLength(0);
            chatPane.setText(toHtml(""));
        }
    }

    private static String toHtml(String markdown) {
        String normalized = markdown == null ? "" : markdown.replace("\r\n", "\n").replace('\r', '\n');
        String markdownHtml = MARKDOWN_RENDERER.render(MARKDOWN_PARSER.parse(normalized));

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("body{font-family:'Segoe UI',sans-serif;font-size:13px;line-height:1.45;margin:8px;}")
                .append("p{margin:0 0 8px 0;}")
                .append("h1,h2,h3,h4,h5,h6{margin:10px 0 6px 0;}")
                .append("pre{background:#f4f4f4;border:1px solid #ddd;border-radius:4px;padding:8px;overflow:auto;}")
                .append("code{font-family:'Consolas',monospace;background:#f4f4f4;padding:1px 3px;border-radius:3px;}")
                .append("blockquote{margin:8px 0;padding:6px 10px;border-left:4px solid #bbb;background:#fafafa;}")
                .append("ul,ol{margin:0 0 8px 20px;padding:0;}")
                .append("li{margin:2px 0;}")
                .append("a{color:#0b57d0;text-decoration:underline;}")
                .append("hr{border:none;border-top:2px solid #cccccc;margin:10px 0;}")
                .append("</style></head><body>");
        html.append(markdownHtml);
        html.append("</body></html>");
        return html.toString();
    }

    private static MutableDataSet createMarkdownOptions() {
        MutableDataSet options = new MutableDataSet();
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        options.set(HtmlRenderer.ESCAPE_HTML, true);
        return options;
    }

    private static String unescapeJsonString(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch != '\\' || i + 1 >= text.length()) {
                sb.append(ch);
                continue;
            }

            char next = text.charAt(++i);
            switch (next) {
                case 'n' -> sb.append('\n');
                case 'r' -> sb.append('\r');
                case 't' -> sb.append('\t');
                case 'b' -> sb.append('\b');
                case 'f' -> sb.append('\f');
                case '"' -> sb.append('"');
                case '\\' -> sb.append('\\');
                case '/' -> sb.append('/');
                case 'u' -> {
                    if (i + 4 < text.length()) {
                        String hex = text.substring(i + 1, i + 5);
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                            i += 4;
                        } catch (NumberFormatException e) {
                            sb.append('u').append(hex);
                            i += 4;
                        }
                    } else {
                        sb.append('u');
                    }
                }
                default -> sb.append(next);
            }
        }
        return sb.toString();
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static final class CodeOption {
        private final String id;
        private final String label;

        private CodeOption(String id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
