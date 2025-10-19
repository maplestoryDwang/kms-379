package objects.users;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Fuck {
   public static void main(String[] args) {
      Map<String, Integer> test = new ConcurrentHashMap<>();
      test.put("A", 123);
      test.put("B", 456);
      test.put("C", 123);

      for (String s : test.keySet()) {
         test.remove(s);
      }
   }
}
