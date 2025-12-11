import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Lancher extends JFrame implements ActionListener {
    private String[] kadaiNames = {
            "① Hello World",
            "② 変数とデータ型",
            "③ 条件分岐",
            "④ 繰り返し処理",
            "⑤ メソッドの定義と呼び出し",
            "⑥ クラスとオブジェクト",
            "⑦ コンストラクタとオーバーロード",
            "⑧ 配列とArrayList"
    };

    private String[] exePaths = {
            "../01_HelloWorld/HelloWorld.exe",
            "../02_Variables/Variables.exe",
            "../03_Conditions/Conditions.exe",
            "../04_Loop/Loop.exe",
            "../05_Calculator/Calculator.exe",
            "../06_Objects/Objects.exe",
            "../07_Constructor/Constructor.exe",
            "../08_Arrays/Arrays.exe"
    };

    public Lancher() {
        setTitle("Java課題ランチャー");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(kadaiNames.length, 1));

        for (int i = 0; i < kadaiNames.length; i++) {
            JButton btn = new JButton(kadaiNames[i]);
            btn.setActionCommand(exePaths[i]);
            btn.addActionListener(this);
            add(btn);
        }

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String path = e.getActionCommand();
        try {
            File exe = new File(path);
            if (exe.exists()) {
                Runtime.getRuntime().exec(path);
            } else {
                JOptionPane.showMessageDialog(this, "実行ファイルが見つかりません: " + path);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "起動に失敗しました: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Lancher();
    }
}
