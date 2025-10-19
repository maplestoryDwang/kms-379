package objects.fields.fieldset.childs;

import constants.QuestExConstants;
import database.DBConfig;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import network.game.GameServer;
import objects.context.party.PartyMemberEntry;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.fieldset.instance.ChaosZakumBoss;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;

public class ChaosZakumEnter extends FieldSet {
   public ChaosZakumEnter(int channel) {
      super("ChaosZakumEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(280030000))));
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
               if (c.getParty() != null && c.getParty() != null && c.getParty().getId() == nCharacter.getParty().getId()) {
                  if (DBConfig.isGanglim && c.getBossTier() < tier) {
                     return -5;
                  }

                  if (DBConfig.isGanglim) {
                     int check = c.getOneInfoQuestInteger(QuestExConstants.ChaosZakum.getQuestID(), "eNum");
                     if (check >= 1 + c.getBossTier()) {
                        return c.getId() * -1;
                     }
                  } else {
                     boolean single = c.getParty().getMembers().size() == 1;
                     String mobDead = null;
                     if (single) {
                        mobDead = c.getOneInfo(15166, "mobDeadSingle");
                     } else {
                        mobDead = c.getOneInfo(15166, "mobDeadMulti");
                     }

                     if (mobDead != null) {
                        String lasttime = c.getOneInfo(15166, "lasttime");

                        try {
                           SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                           Date lastDate = sdf.parse(lasttime);
                           Date today = new Date();
                           if (mobDead.equals("0")) {
                              lastDate.setMinutes(lastDate.getMinutes() + 30);
                              if (today.before(lastDate)) {
                                 return 7;
                              }
                           } else if (mobDead.equals("1")) {
                              int initDay = 7 - lastDate.getDay() + 4;
                              if (lastDate.getDay() < 4) {
                                 initDay = 4 - lastDate.getDay();
                              }

                              lastDate.setDate(lastDate.getDate() + initDay);
                              lastDate.setHours(0);
                              lastDate.setMinutes(0);
                              lastDate.setSeconds(0);
                              if (today.before(lastDate)) {
                                 return c.getId() * -1;
                              }
                           }
                        } catch (ParseException var17) {
                           return -1;
                        }
                     }
                  }
               }
            }
         }

         if (DBConfig.isGanglim) {
            for (MapleCharacter chr : nCharacter.getMap().getCharacters()) {
               if (chr.getParty() != null && chr.getParty().getId() == nCharacter.getParty().getId()) {
                  int clear = chr.getOneInfoQuestInteger(QuestExConstants.ChaosZakum.getQuestID(), "eNum");
                  int max = 1 + chr.getBossTier();
                  chr.dropMessage(5, String.format("금일 %s 입장횟수 %d / %d", "자쿰", clear, max));
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
                              QuestExConstants.ChaosZakum.getQuestID(),
                              "eNum",
                              String.valueOf(p.getOneInfoQuestInteger(QuestExConstants.ChaosZakum.getQuestID(), "eNum") + 1)
                           );
                        }
                     }
                  }

                  if (!DBConfig.isGanglim && nCharacter.getPartyMemberSize() > 1) {
                     for (MapleCharacter partyMember : nCharacter.getPartyMembersSameMap()) {
                        partyMember.setMultiMode(true);
                     }
                  }

                  FieldSetInstance fsi = new ChaosZakumBoss(this, fsim, nCharacter);
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
      GameServer gs = GameServer.getInstance(this.channel);
      MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);

      for (MapleCharacter c : nCharacter.getMap().getCharacters()) {
         if (c.getParty() != null && c.getParty().getId() == nCharacter.getParty().getId()) {
            String mobDead = c.getOneInfo(15166, "mobDead");
            if (mobDead != null) {
               String lasttime = c.getOneInfo(15166, "lasttime");

               try {
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                  Date lastDate = sdf.parse(lasttime);
                  Date today = new Date();
                  if (mobDead.equals("0")) {
                     lastDate.setMinutes(lastDate.getMinutes() + 30);
                     if (today.before(lastDate)) {
                        return c.getName() + "(" + (lastDate.getMinutes() - today.getMinutes()) + "분)";
                     }
                  }
               } catch (ParseException var11) {
                  return "알 수 없음";
               }
            }
         }
      }

      return "알 수 없음";
   }

   @Override
   public void resetQuestTime() {
   }
}
