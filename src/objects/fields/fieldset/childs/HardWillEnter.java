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
import objects.fields.fieldset.instance.HardWillBoss;
import objects.users.MapleCharacter;

public class HardWillEnter extends FieldSet {
   public HardWillEnter(int channel) {
      super("HardWillEnter", channel);
      this.instanceMaps
         .add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(450008100, 450008150, 450008200, 450008250, 450008300, 450008350, 450008380))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 235;
      this.maxLv = 1000;
      this.qexKey = 1234569;
      this.keyValue = "will_clear";
      this.canTimeKey = "will_can_time";
      this.bossName = "윌";
      this.difficulty = "하드";
      this.dailyLimit = DBConfig.isGanglim ? 3 : 6;
      this.genesisQuestMemberLimit = 2;
      this.genesisQuestId = 2000024;
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
                  boolean clearCheck;
                  if (DBConfig.isGanglim) {
                     clearCheck = chrx.bClearCheck(this.qexKey, this.keyValue, isPracticeMode);
                  } else {
                     boolean single = nCharacter.getPartyMemberSize() == 1;
                     clearCheck = chrx.bClearCheck(this.qexKey, this.keyValue + (single ? "_single" : "_multi"), isPracticeMode);
                  }

                  if (clearCheck) {
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

         if (DBConfig.isGanglim && !isPracticeMode) {
            for (MapleCharacter chrxx : nCharacter.getMap().getCharacters()) {
               if (chrxx.getParty() != null && chrxx.getParty().getId() == nCharacter.getParty().getId()) {
                  chrxx.clearCount(this.qexKey, this.keyValue, this.bossName);
               }
            }
         }

         if (enterInteger > 6 && nCharacter != null) {
            for (MapleCharacter chrxxx : nCharacter.getMap().getCharacters()) {
               if (chrxxx.getParty() != null && chrxxx.getParty().getId() == nCharacter.getParty().getId()) {
                  String bn2 = bn;
                  if (isPracticeMode) {
                     bn2 = bn + "(연습)";
                  }

                  StringBuilder sb = new StringBuilder("보스 " + bn2 + " 입장");
                  LoggingManager.putLog(new BossLog(chrxxx, BossLogType.EnterLog.getType(), sb));
                  if (DBConfig.isGanglim && !isPracticeMode) {
                     chrxxx.updateOneInfo(this.qexKey, this.keyValue, String.valueOf(chrxxx.getOneInfoQuestInteger(this.qexKey, this.keyValue) + 1));
                  } else {
                     if (!DBConfig.isGanglim && nCharacter.getPartyMemberSize() > 1) {
                        chrxxx.setMultiMode(true);
                        if (!genesisQuest) {
                           chrxxx.applyBMCurseJinMulti();
                        }
                     }

                     this.AC(chrxxx, key, isPracticeMode);
                  }

                  chrxxx.setCurrentBossPhase(1);
                  if (genesisQuest) {
                     chrxxx.setStartBMQuest7(true);
                  }
               }
            }

            for (FieldSetInstanceMap fsim : this.instanceMaps) {
               if (fsim.instances.get(0) == enterInteger) {
                  FieldSetInstance fsi = new HardWillBoss(this, fsim, nCharacter, isPracticeMode, genesisQuest);
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
