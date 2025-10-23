package pacage;

import java.util.List;

public class Class {
    private final String name;
    private List<Integer> values;

    public Class(String name, List<Integer> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getValues() {
        return values;
    }
}
