package scripting.newscripting;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ScriptThreadManager implements ScriptThreadManagerMBean {
   private static ScriptThreadManager instance = new ScriptThreadManager();
   private Executor _executor;
   private ExecutorService _executors;
   private ScheduledThreadPoolExecutor ses;

   private ScriptThreadManager() {
      MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

      try {
         mBeanServer.registerMBean(this, new ObjectName("server:type=TimerManger"));
      } catch (Exception var3) {
      }
   }

   public static ScriptThreadManager getInstance() {
      return instance;
   }

   public void start() {
      if (this.ses == null || this.ses.isShutdown() || this.ses.isTerminated()) {
         ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(30, new ThreadFactory() {
            private final AtomicLong threadNumber = new AtomicLong(1L);

            @Override
            public Thread newThread(Runnable r) {
               Thread t = new Thread(r);
               t.setName("TimerManager-Worker-" + this.threadNumber.getAndIncrement());
               t.setPriority(5);
               return t;
            }
         });
         stpe.setKeepAliveTime(10L, TimeUnit.MINUTES);
         stpe.allowCoreThreadTimeOut(true);
         stpe.setMaximumPoolSize(50);
         stpe.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
         this.ses = stpe;
         this._executor = Executors.newCachedThreadPool();
      }
   }

   public void execute(Runnable r) {
      this._executor.execute(r);
   }

   public void stop() {
      this.ses.shutdownNow();
   }

   public Runnable purge() {
      return new Runnable() {
         @Override
         public void run() {
            ScriptThreadManager.this.ses.purge();
         }
      };
   }

   public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
      return this.ses.scheduleAtFixedRate(new ScriptThreadManager.LoggingSaveRunnable(r), delay, repeatTime, TimeUnit.MILLISECONDS);
   }

   public ScheduledFuture<?> register(Runnable r, long repeatTime) {
      return this.ses.scheduleAtFixedRate(new ScriptThreadManager.LoggingSaveRunnable(r), 0L, repeatTime, TimeUnit.MILLISECONDS);
   }

   public ScheduledFuture<?> schedule(Runnable r, long delay) {
      return this.ses.schedule(new ScriptThreadManager.LoggingSaveRunnable(r), delay, TimeUnit.MILLISECONDS);
   }

   public ScheduledFuture<?> scheduleAtTimestamp(Runnable r, long timestamp) {
      return this.schedule(r, timestamp - System.currentTimeMillis());
   }

   @Override
   public long getActiveCount() {
      return this.ses.getActiveCount();
   }

   @Override
   public long getCompletedTaskCount() {
      return this.ses.getCompletedTaskCount();
   }

   @Override
   public int getQueuedTasks() {
      return this.ses.getQueue().toArray().length;
   }

   @Override
   public long getTaskCount() {
      return this.ses.getTaskCount();
   }

   @Override
   public boolean isShutdown() {
      return this.ses.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return this.ses.isTerminated();
   }

   private static class LoggingSaveRunnable implements Runnable {
      Runnable r;

      public LoggingSaveRunnable(Runnable r) {
         this.r = r;
      }

      @Override
      public void run() {
         try {
            this.r.run();
         } catch (Throwable var2) {
         }
      }
   }
}
