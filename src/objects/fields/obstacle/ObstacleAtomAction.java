package objects.fields.obstacle;

public class ObstacleAtomAction {
   private Runnable runnable;
   private long startTime;
   private int duration;
   private int interval;

   public Runnable getRunnable() {
      return this.runnable;
   }

   public void setRunnable(Runnable runnable) {
      this.runnable = runnable;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public int getInterval() {
      return this.interval;
   }

   public void setInterval(int interval) {
      this.interval = interval;
   }
}
