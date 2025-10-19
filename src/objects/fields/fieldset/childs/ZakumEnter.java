package objects.fields.fieldset.childs;

import constants.QuestExConstants;
import database.DBConfig;
import java.util.ArrayList;
import java.util.Arrays;
import network.game.GameServer;
import objects.context.party.PartyMemberEntry;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.fieldset.instance.ZakumBoss;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;

public class ZakumEnter extends FieldSet {
   public ZakumEnter(int channel) {
      super("ZakumEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(280030100))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 90;
      this.maxLv = 1000;
   }

   @Override
   public void updateFieldSetPS() {
      synchronized (this.fInstance) {
         int totalUserCount = 0;

         for (FieldSetInstance fis : this.fInstance.keySet()) {
            int userCount = 0;

            for (Integer map : fis.fsim.instances) {
               int userSize = this.field(map).getCharacters().size();
               userCount += userSize;
               totalUserCount += userSize;
            }

            if (userCount == 0) {
               for (Integer map : fis.fsim.instances) {
                  this.field(map).setFieldSetInstance(null);
               }

               this.fInstance.remove(fis);
            }
         }

         if (!DBConfig.isGanglim && totalUserCount > 1) {
            for (FieldSetInstance fis : this.fInstance.keySet()) {
               for (Integer map : fis.fsim.instances) {
                  for (MapleCharacter chr : this.field(map).getCharacters()) {
                     if (chr.getBuffedValue(SecondaryStatFlag.BlackMageCursePmdReduce) == null) {
                        chr.temporaryStatSet(80002636, Integer.MAX_VALUE, SecondaryStatFlag.BlackMageCursePmdReduce, 50);
                     }
                  }
               }
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

               if (DBConfig.isGanglim) {
                  int check = c.getOneInfoQuestInteger(QuestExConstants.Zakum.getQuestID(), "eNum");
                  if (check >= 1 + c.getBossTier()) {
                     return c.getId() * -1;
                  }
               } else if (c.getParty() != null && c.getParty().getId() == nCharacter.getParty().getId()) {
                  if (c.getParty().getMembers().size() > 1) {
                     int count = c.getOneInfoQuestInteger(7003, "Multi");
                     if (count > 1) {
                        return c.getId() * -1;
                     }
                  } else {
                     int count = c.getOneInfoQuestInteger(7003, "Single");
                     if (count > 0) {
                        return c.getId() * -1;
                     }
                  }
               }
            }
         }

         if (enterInteger > 6 && nCharacter != null) {
            for (FieldSetInstanceMap fsim : this.instanceMaps) {
               if (fsim.instances.get(0) == enterInteger) {
                  if (DBConfig.isGanglim && nCharacter.getParty() != null) {
                     for (PartyMemberEntry mpc : nCharacter.getParty().getPartyMemberList()) {
                        MapleCharacter p = nCharacter.getMap().getCharacterById(mpc.getId());
                        if (p != null) {
                           p.updateOneInfo(
                              QuestExConstants.Zakum.getQuestID(),
                              "eNum",
                              String.valueOf(p.getOneInfoQuestInteger(QuestExConstants.Zakum.getQuestID(), "eNum") + 1)
                           );
                        }
                     }
                  }

                  if (!DBConfig.isGanglim && nCharacter.getPartyMemberSize() > 1) {
                     for (MapleCharacter partyMember : nCharacter.getPartyMembersSameMap()) {
                        partyMember.setMultiMode(true);
                     }
                  }

                  FieldSetInstance fsi = new ZakumBoss(this, fsim, nCharacter);
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
