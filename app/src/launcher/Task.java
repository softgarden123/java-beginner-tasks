package launcher;

public enum Task {
    INTRO("はじめに", null),
    HELLO_WORLD("① Hello World", "task01.HelloWorld"),
    VARIABLES("② 変数とデータ型", "task02.Variables"),
    COND_IF("③-1 if / else if / else の分岐理解", "task03.part1.ConditionsPart1"),
    COND_SWITCH("③-2 switch / case の分岐理解", "task03.part2.ConditionsPart2"),
    LOOP("④ 繰り返し処理", "task04.Loop"),
    CALC_STRUCT("⑤-1 メソッドの構造を理解する", "task05.part1.CalculatorPart1"),
    CALC_CALL("⑤-2 メソッドの呼び出しを理解する", "task05.part2.CalculatorPart2"),
    CALC_CREATE("⑤-3 メソッドの作成と呼び出し", "task05.part3.CalculatorPart3"),
    CLASS_PART1("⑥-1 クラスの構造を理解する", "task06.part1.Task06Part1"),
    CLASS_PART2("⑥-2 インスタンス化を理解する", "task06.part2.Task06Part2"),
    CLASS_PART3("⑥-3 setter を作成する", "task06.part3.Task06Part3"),
    CLASS_PART4("⑥-4 フィールド情報を出力する", "task06.part4.Task06Part4"),
    CONSTRUCTOR_PART1("⑦-1 コンストラクタを理解する", "task07.part1.Task07Part1"),
    CONSTRUCTOR_PART2("⑦-2 オーバーロードを理解する", "task07.part2.Task07Part2"),
    CONSTRUCTOR_PART3("⑦-3 結果をフィールドから出力する", "task07.part3.Task07Part3"),
    ARRAY_PART1("⑧-1 配列を理解する", "task08.part1.Task08Part1"),
    ARRAY_PART2("⑧-2 ArrayListを理解する", "task08.part2.Task08Part2"),
    ARRAY_PART3("⑧-3 ArrayListとfor文の組み合わせ", "task08.part3.Task08Part3"),
    APPLICATION("⑨ 最終課題", "task09.Application");

    private final String name;
    private final String mainClassName;

    Task(String name, String mainClassName) {
        this.name = name;
        this.mainClassName = mainClassName;
    }

    public String getName() {
        return name;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public boolean isReadme() {
        return mainClassName == null;
    }
}
