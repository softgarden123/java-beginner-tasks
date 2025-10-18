import javax.swing.*;

public class ProfileHandler{
    private final JTextField nameField;
    private final JTextField ageField;
    private final JTextField heightField;
    private final JTextField gradeField;
    private final JComboBox<String> sexComboBox;

    protected ProfileHandler(JTextField nameField, JTextField ageField,
                             JTextField heightField, JTextField gradeField, JComboBox<String> sexComboBox) {
        this.nameField = nameField;
        this.ageField = ageField;
        this.heightField = heightField;
        this.gradeField = gradeField;
        this.sexComboBox = sexComboBox;
    }

    // TODO: Profileオブジェクトを生成して返すメソッドを実装してください。
    public Profile createProfile() {
        // フィールド変数はthis.でアクセス可能で、getText()で値を取得できる
        // 例：this.nameField.getText()

        // 性別は this.sexField.getSelectedItem() で取得できる

        return new Profile(name, age, height, grade, isMale);
    }
}
