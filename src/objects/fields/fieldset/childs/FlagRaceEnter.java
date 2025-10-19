package objects.fields.fieldset.childs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import network.game.GameServer;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.fieldset.instance.FlagRace;
import objects.users.MapleCharacter;
import objects.utils.Timer;

public class FlagRaceEnter extends FieldSet {
   public boolean isOpen = false;
   public FieldSetInstance fsi = null;

   public FlagRaceEnter(int channel) {
      super("FlagRaceEnter", channel);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

      try {
         Date date1 = sdf.parse("20210802");
         Date date2 = new Date();
         long time = date2.getTime() - date1.getTime();
         long day = TimeUnit.MILLISECONDS.toDays(time);
         if (day / 7L % 3L == 0L) {
            this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(942000000, 942000500))));
         } else if (day / 7L % 3L == 1L) {
            this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(942001000, 942001500))));
         } else if (day / 7L % 3L == 2L) {
            this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(942002000, 942002500))));
         }
      } catch (ParseException var9) {
         var9.printStackTrace();
      }

      this.minMember = 1;
      this.maxMember = 1;
      this.minLv = 101;
      this.maxLv = 300;
      final FlagRaceEnter t = this;
      Timer.EventTimer.getInstance().register(new Runnable() {
         @Override
         public void run() {
            if (new Date().getMinutes() == 30 && !FlagRaceEnter.this.isOpen) {
               t.isOpen = true;
               t.fsi = new FlagRace(t, t.instanceMaps.get(0), null);
               t.closeGame();
            }
         }
      }, 10000L, 0L);
   }

   @Override
   public void updateFieldSetPS() {
      synchronized (this.fInstance) {
         ;
      }
   }

   @Override
   public synchronized int enter(int CharacterID, int tier) {
      synchronized (this.fInstance) {
         int enterInteger = this.tryEnterFlagRace(CharacterID);
         GameServer gs = GameServer.getInstance(this.channel);
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
         Date date = new Date();
         if ((date.getDay() != 0 || date.getHours() != 23) && (date.getDay() != 1 || date.getHours() >= 1)) {
            if (enterInteger > 6 && this.isOpen && this.fsi != null && nCharacter != null) {
               this.transferFieldAll(enterInteger, new ArrayList<>(Arrays.asList(nCharacter.getId())));
               this.fsi.userList.add(nCharacter.getId());
               return enterInteger;
            } else {
               return -2;
            }
         } else {
            return -3;
         }
      }
   }

   @Override
   public synchronized int startManually() {
      return 1;
   }

   @Override
   public String getQuestTime(int CharacterID) {
      return "";
   }

   @Override
   public void resetQuestTime() {
   }

   public void closeGame() {
      Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            FlagRaceEnter.this.isOpen = false;
         }
      }, 120000L);
   }
}
