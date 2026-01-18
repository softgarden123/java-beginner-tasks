public enum Task {
    INTRO("はじめに", "Readme"),
    HELLO_WORLD("① Hello World", "../01_HelloWorld/HelloWorld.exe"),
    VARIABLES("② 変数とデータ型", "../02_Variables/Variables.exe"),
    CONDITIONS("③ 条件分岐", "../03_Conditions/Conditions.exe"),
    LOOP("④ 繰り返し処理", "../04_Loop/Loop.exe"),
    METHODS("⑤ メソッドの定義と呼び出し", "../05_Calculator/Calculator.exe"),
    OBJECTS("⑥ クラスとオブジェクト", "../06_Objects/Objects.exe"),
    CONSTRUCTOR("⑦ コンストラクタとオーバーロード", "../07_Constructor/Constructor.exe"),
    ARRAYS("⑧ 配列とArrayList", "../08_Arrays/Arrays.exe");

    private final String name;
    private final String exePath;

    Task(String name, String exePath) {
        this.name = name;
        this.exePath = exePath;
    }

    public String getName() {
        return name;
    }

    public String getExePath() {
        return exePath;
    }
}
