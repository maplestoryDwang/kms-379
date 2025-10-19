package objects.fields.events;

import java.util.concurrent.ScheduledFuture;
import network.models.CField;
import network.models.CWvsContext;
import objects.users.MapleCharacter;
import objects.utils.Timer;

public class MapleSurvival extends MapleEvent {
   protected long time = 360000L;
   protected long timeStarted = 0L;
   protected ScheduledFuture<?> olaSchedule;

   public MapleSurvival(int channel, MapleEventType type) {
      super(channel, type);
   }

   @Override
   public void finished(MapleCharacter chr) {
      givePrize(chr);
   }

   @Override
   public void onMapLoad(MapleCharacter chr) {
      super.onMapLoad(chr);
      if (this.isTimerStarted()) {
         chr.getClient().getSession().writeAndFlush(CField.getClock((int)(this.getTimeLeft() / 1000L)));
      }
   }

   @Override
   public void startEvent() {
      this.unreset();
      super.reset();
      this.broadcast(CField.getClock((int)(this.time / 1000L)));
      this.timeStarted = System.currentTimeMillis();
      this.olaSchedule = Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            for (int i = 0; i < MapleSurvival.this.type.mapids.length; i++) {
               for (MapleCharacter chr : MapleSurvival.this.getMap(i).getCharactersThreadsafe()) {
                  MapleSurvival.this.warpBack(chr);
               }

               MapleSurvival.this.unreset();
            }
         }
      }, this.time);
      this.broadcast(CWvsContext.serverNotice(0, "The portal has now opened. Press the up arrow key at the portal to enter."));
      this.broadcast(CWvsContext.serverNotice(0, "Fall down once, and never get back up again! Get to the top without falling down!"));
   }

   public boolean isTimerStarted() {
      return this.timeStarted > 0L;
   }

   public long getTime() {
      return this.time;
   }

   public void resetSchedule() {
      this.timeStarted = 0L;
      if (this.olaSchedule != null) {
         this.olaSchedule.cancel(false);
      }

      this.olaSchedule = null;
   }

   @Override
   public void reset() {
      super.reset();
      this.resetSchedule();
      this.getMap(0).getPortal("join00").setPortalState(false);
   }

   @Override
   public void unreset() {
      super.unreset();
      this.resetSchedule();
      this.getMap(0).getPortal("join00").setPortalState(true);
   }

   public long getTimeLeft() {
      return this.time - (System.currentTimeMillis() - this.timeStarted);
   }
}
