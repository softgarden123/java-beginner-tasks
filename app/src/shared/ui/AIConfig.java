package shared.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AIConfig {

    private static final Path CONFIG_PATH = Paths.get("saved", "ai_chat.properties");

    public String endpoint = "http://localhost:11434";
    public String model = "qwen2.5:3b";
    public int timeoutSeconds = 15;

    public static AIConfig load() {
        AIConfig cfg = new AIConfig();
        try {
            if (Files.exists(CONFIG_PATH)) {
                Properties p = new Properties();
                try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
                    p.load(in);
                }
                cfg.endpoint = p.getProperty("endpoint", cfg.endpoint);
                cfg.model = p.getProperty("model", cfg.model);
                cfg.timeoutSeconds = Integer.parseInt(p.getProperty("timeoutSeconds", String.valueOf(cfg.timeoutSeconds)));
            }
        } catch (Exception ignored) {
        }
        return cfg;
    }

    public void save() throws IOException {
        if (CONFIG_PATH.getParent() != null) Files.createDirectories(CONFIG_PATH.getParent());
        Properties p = new Properties();
        p.setProperty("endpoint", endpoint);
        p.setProperty("model", model);
        p.setProperty("timeoutSeconds", String.valueOf(timeoutSeconds));
        try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
            p.store(out, "AI Chat configuration");
        }
    }
}
