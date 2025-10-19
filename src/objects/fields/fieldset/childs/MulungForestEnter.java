package objects.fields.fieldset.childs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import network.game.GameServer;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.fieldset.instance.MulungForest;
import objects.users.MapleCharacter;

public class MulungForestEnter extends FieldSet {
   public MulungForestEnter(int channel) {
      super("MulungForestEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(993198000))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 200;
      this.maxLv = 1000;
   }

   @Override
   public void updateFieldSetPS() {
      synchronized (this.fInstance) {
         for (FieldSetInstance fis : this.fInstance.keySet()) {
            int userCount = 0;

            for (Integer map : fis.fsim.instances) {
               userCount += this.field(map).getCharacters().size();
            }

            if (userCount == 0) {
               for (Integer map : fis.fsim.instances) {
                  this.field(map).setFieldSetInstance(null);
               }

               this.fInstance.remove(fis);
            }
         }
      }
   }

   @Override
   public synchronized int enter(int CharacterID, int tier) {
      synchronized (this.fInstance) {
         int enterInteger = this.tryEnterPartyQuest(CharacterID, false, false);
         GameServer gs = GameServer.getInstance(this.channel);
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
         if (enterInteger > 6 && nCharacter != null) {
            for (MapleCharacter c : nCharacter.getMap().getCharacters()) {
               if (c.getParty() != null && c.getParty().getId() == nCharacter.getParty().getId()) {
                  int count = c.getOneInfoQuestInteger(100936, "count");
                  if (count > 0) {
                     String customData = c.getOneInfo(100936, "date");

                     try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
                        Date date0 = sdf.parse(customData);
                        String dateString = sdf.format(new Date());
                        Date date1 = sdf.parse(dateString);
                        if (date0.compareTo(date1) >= 0 && count >= 5) {
                           return -1;
                        }
                     } catch (ParseException var16) {
                        return -1;
                     }
                  }
               }
            }
         }

         if (enterInteger > 6 && nCharacter != null) {
            for (FieldSetInstanceMap fsim : this.instanceMaps) {
               if (fsim.instances.get(0) == enterInteger) {
                  FieldSetInstance fsi = new MulungForest(this, fsim, nCharacter);
                  this.transferFieldAll(enterInteger, this.fInstance.get(fsi));
                  break;
               }
            }
         }

         return enterInteger;
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
}
