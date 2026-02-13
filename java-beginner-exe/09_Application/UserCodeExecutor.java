import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;

public class UserCodeExecutor {

    private File writeTempFile(String fileName, String code) throws IOException {
        File dir = new File("temp");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, fileName);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(code);
        }
        return file;
    }

    private boolean compileFiles(File... files) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("JDKが必要です（JREではコンパイルできません）");
            return false;
        }

        String[] filePaths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filePaths[i] = files[i].getPath();
        }

        int result = compiler.run(null, null, null, filePaths);
        return result == 0;
    }

    private void runMainClass(OutputHandler handler) throws Exception {

        String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String tempPath = new File("temp").getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c",
                "start", "cmd.exe", "/k",
                "\"" + javaPath + "\" -cp \"" + tempPath + "\" Main"
        );

        pb.start();

        handler.onOutput("別ウィンドウで実行を開始しました。");
    }

    public void execute(String mainCode, String itemCode, String cartCode, OutputHandler handler) {
        try {
            File mainFile = writeTempFile("Main.java", mainCode);
            File itemFile = writeTempFile("Item.java", itemCode);
            File cartFile = writeTempFile("Cart.java", cartCode);

            boolean success = compileFiles(mainFile, itemFile, cartFile);

            if (!success) {
                handler.onOutput("コンパイルエラーがあります");
                return;
            }

            runMainClass(handler);

        } catch (Exception e) {
            handler.onOutput("エラー: " + e.getMessage());
        }
    }

    public interface OutputHandler {
        void onOutput(String text);
    }
}
