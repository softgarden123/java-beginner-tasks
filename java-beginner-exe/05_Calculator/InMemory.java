import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class InMemory extends SimpleJavaFileObject {
    private final String code;

    public InMemory(String className, String code) {
        super(URI.create("string:///" + className + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
