package objects.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConsumerTest {
   private int testA = 0;
   private int testB = 0;
   private int testC = 0;
   List<Consumer<ConsumerTest>> list = new ArrayList<>();

   public static void main(String[] args) {
      ConsumerTest test = new ConsumerTest();
      test.aaa();
   }

   public void aaa() {
      this.bbb(this.test(1, 2), this.test2(3, 2, 1));

      for (Consumer<ConsumerTest> l : this.list) {
         l.accept(this);
         System.out.println(this.testA + " " + this.testB);
      }
   }

   public void bbb(Consumer<ConsumerTest>... test) {
      for (Consumer<ConsumerTest> t : test) {
         this.list.add(t);
      }
   }

   public Consumer<ConsumerTest> test(int a, int b) {
      return c -> {
         this.testA = a;
         this.testB = b;
      };
   }

   public Consumer<ConsumerTest> test2(int a, int b, int c) {
      return cc -> {
         this.testA = a;
         this.testB = b;
         this.testC = c;
      };
   }
}
