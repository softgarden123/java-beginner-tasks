import javax.swing.*;

public class TextAreaRenderer implements OutputRenderer {

    @Override
    public void render(Profile profile, Object outputTarget) {
        if (!(outputTarget instanceof JTextArea)) {
            throw new IllegalArgumentException("JTextArea以外は対応していません");
        }

        JTextArea area = (JTextArea) outputTarget;

        StringBuilder sb = new StringBuilder();
        sb.append("こんにちは")
                .append(profile.name)
                .append("さん（")
                .append(profile.age)
                .append("歳、身長")
                .append(profile.height)
                .append("cm）。学生：")
                .append(profile.isStudent)
                .append("、評価：")
                .append(profile.grade);
        area.setText(sb.toString());
    }
}
