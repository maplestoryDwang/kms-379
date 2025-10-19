package objects.utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Timer {
   private ScheduledThreadPoolExecutor ses;
   protected String file;
   protected String name;
   private static final AtomicLong threadNumber = new AtomicLong(1L);

   public void start() {
      if (this.ses == null || this.ses.isShutdown() || this.ses.isTerminated()) {
         this.file = "Log_" + this.name + "_Except.rtf";
         this.ses = new ScheduledThreadPoolExecutor(30, new Timer.RejectedThreadFactory());
         this.ses.setKeepAliveTime(10L, TimeUnit.MINUTES);
         this.ses.allowCoreThreadTimeOut(true);
         this.ses.setMaximumPoolSize(50);
         this.ses.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
      }
   }

   public ScheduledThreadPoolExecutor getSES() {
      return this.ses;
   }

   public void stop() {
      if (this.ses != null) {
         this.ses.shutdown();
      }
   }

   public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
      return this.ses == null ? null : this.ses.scheduleAtFixedRate(new Timer.LoggingSaveRunnable(r, this.file), delay, repeatTime, TimeUnit.MILLISECONDS);
   }

   public ScheduledFuture<?> register(Runnable r, long repeatTime) {
      return this.ses == null ? null : this.ses.scheduleAtFixedRate(new Timer.LoggingSaveRunnable(r, this.file), 0L, repeatTime, TimeUnit.MILLISECONDS);
   }

   public ScheduledFuture<?> schedule(Runnable r, long delay) {
      return this.ses == null ? null : this.ses.schedule(new Timer.LoggingSaveRunnable(r, this.file), delay, TimeUnit.MILLISECONDS);
   }

   public ScheduledFuture<?> scheduleAtTimestamp(Runnable r, long timestamp) {
      return this.schedule(r, timestamp - System.currentTimeMillis());
   }

   public static class BuffTimer extends Timer {
      private static Timer.BuffTimer instance = new Timer.BuffTimer();

      private BuffTimer() {
         this.name = "Bufftimer";
      }

      public static Timer.BuffTimer getInstance() {
         return instance;
      }
   }

   public static class CheatTimer extends Timer {
      private static Timer.CheatTimer instance = new Timer.CheatTimer();

      private CheatTimer() {
         this.name = "Cheattimer";
      }

      public static Timer.CheatTimer getInstance() {
         return instance;
      }
   }

   public static class CloneTimer extends Timer {
      private static Timer.CloneTimer instance = new Timer.CloneTimer();

      private CloneTimer() {
         this.name = "Clonetimer";
      }

      public static Timer.CloneTimer getInstance() {
         return instance;
      }
   }

   public static class EtcTimer extends Timer {
      private static Timer.EtcTimer instance = new Timer.EtcTimer();

      private EtcTimer() {
         this.name = "Etctimer";
      }

      public static Timer.EtcTimer getInstance() {
         return instance;
      }
   }

   public static class EventTimer extends Timer {
      private static Timer.EventTimer instance = new Timer.EventTimer();

      private EventTimer() {
         this.name = "Eventtimer";
      }

      public static Timer.EventTimer getInstance() {
         return instance;
      }
   }

   public static class HeartBeatTimer extends Timer {
      private static Timer.HeartBeatTimer instance = new Timer.HeartBeatTimer();

      private HeartBeatTimer() {
         this.name = "HeartBeatTimer";
      }

      public static Timer.HeartBeatTimer getInstance() {
         return instance;
      }
   }

   public static class HottimeTimer extends Timer {
      private static Timer.HottimeTimer instance = new Timer.HottimeTimer();

      private HottimeTimer() {
         this.name = "HottimeTimer";
      }

      public static Timer.HottimeTimer getInstance() {
         return instance;
      }
   }

   private static class LoggingSaveRunnable implements Runnable {
      Runnable r;
      String file;

      public LoggingSaveRunnable(Runnable r, String file) {
         this.r = r;
         this.file = file;
      }

      @Override
      public void run() {
         if (this.r != null) {
            try {
               this.r.run();
            } catch (Throwable var2) {
               FileoutputUtil.outputFileError(this.file, var2);
            }
         }
      }
   }

   public static class MapTimer extends Timer {
      private static Timer.MapTimer instance = new Timer.MapTimer();

      private MapTimer() {
         this.name = "Maptimer";
      }

      public static Timer.MapTimer getInstance() {
         return instance;
      }
   }

   public static class PingTimer extends Timer {
      private static Timer.PingTimer instance = new Timer.PingTimer();

      private PingTimer() {
         this.name = "Pingtimer";
      }

      public static Timer.PingTimer getInstance() {
         return instance;
      }
   }

   private class RejectedThreadFactory implements ThreadFactory {
      private final AtomicLong threadNumber2 = new AtomicLong(1L);
      private final String tname = Timer.this.name + Randomizer.nextLong();

      public RejectedThreadFactory() {
      }

      @Override
      public Thread newThread(Runnable r) {
         Thread t = new Thread(r);
         t.setName(this.tname + "-W-" + Timer.threadNumber.getAndIncrement() + "-" + this.threadNumber2.getAndIncrement());
         return t;
      }
   }

   public static class SaveTimer extends Timer {
      private static Timer.SaveTimer instance = new Timer.SaveTimer();

      private SaveTimer() {
         this.name = "SaveTimer";
      }

      public static Timer.SaveTimer getInstance() {
         return instance;
      }
   }

   public static class ShowTimer extends Timer {
      private static Timer.ShowTimer instance = new Timer.ShowTimer();

      private ShowTimer() {
         this.name = "ShowTimer";
      }

      public static Timer.ShowTimer getInstance() {
         return instance;
      }
   }

   public static class WorldTimer extends Timer {
      private static Timer.WorldTimer instance = new Timer.WorldTimer();

      private WorldTimer() {
         this.name = "Worldtimer";
      }

      public static Timer.WorldTimer getInstance() {
         return instance;
      }
   }
}
