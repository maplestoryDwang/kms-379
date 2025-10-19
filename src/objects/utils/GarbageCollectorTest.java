package objects.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import objects.summoned.Summoned;

public class GarbageCollectorTest {
   public static void main(String[] args) {
      try {
         Summoned summoned = null;
         summoned.getHP();
      } catch (Exception var5) {
         System.out.println("GC Error");
         var5.printStackTrace();
      }

      List<Integer> li = IntStream.range(1, 100).boxed().collect(Collectors.toList());
      int i = 1;

      while (true) {
         if (i % 50 == 0) {
            try {
               Thread.sleep(200L);
            } catch (InterruptedException var4) {
            }
         }

         IntStream.range(0, 100).forEach(li::add);
         i++;
      }
   }

   public static void test(List<String> list) {
      for (int i = 0; i < 100; i++) {
         list.add("Test" + i);
      }
   }
}
