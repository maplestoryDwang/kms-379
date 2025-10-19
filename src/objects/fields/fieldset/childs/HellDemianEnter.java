package objects.fields.fieldset.childs;

import database.DBConfig;
import java.util.ArrayList;
import java.util.Arrays;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.game.GameServer;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.fieldset.instance.HellDemianBoss;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;

public class HellDemianEnter extends FieldSet {
   public HellDemianEnter(int channel) {
      super("HellDemianEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(350160160, 350160100, 350160120, 350160140, 350160180))));
      this.minMember = 1;
      this.maxMember = 3;
      this.minLv = 270;
      this.maxLv = 1000;
      this.qexKey = 1234569;
      this.keyValue = "demian_clear";
      this.canTimeKey = "demian_can_time";
      this.bossName = "데미안";
      this.difficulty = "헬";
      this.dailyLimit = 3;
   }

   @Override
   public void updateFieldSetPS() {
      synchronized (this.fInstance) {
         for (FieldSetInstance fis : this.fInstance.keySet()) {
            int userCount = 0;

            for (Integer map : fis.fsim.instances) {
               if (DBConfig.isGanglim) {
                  for (MapleCharacter chr : this.field(map).getCharacters()) {
                     if (chr.getBuffedValue(SecondaryStatFlag.BlackMageCursePmdReduce) == null) {
                        chr.temporaryStatSet(80002636, Integer.MAX_VALUE, SecondaryStatFlag.BlackMageCursePmdReduce, 99);
                     }
                  }
               }

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
         String bn = this.difficulty + " " + this.bossName;
         String key = bn + "c";
         if (enterInteger > 6 && nCharacter != null) {
            if (nCharacter.getParty().getPartyMemberList().size() > 3) {
               return -10;
            }

            int dailyLimit = 1;

            for (MapleCharacter chr : nCharacter.getMap().getCharacters()) {
               if (chr.getParty() != null && chr.getParty().getId() == nCharacter.getParty().getId()) {
                  String clearCheck = chr.getOneInfoQuest(this.qexKey, this.keyValue);
                  String clearCheck2 = chr.getOneInfoQuest(this.qexKey, "hell_" + this.keyValue);
                  if (DBConfig.isGanglim && chr.getBossTier() < tier) {
                     return -5;
                  }

                  if (clearCheck2 != null && !clearCheck2.isEmpty() && clearCheck2.equals("1")) {
                     return -3;
                  }

                  boolean countCheck = chr.CountCheck(key, dailyLimit, true);
                  boolean canEnterBoss = chr.canEnterBoss(this.canTimeKey);
                  if (!countCheck) {
                     return -1;
                  }

                  if (!canEnterBoss) {
                     return -2;
                  }
               }
            }
         }

         if (enterInteger > 6 && nCharacter != null) {
            for (MapleCharacter chrx : nCharacter.getMap().getCharacters()) {
               if (chrx.getParty() != null && chrx.getParty().getId() == nCharacter.getParty().getId()) {
                  StringBuilder sb = new StringBuilder("보스 " + bn + " 입장");
                  LoggingManager.putLog(new BossLog(chrx, BossLogType.EnterLog.getType(), sb));
                  this.AC(chrx, key, false);
                  chrx.setCurrentBossPhase(1);
               }
            }

            for (FieldSetInstanceMap fsim : this.instanceMaps) {
               if (fsim.instances.get(0) == enterInteger) {
                  FieldSetInstance fsi = new HellDemianBoss(this, fsim, nCharacter);
                  this.transferFieldAll(enterInteger, this.fInstance.get(fsi));
                  break;
               }
            }
         }

         return enterInteger;
      }
   }

   private void AC(MapleCharacter chr, String boss, boolean practice) {
      chr.CountAdd(boss);
      if (!practice) {
         chr.updateCanTime(this.canTimeKey, 0);
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
