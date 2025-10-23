import java.util.Arrays;
import pacage.Class;

public class Main {
    public static void main(String[] args) {

        Class myClass = new Class("Example", Arrays.asList(1, 2, 3, 4, 5));
        System.out.println("Class Name: " + myClass.getName());
        System.out.println("Values: " + myClass.getValues());
    }
}