public class Profile {
    String name;
    int age;
    double height;
    boolean isStudent;
    char grade;

    public Profile(Object name, Object age, Object height, Object isStudent, Object grade) {
        this.name = String.valueOf(name);
        this.age = (int) age;
        this.height = castHeight(height);
        this.isStudent = (boolean) isStudent;
        this.grade = (char) grade;
    }

    private double castHeight(Object height) {
        if (!(height instanceof Double)) {
            throw new IllegalArgumentException("height must be a double");
        }
        return (double) height;
    }
}
