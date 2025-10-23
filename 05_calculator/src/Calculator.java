public class Calculator {

    public static int calculate(int a, int b, Type type) {
        switch (type) {
            case ADD:
                // 足し算の処理を新たなメソッドに分離して、そのメソッドをここで呼び出してください。
            case SUBTRACT:
                // 引き算の処理を新たなメソッドに分離して、そのメソッドをここで呼び出してください。
            case MULTIPLY:
                // 掛け算の処理を新たなメソッドに分離して、そのメソッドをここで呼び出してください。
            case DIVIDE:
                // 割り算の処理を新たなメソッドに分離して、そのメソッドをここで呼び出してください。
            default:
                throw new IllegalArgumentException("Invalid operation type.");
        }
    }

    // TODO 足し算

    // TODO 引き算

    // TODO 掛け算

    // TODO 割り算

}
