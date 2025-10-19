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
import objects.fields.fieldset.instance.HardBlackMageBoss;
import objects.users.MapleCharacter;

public class HardBlackMageEnter extends FieldSet {
   public HardBlackMageEnter(int channel) {
      super("HardBlackMageEnter", channel);
      this.instanceMaps
         .add(
            new FieldSetInstanceMap(
               new ArrayList<>(Arrays.asList(450013000, 450013100, 450013200, 450013300, 450013400, 450013500, 450013600, 450013700, 450013750))
            )
         );
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 255;
      this.maxLv = 1000;
      this.qexKey = 1234570;
      this.keyValue = "blackmage_clear";
      this.canTimeKey = "bm_can_time";
      this.bossName = "검은마법사";
      this.difficulty = "하드";
      this.dailyLimit = DBConfig.isGanglim ? 3 : 6;
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
      return this.enter(CharacterID, false, tier);
   }

   public synchronized int enter(int CharacterID, boolean isPracticeMode, int tier) {
      synchronized (this.fInstance) {
         int enterInteger = this.tryEnterPartyQuest(CharacterID, isPracticeMode, true);
         GameServer gs = GameServer.getInstance(this.channel);
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
         String bn = this.difficulty + " " + this.bossName;
         String key = bn + "c";
         if (enterInteger > 6 && nCharacter != null) {
            int dailyLimit = 3;
            if (isPracticeMode) {
               key = "boss_practice";
               dailyLimit = 20;
            }

            for (MapleCharacter chr : nCharacter.getMap().getCharacters()) {
               if (chr.getParty() != null && chr.getParty().getId() == nCharacter.getParty().getId()) {
                  if (!chr.isGM() && chr.bClearCheck(this.qexKey, this.keyValue, isPracticeMode)) {
                     return -3;
                  }

                  if (!chr.haveItem(4001895, 1) && !isPracticeMode && !DBConfig.isGanglim) {
                     return -4;
                  }

                  if (DBConfig.isGanglim && chr.getBossTier() < tier) {
                     return -5;
                  }

                  if (!DBConfig.isGanglim) {
                     boolean countCheck = chr.CountCheck(key, dailyLimit);
                     if (!countCheck) {
                        return -1;
                     }

                     if (!isPracticeMode) {
                        boolean canEnterBoss = chr.canEnterBoss(this.canTimeKey);
                        if (!canEnterBoss) {
                           return -2;
                        }
                     }
                  }
               }
            }
         }

         if (DBConfig.isGanglim && !isPracticeMode) {
            for (MapleCharacter chrx : nCharacter.getMap().getCharacters()) {
               if (chrx.getParty() != null && chrx.getParty().getId() == nCharacter.getParty().getId()) {
                  chrx.clearCount(this.qexKey, this.keyValue, this.bossName);
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
                  FieldSetInstance fsi = new HardBlackMageBoss(this, fsim, nCharacter, isPracticeMode);
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
         chr.updateCanTime(this.canTimeKey, 2);
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
