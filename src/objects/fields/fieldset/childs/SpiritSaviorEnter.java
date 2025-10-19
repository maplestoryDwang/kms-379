package objects.fields.fieldset.childs;

import java.util.ArrayList;
import java.util.Arrays;
import network.game.GameServer;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.fieldset.instance.SpiritSavior;
import objects.users.MapleCharacter;

public class SpiritSaviorEnter extends FieldSet {
   public SpiritSaviorEnter(int channel) {
      super("SpiritSaviorEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(921172300))));
      this.minMember = 1;
      this.maxMember = 1;
      this.minLv = 225;
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
         int enterInteger = this.tryEnterSingleQuest(CharacterID);
         GameServer gs = GameServer.getInstance(this.channel);
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
         if (enterInteger > 6 && nCharacter != null && nCharacter.getOneInfoQuestInteger(16214, "count") >= 3) {
            return -2;
         } else {
            if (enterInteger > 6 && nCharacter != null) {
               for (FieldSetInstanceMap fsim : this.instanceMaps) {
                  if (fsim.instances.get(0) == enterInteger) {
                     FieldSetInstance fsi = new SpiritSavior(this, fsim, nCharacter);
                     this.transferFieldAll(enterInteger, this.fInstance.get(fsi));
                     break;
                  }
               }
            }

            return enterInteger;
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
}
