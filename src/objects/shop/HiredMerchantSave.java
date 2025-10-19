package objects.shop;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiredMerchantSave {
   public static final int NumSavingThreads = 5;
   private static final HiredMerchantSave.TimingThread[] Threads = new HiredMerchantSave.TimingThread[5];
   private static final AtomicInteger Distribute;

   public static void QueueShopForSave(HiredMerchant hm) {
      int Current = Distribute.getAndIncrement() % 5;
      Threads[Current].getRunnable().Queue(hm);
   }

   public static void Execute(Object ToNotify) {
      for (int i = 0; i < Threads.length; i++) {
         Threads[i].getRunnable().SetToNotify(ToNotify);
      }

      for (int i = 0; i < Threads.length; i++) {
         Threads[i].start();
      }
   }

   static {
      for (int i = 0; i < Threads.length; i++) {
         Threads[i] = new HiredMerchantSave.TimingThread(new HiredMerchantSave.HiredMerchantSaveRunnable());
      }

      Distribute = new AtomicInteger(0);
   }

   private static class HiredMerchantSaveRunnable implements Runnable {
      private static AtomicInteger RunningThreadID = new AtomicInteger(0);
      private int ThreadID = RunningThreadID.incrementAndGet();
      private long TimeTaken = 0L;
      private int ShopsSaved = 0;
      private Object ToNotify;
      private ArrayBlockingQueue<HiredMerchant> Queue = new ArrayBlockingQueue<>(500);

      @Override
      public void run() {
         try {
            while (true) {
               if (!this.Queue.isEmpty()) {
                  HiredMerchant next = this.Queue.take();
                  long Start = System.currentTimeMillis();
                  next.closeShop(true, false);
                  this.TimeTaken = this.TimeTaken + (System.currentTimeMillis() - Start);
                  this.ShopsSaved++;
               } else {
                  System.out
                     .println(
                        "[HiredMerchantSave Thread "
                           + this.ThreadID
                           + "] Shops Saved: "
                           + this.ShopsSaved
                           + " | Time Taken: "
                           + this.TimeTaken
                           + " Milliseconds"
                     );
                  synchronized (this.ToNotify) {
                     this.ToNotify.notify();
                     break;
                  }
               }
            }
         } catch (InterruptedException var6) {
            Logger.getLogger(HiredMerchantSave.class.getName()).log(Level.SEVERE, null, var6);
         }
      }

      private void Queue(HiredMerchant hm) {
         this.Queue.add(hm);
      }

      private void SetToNotify(Object o) {
         if (this.ToNotify == null) {
            this.ToNotify = o;
         }
      }
   }

   private static class TimingThread extends Thread {
      private final HiredMerchantSave.HiredMerchantSaveRunnable ext;

      public TimingThread(HiredMerchantSave.HiredMerchantSaveRunnable r) {
         super(r);
         this.ext = r;
      }

      public HiredMerchantSave.HiredMerchantSaveRunnable getRunnable() {
         return this.ext;
      }
   }
}
