package objects.utils;

public class WaitNotifyTest {
   public static Object a = new Object();

   public static int selectzz() {
      try {
         synchronized (a) {
            a.wait();
         }
      } catch (InterruptedException var3) {
      }

      return 1;
   }

   public static void main(String[] args) {
      Timer.BuffTimer.getInstance().start();
      Timer.BuffTimer.getInstance().register(new Runnable() {
         @Override
         public void run() {
            synchronized (WaitNotifyTest.a) {
               WaitNotifyTest.a.notifyAll();
            }
         }
      }, 3000L);

      while (true) {
         int selected = selectzz();
         System.out.println(selected);
         System.out.println("이것은 테스트 인것이여~2");
      }
   }
}
