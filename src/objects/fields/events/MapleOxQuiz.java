package objects.fields.events;

import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.users.MapleStat;
import objects.utils.Pair;
import objects.utils.Timer;

public class MapleOxQuiz extends MapleEvent {
   private ScheduledFuture<?> oxSchedule;
   private ScheduledFuture<?> oxSchedule2;
   private int timesAsked = 0;
   private boolean finished = false;

   public MapleOxQuiz(int channel, MapleEventType type) {
      super(channel, type);
   }

   @Override
   public void finished(MapleCharacter chr) {
   }

   private void resetSchedule() {
      if (this.oxSchedule != null) {
         this.oxSchedule.cancel(false);
         this.oxSchedule = null;
      }

      if (this.oxSchedule2 != null) {
         this.oxSchedule2.cancel(false);
         this.oxSchedule2 = null;
      }
   }

   @Override
   public void onMapLoad(MapleCharacter chr) {
      super.onMapLoad(chr);
      if (chr.getMapId() == this.type.mapids[0] && !chr.isGM()) {
         chr.canTalk(false);
      }
   }

   @Override
   public void reset() {
      super.reset();
      this.getMap(0).getPortal("join00").setPortalState(false);
      this.resetSchedule();
      this.timesAsked = 0;
   }

   @Override
   public void unreset() {
      super.unreset();
      this.getMap(0).getPortal("join00").setPortalState(true);
      this.resetSchedule();
   }

   @Override
   public void startEvent() {
      this.sendQuestion();
      this.finished = false;
   }

   public void sendQuestion() {
      this.sendQuestion(this.getMap(0));
   }

   public void sendQuestion(final Field toSend) {
      final Entry<Pair<Integer, Integer>, MapleOxQuizFactory.MapleOxQuizEntry> question = MapleOxQuizFactory.getInstance().grabRandomQuestion();
      if (this.oxSchedule2 != null) {
         this.oxSchedule2.cancel(false);
      }

      this.oxSchedule2 = Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            int number = 0;

            for (MapleCharacter mc : toSend.getCharactersThreadsafe()) {
               if (mc.isGM() || !mc.isAlive()) {
                  number++;
               }
            }

            if (toSend.getCharactersSize() - number > 1 && MapleOxQuiz.this.timesAsked != 10) {
               toSend.broadcastMessage(CField.showOXQuiz((Integer)question.getKey().left, (Integer)question.getKey().right, true));
               toSend.broadcastMessage(CField.getClock(10));
            } else {
               toSend.broadcastMessage(CWvsContext.serverNotice(6, "The event has ended"));
               MapleOxQuiz.this.unreset();

               for (MapleCharacter chr : toSend.getCharactersThreadsafe()) {
                  if (chr != null && !chr.isGM() && chr.isAlive()) {
                     chr.canTalk(true);
                     MapleEvent.givePrize(chr);
                     MapleOxQuiz.this.warpBack(chr);
                  }
               }

               MapleOxQuiz.this.finished = true;
            }
         }
      }, 10000L);
      if (this.oxSchedule != null) {
         this.oxSchedule.cancel(false);
      }

      this.oxSchedule = Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!MapleOxQuiz.this.finished) {
               toSend.broadcastMessage(CField.showOXQuiz((Integer)question.getKey().left, (Integer)question.getKey().right, false));
               MapleOxQuiz.this.timesAsked++;

               for (MapleCharacter chr : toSend.getCharactersThreadsafe()) {
                  if (chr != null && !chr.isGM() && chr.isAlive()) {
                     if (!MapleOxQuiz.this.isCorrectAnswer(chr, question.getValue().getAnswer())) {
                        chr.getStat().setHp(0L, chr);
                        chr.updateSingleStat(MapleStat.HP, 0L);
                     } else {
                        chr.gainExp(3000.0, true, true, false);
                     }
                  }
               }

               MapleOxQuiz.this.sendQuestion();
            }
         }
      }, 20000L);
   }

   private boolean isCorrectAnswer(MapleCharacter chr, int answer) {
      double x = chr.getTruePosition().getX();
      double y = chr.getTruePosition().getY();
      if ((!(x > -234.0) || !(y > -26.0) || answer != 0) && (!(x < -234.0) || !(y > -26.0) || answer != 1)) {
         chr.dropMessage(6, "[Ox Quiz] Incorrect!");
         return false;
      } else {
         chr.dropMessage(6, "[Ox Quiz] Correct!");
         return true;
      }
   }
}
