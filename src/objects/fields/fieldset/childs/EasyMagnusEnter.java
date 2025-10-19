package objects.fields.fieldset.childs;

import database.DBConfig;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import network.game.GameServer;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.fieldset.instance.EasyMagnusBoss;
import objects.users.MapleCharacter;

public class EasyMagnusEnter extends FieldSet {
   public EasyMagnusEnter(int channel) {
      super("EasyMagnusEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(401060300))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 115;
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
         int enterInteger = this.tryEnterPartyQuest(CharacterID, false, true);
         GameServer gs = GameServer.getInstance(this.channel);
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
         if (enterInteger > 6 && nCharacter != null) {
            for (MapleCharacter c : nCharacter.getMap().getCharacters()) {
               if (DBConfig.isGanglim && c.getBossTier() < tier) {
                  return -5;
               }

               if (c.getParty() != null && c.getParty().getId() == nCharacter.getParty().getId()) {
                  if (!DBConfig.isGanglim && nCharacter.getPartyMemberSize() > 1) {
                     c.applyBMCurseJinMulti();
                  }

                  String lasttime = c.getOneInfo(3996, "lasttime");
                  if (lasttime != null) {
                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                     try {
                        Date date0 = sdf.parse(lasttime);
                        String dateString = sdf.format(new Date());
                        Date date1 = sdf.parse(dateString);
                        if (date0.getDate() == date1.getDate() && date0.getMonth() == date1.getMonth() && date0.getYear() == date1.getYear()) {
                           return c.getId() * -1;
                        }
                     } catch (ParseException var15) {
                        return -1;
                     }
                  }
               }
            }
         }

         if (enterInteger > 6 && nCharacter != null) {
            for (FieldSetInstanceMap fsim : this.instanceMaps) {
               if (fsim.instances.get(0) == enterInteger) {
                  FieldSetInstance fsi = new EasyMagnusBoss(this, fsim, nCharacter);
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
