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
                .append(profile.getName())
                .append("さん（")
                .append(profile.getAge())
                .append("歳、身長")
                .append(profile.getHeight())
                .append("cm）。学生：")
                .append(profile.isMale() ? "男性" : "女性")
                .append("、評価：")
                .append(profile.getGrade());
        area.setText(sb.toString());
    }
}
