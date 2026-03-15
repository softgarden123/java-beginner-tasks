import java.util.ArrayList;
public class UserCode {
    public static void main(String[] args) {
      // サイズが3の配列(String型)、fluteArrayを作成してください
      String[] fluteArray = new String[3];

      fluteArray[0] = "リンゴ";
      fluteArray[1] = "ブドウ";
      fluteArray[2] = "レモン";

      System.out.println("1つ目の果物は" + fluteArray[0] + "です。");
      System.out.println("2つ目の果物は" + fluteArray[1] + "です。");
      System.out.println("3つ目の果物は" + fluteArray[2] + "です。");
      System.out.println("配列のサイズは" + fluteArray.length + "です。");

      // ArrayList(String型)、nameListを作成してください。
      ArrayList<String> nameList = new ArrayList<>();

      // nameListに、任意の数の名前を入れてください。
      // addメソッドを使用することで、オブジェクトを追加することができます。

      for (int i = 0; i < nameList.size(); i++) {
        String name = nameList.get(i);
        System.out.println(i + "人目の名称は" + name + "です");
      }
      System.out.println("名前のリストのサイズは" + nameList.size() + "です");
    }
}
