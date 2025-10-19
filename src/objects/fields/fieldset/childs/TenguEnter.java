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
import objects.fields.fieldset.instance.TenguBoss;
import objects.users.MapleCharacter;

public class TenguEnter extends FieldSet {
   public TenguEnter(int channel) {
      super("TenguEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(813000000))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 250;
      this.maxLv = 1000;
      this.qexKey = 1234569;
      this.keyValue = "tengu_clear";
      this.canTimeKey = "tengu_can_time";
      this.bossName = "텐구";
      this.difficulty = "하드";
      this.dailyLimit = 1;
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
   public int enter(int CharacterID, int tier) {
      return this.enter(CharacterID, false, false, tier);
   }

   public synchronized int enter(int CharacterID, boolean isPracticeMode, boolean genesisQuest, int tier) {
      synchronized (this.fInstance) {
         int enterInteger = this.tryEnterPartyQuest(CharacterID, isPracticeMode, true);
         GameServer gs = GameServer.getInstance(this.channel);
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
         String bn = this.difficulty + " " + this.bossName;
         String key = bn + "c";
         if (enterInteger > 6 && nCharacter != null) {
            if (genesisQuest && nCharacter.getParty().getPartyMemberList().size() > this.genesisQuestMemberLimit) {
               return -10;
            }

            if (genesisQuest) {
               for (MapleCharacter chr : nCharacter.getMap().getCharacters()) {
                  if (chr.getParty() != null && chr.getParty().getId() == nCharacter.getParty().getId() && chr.getQuestStatus(this.genesisQuestId) != 1) {
                     return -20;
                  }
               }
            }

            int dailyLimit = 3;
            if (isPracticeMode) {
               key = "boss_practice";
               dailyLimit = 20;
            }

            for (MapleCharacter chrx : nCharacter.getMap().getCharacters()) {
               if (chrx.getParty() != null && chrx.getParty().getId() == nCharacter.getParty().getId()) {
                  String clearCheck = chrx.getOneInfoQuest(this.qexKey, this.keyValue);
                  if (chrx.bClearCheck(this.qexKey, this.keyValue, isPracticeMode)) {
                     return -3;
                  }

                  if (DBConfig.isGanglim && chrx.getBossTier() < tier) {
                     return -5;
                  }

                  if (!DBConfig.isGanglim) {
                     boolean countCheck = chrx.CountCheck(key, dailyLimit);
                     if (!countCheck) {
                        return -1;
                     }

                     if (!isPracticeMode) {
                        boolean canEnterBoss = chrx.canEnterBoss(this.canTimeKey);
                        if (!canEnterBoss) {
                           return -2;
                        }
                     }
                  }
               }
            }
         }

         if (enterInteger > 6 && nCharacter != null) {
            for (MapleCharacter chrxx : nCharacter.getMap().getCharacters()) {
               if (chrxx.getParty() != null && chrxx.getParty().getId() == nCharacter.getParty().getId()) {
                  String bn2 = bn;
                  if (isPracticeMode) {
                     bn2 = bn + "(연습)";
                  }

                  StringBuilder sb = new StringBuilder("보스 " + bn2 + " 입장");
                  LoggingManager.putLog(new BossLog(chrxx, BossLogType.EnterLog.getType(), sb));
                  if (DBConfig.isGanglim && !isPracticeMode) {
                     chrxx.updateOneInfo(this.qexKey, this.keyValue, String.valueOf(chrxx.getOneInfoQuestInteger(this.qexKey, this.keyValue) + 1));
                  } else {
                     this.AC(chrxx, key, isPracticeMode);
                  }

                  chrxx.setCurrentBossPhase(1);
               }
            }

            for (FieldSetInstanceMap fsim : this.instanceMaps) {
               if (fsim.instances.get(0) == enterInteger) {
                  FieldSetInstance fsi = new TenguBoss(this, fsim, nCharacter, isPracticeMode);
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
