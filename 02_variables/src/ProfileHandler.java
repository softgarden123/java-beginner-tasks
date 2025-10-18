import javax.swing.*;

public class ProfileHandler{
    private final JTextField nameField;
    private final JTextField ageField;
    private final JTextField heightField;
    private final JTextField studentField;
    private final JTextField gradeField;

    protected ProfileHandler(JTextField nameField, JTextField ageField,
                             JTextField heightField, JTextField studentField,
                             JTextField gradeField) {
        this.nameField = nameField;
        this.ageField = ageField;
        this.heightField = heightField;
        this.studentField = studentField;
        this.gradeField = gradeField;
    }

    // TODO: Profileオブジェクトを生成して返すメソッドを実装してください。
    public Profile createProfile() {
        // フィールド変数はthis.でアクセス可能で、getText()で値を取得できる
        // 例：this.name.getText()



        return new Profile(name, age, height, student, grade);
    }
}
