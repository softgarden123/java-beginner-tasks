public class Profile {
    private final String name;
    private final int age;
    private final double height;
    private final char grade;
    private final boolean isMale;

    public Profile(Object name, Object age, Object height, Object grade, Object isMale) {
        this.name = String.valueOf(name);
        this.age = (int) age;
        this.height = castHeight(height);
        this.grade = (char) grade;
        this.isMale = (boolean) isMale;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public double getHeight() {
        return this.height;
    }

    public char getGrade() {
        return this.grade;
    }

    public boolean isMale() {
        return this.isMale;
    }

    private double castHeight(Object height) {
        if (!(height instanceof Double)) {
            throw new IllegalArgumentException("height must be a double");
        }
        return (double) height;
    }
}
