import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variables extends JFrame implements ActionListener {

    private JTextArea codeArea;
    private JTextArea outputArea;
    private JTextArea errorArea;
    private JButton runButton;
    // Ollama チャット用 UI コンポーネント
    private JPanel chatPanel;
    private JTextArea chatAreaUI;
    private JTextField chatInput;
    private JButton chatSend;
    private boolean ollamaAvailable;

    public Variables() {
        setTitle("課題②：変数とデータ型（コード実行）");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // -----------------------------
        // GUI コンポーネント
        // -----------------------------
        codeArea = new JTextArea(Variables.getTemplate());
        outputArea = new JTextArea();
        errorArea = new JTextArea();

        Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        codeArea.setFont(mono);
        codeArea.setTabSize(4);

        outputArea.setEditable(false);
        errorArea.setEditable(false);

        runButton = new JButton("コンパイルして実行");
        runButton.addActionListener(this);

        // 上：コード入力
        JScrollPane codeScroll = new JScrollPane(codeArea);

        // 中：実行結果
        JScrollPane outputScroll = new JScrollPane(outputArea);

        // 下：エラー表示
        JScrollPane errorScroll = new JScrollPane(errorArea);

        // コード入力用パネル：コード領域の下に実行ボタンを配置する
        JPanel codePanel = new JPanel(new BorderLayout());
        codePanel.add(codeScroll, BorderLayout.CENTER);
        // ボタンをコードパネルの下に置く
        codePanel.add(runButton, BorderLayout.SOUTH);

        // 上下分割
        JSplitPane topBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codePanel, outputScroll);
        topBottom.setDividerLocation(350);

        JSplitPane allSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topBottom, errorScroll);
        allSplit.setDividerLocation(550);

        // Ollama のインストール有無をチェック
        ollamaAvailable = isOllamaInstalled();

        if (ollamaAvailable) {
            // チャットパネルを構築して右側に配置する（左右分割）
            setupOllamaPanel();
            JSplitPane leftRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, allSplit, chatPanel);
            leftRight.setDividerLocation(650);
            add(leftRight, BorderLayout.CENTER);
        } else {
            // Ollama 未検出 => 右側パネルを表示しない
            add(allSplit, BorderLayout.CENTER);
        }
        // runButton は codePanel に追加済みのため、フレームには再追加しない

        setVisible(true);
    }

    // Ollama がコマンドラインで利用できるかをチェック
    private boolean isOllamaInstalled() {
        try {
            HttpRequest check = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/tags"))
                    .GET()
                    .build();

            HttpResponse<String> checkRes = HttpClient.newHttpClient()
                    .send(check, HttpResponse.BodyHandlers.ofString());

            System.out.println("Ollama detected. Models:");
            System.out.println(checkRes.body());
        } catch (Exception e) {
            System.out.println("Ollama が見つかりません。AI 機能は OFF になります。");
            return false;
        }
        return true;
    }

    // チャットパネルの UI を作る
    private void setupOllamaPanel() {
        chatPanel = new JPanel(new BorderLayout());
        chatAreaUI = new JTextArea();
        chatAreaUI.setEditable(false);
        chatAreaUI.setLineWrap(true);
        chatAreaUI.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatAreaUI);

        JPanel inputPanel = new JPanel(new BorderLayout());
        chatInput = new JTextField();
        chatSend = new JButton("送信");
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(chatSend, BorderLayout.EAST);

        chatPanel.add(new JLabel("AI チャット（Ollama）"), BorderLayout.NORTH);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        // 送信アクション
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String question = chatInput.getText().trim();
                String code = codeArea.getText();

                String prompt = """
                    あなたはJava初心者向けのメンターです。
                    説明は簡潔に、要点だけを短くまとめてください。
                    完成コードや修正済みコード、具体的なコード例を絶対に書かないでください。
                    エラーの原因や考え方のヒントだけを説明してください。
                    日本語は自然で読みやすくしてください。
                        
                        
                    【質問】
                        %s

                    【コード】
                        %s
                        
                     【禁止例】
                         int age = 20;  // ← このような完成コードを書かない
                    """.formatted(question, code);

                if (prompt.isEmpty()) return;
                chatInput.setText("");
                appendChat("You: " + question + "\n");
                // 別スレッドで実行して UI をブロックしない
                new Thread(() -> {
                    try {
                        sendToOllama(prompt);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }).start();
            }
        };

        chatSend.addActionListener(sendAction);
        chatInput.addActionListener(sendAction);
    }

    // Ollama CLI にプロンプトを送り、応答をチャット領域に追記する
    private void sendToOllama(String prompt) throws IOException, InterruptedException {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
             @Override
             protected Void doInBackground() throws Exception {

                // JSON に埋めるためにエスケープ（コード内の " や \ があると JSON が壊れる）
                String json = "{" +
                        "\"model\":\"qwen2.5:3b\"," +
                        "\"prompt\":\"" + escapeJson(prompt) + "\"" +
                        "}";

                // debug removed
                String dbg = json.length() > 1000 ? json.substring(0, 1000) + "...(truncated)" : json;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434/api/generate"))
                        .header("Content-Type", "application/json; charset=utf-8")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<InputStream> response =
                         client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                // デバッグ: 送信した JSON をファイルに保存
                try (FileWriter fw = new FileWriter("last_ollama_request.json")) {
                     fw.write(json);
                 } catch (Exception ex) {
                     // debug removed
                 }

                // AI応答開始の目印を表示（見やすくするためのラベル）
                appendChat("AI: ");

                 // 受信ストリームを読みつつ生データをバイナリで保存し、行単位で処理する
                 try (InputStream in = response.body();
                      ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                     String line;
                     while ((line = reader.readLine()) != null) {
                        // 保存
                        baos.write((line + "\n").getBytes());
                        // 正規表現で "response"（文字列値）と "done" を抽出する
                        String token = null;
                        boolean done = false;
                        try {
                            // response: "response": "..."
                            String resp = Variables.extractJsonStringValue(line, "response");
                            if (resp != null) token = resp;
                            // done: "done": true/false
                            Boolean d = Variables.extractJsonBooleanValue(line, "done");
                            if (d != null) done = d.booleanValue();
                        } catch (Exception ex) {
                            // 解析失敗は無視して次行へ
                        }

                        if (token != null) {
                            // デバッグ: 生トークンをチャットに出す(短く)
                            String rawDbg = token.length() > 200 ? token.substring(0,200) + "...(truncated)" : token;
                            // 可視化：実際の改行やタブをエスケープ表現で表示し、長さとコードポイントも出す
                            String visible = rawDbg.replace("\\", "\\\\")
                                    .replace("\n", "\\n")
                                    .replace("\r", "\\r")
                                    .replace("\t", "\\t");
                            boolean hasActualNewline = token.indexOf('\n') >= 0;
                            boolean hasLiteralBackslashN = token.contains("\\n");
                            String status = (hasActualNewline ? "(contains actual newline U+000A)" : "")
                                    + (hasLiteralBackslashN ? "(contains literal \\\\n+)": "");
                            // debug removed
                             // JSON パーサを通すと通常のエスケープは除去されるため、ここで二重エスケープへの対応も行う
                             String out = token;
                            // 表示上まだ "\\n" のような二重エスケープが残っている場合、逐次置換して実際の改行にする
                            String prev;
                            do {
                                prev = out;
                                out = out.replace("\\\\n", "\\n")
                                         .replace("\\n", "\n")
                                         .replace("\\\\r", "\\r")
                                         .replace("\\r", "\r")
                                         .replace("\\\\t", "\\t")
                                         .replace("\\t", "\t");
                            } while (!out.equals(prev));

                            // さらなる一般的なアンエスケープ
                            out = unescapeJson(out);
                            publish(out);
                         }
                         if (done) break;
                      }

                    // 受信した生レスポンスをファイルに保存（デバッグ用）
                    try (FileOutputStream fos = new FileOutputStream("last_ollama_response.txt")) {
                         fos.write(baos.toByteArray());
                     } catch (Exception ex) {
                         // debug removed
                     }
                   }

                // debug removed: HTTP status
                 return null;
             }

            @Override
            protected void process(List<String> chunks) {
                for (String s : chunks) {
                    chatAreaUI.append(s);
                }
            }
        };

        worker.execute();
    }

    private void appendChat(String text) {
        SwingUtilities.invokeLater(() -> {
            // 引数をラムダ内で自由に編集するためにローカルにコピーする
            String t = text;
            String existing = chatAreaUI.getText();

            // 既存テキストが空でなければ、前のメッセージとつながらないように
            // 必要に応じて先頭に改行を挿入する。既に空行がある場合は挿入しない。
            if (!existing.isEmpty()) {
                if (!existing.endsWith("\n\n")) {
                    if (existing.endsWith("\n")) {
                        t = "\n" + t; // 1つ改行がある場合はさらに1つ追加して空行を作る
                    } else {
                        t = "\n\n" + t; // 改行がない場合は2つ入れて空行を作る
                    }
                }
            }

            // ユーザのメッセージ（"You:" で始まる）なら、末尾にも空行を確保して
            // 直後に来る AI のラベルや応答がつながらないようにする
            if (t.startsWith("You:") && !t.endsWith("\n\n")) {
                if (t.endsWith("\n")) {
                    t = t + "\n";
                } else {
                    t = t + "\n\n";
                }
            }

            chatAreaUI.append(t);
            chatAreaUI.setCaretPosition(chatAreaUI.getDocument().getLength());
        });
    }


     // JSON 文字列としてエスケープする簡易関数
     private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 20);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        // control characters
                        sb.append(String.format("\\u%04x", (int)c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    // JSON 内でエスケープされた文字列を元に戻す（\n -> newline など）
    private static String unescapeJson(String s) {
        if (s == null) return null;
        // まず連続した二重バックスラッシュを正規化する（\\ => \ を繰り返す）
        // これにより、サーバが二重にエスケープした場合でも最終的に単純なエスケープ列にできる
        String normalized = s;
        while (normalized.contains("\\\\")) {
            String next = normalized.replace("\\\\", "\\");
            if (next.equals(normalized)) break;
            normalized = next;
        }
        s = normalized;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case 'n': sb.append('\n'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case '"': sb.append('"'); i++; break;
                    case 'u':
                            // unicode escape (uXXXX) - we expect 4 hex digits after 'u'
                            if (i + 5 < s.length()) {
                                String hex = s.substring(i + 2, i + 6);
                                try {
                                    int code = Integer.parseInt(hex, 16);
                                    sb.append((char) code);
                                    i += 5;
                                } catch (NumberFormatException ex) {
                                    // invalid, keep literal 'u'
                                    sb.append('u');
                                }
                            } else {
                                sb.append('u');
                            }
                            break;
                    default:
                        // unknown escape, keep next char as-is
                        sb.append(next);
                        i++;
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // -----------------------------
    // Ollama 応答パース用ユーティリティ（sendToOllama の前に定義）
    // -----------------------------
    // 行内 JSON から指定キーの文字列値を抽出する（簡易）
    private static String extractJsonStringValue(String line, String key) {
        if (line == null || key == null) return null;
        // パターン: "key"\s*:\s*"((?:\\.|[^\\"])*)"
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\\\\\"])*?)\"");
        Matcher m = p.matcher(line);
        if (m.find()) {
            String raw = m.group(1);
            // raw は JSON エスケープされた中身（例: Hello\\nWorld）
            return unescapeJson(raw);
        }
        return null;
    }

    // 行内 JSON から指定キーの boolean 値を抽出する（簡易）
    private static Boolean extractJsonBooleanValue(String line, String key) {
        if (line == null || key == null) return null;
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(line);
        if (m.find()) {
            return Boolean.valueOf(m.group(1).toLowerCase());
        }
        return null;
    }

    private static String getTemplate () {
        // -----------------------------
        // 初期テンプレート（型を意識させる構造）
        // -----------------------------
            return
                "public class UserCode {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        // --------------------------------\n" +
                        "        // ここに変数を宣言、値を入力して以下の式の処理が通るように実装してください\n" +
                        "        // age / height / name / isStudent\n" +
                        "        // --------------------------------\n\n\n" +

                        "        // --------------------------------\n" +
                        "        // ※以下は変更しないこと\n" +
                        "        // --------------------------------\n" +

                        "        // ① 年齢に10を足して futureAge に代入（int）\n" +
                        "        int futureAge = age + 10;\n\n" +

                        "        // ② 身長(cm)をメートルに変換して heightMeter に代入（double）\n" +
                        "        double heightMeter = height / 100;\n\n" +

                        "        // ③ 名前の文字数を nameLength に代入（int）\n" +
                        "        int nameLength = name.length();\n\n" +

                        "        // ④ 学生かどうかでメッセージを切り替える（boolean → if）\n" +
                        "        String studentMessage;\n" +
                        "        if (isStudent) {\n" +
                        "            studentMessage = \"学生です\";\n" +
                        "        } else {\n" +
                        "            studentMessage = \"学生ではありません\";\n" +
                        "        }\n\n" +

                        "        // --------------------------------\n" +
                        "        // 結果出力\n\n" +
                        "        // --------------------------------\n" +
                        "        System.out.println(\"10年後の年齢: \" + futureAge);\n" +
                        "        System.out.println(\"身長(メートル): \" + heightMeter);\n" +
                        "        System.out.println(\"名前の文字数: \" + nameLength);\n" +
                        "        System.out.println(\"学生判定: \" + studentMessage);\n" +
                        "    }\n" +
                        "}";
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // -----------------------------
            // ソースコードを保存
            // -----------------------------
            File file = new File("UserCode.java");
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(codeArea.getText());
            }

            // -----------------------------
            // コンパイル
            // -----------------------------
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            int result = compiler.run(null, null, err, file.getPath());

            if (result != 0) {
                errorArea.setText("コンパイルエラー:\n" + err.toString());
                outputArea.setText("");
                return;
            }

            errorArea.setText("");

            // -----------------------------
            // 実行
            // -----------------------------
            Process p = Runtime.getRuntime().exec("java UserCode");
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "Shift_JIS"));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            outputArea.setText(sb.toString());

        } catch (Exception ex) {
            errorArea.setText("エラー: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Variables();
    }
}
