package objects.users.stats;

import java.util.concurrent.ScheduledFuture;

public class SecondaryStatValueHolder {
   public SecondaryStatEffect effect;
   public long startTime;
   public int value;
   public int localDuration;
   public int cid;
   public ScheduledFuture<?> schedule;

   public SecondaryStatValueHolder(SecondaryStatEffect effect, long startTime, ScheduledFuture<?> schedule, int value, int localDuration, int cid) {
      this.effect = effect;
      this.startTime = startTime;
      this.schedule = schedule;
      this.value = value;
      this.localDuration = localDuration;
      this.cid = cid;
   }
}
