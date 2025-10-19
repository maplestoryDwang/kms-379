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
import objects.fields.fieldset.instance.HellBlackHeavenBoss;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;

public class HellBlackHeavenBossEnter extends FieldSet {
   public HellBlackHeavenBossEnter(int channel) {
      super("HellBlackHeavenBossEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(350060400, 350060500, 350060600, 350060650))));
      this.minMember = 1;
      this.maxMember = 3;
      this.minLv = 270;
      this.maxLv = 1000;
      this.qexKey = 1234569;
      this.keyValue = "hell_swoo_clear";
      this.canTimeKey = "swoo_can_time";
      this.bossName = "스우";
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
            if (isPracticeMode) {
               key = "boss_practice";
               this.dailyLimit = 20;
            }

            for (MapleCharacter chr : nCharacter.getMap().getCharacters()) {
               if (chr.getParty() != null && chr.getParty().getId() == nCharacter.getParty().getId()) {
                  String notHellSwoo = this.keyValue.substring(5);
                  String clearCheck2 = chr.getOneInfoQuest(this.qexKey, this.keyValue);
                  if (DBConfig.isGanglim && chr.getBossTier() < tier) {
                     return -5;
                  }

                  if (clearCheck2 != null && !clearCheck2.isEmpty() && !isPracticeMode) {
                     try {
                        int clearcount = Integer.parseInt(clearCheck2);
                        if (clearcount >= 1) {
                           return -3;
                        }
                     } catch (Exception var17) {
                     }
                  }

                  boolean countCheck = chr.CountCheck(key, this.dailyLimit, true);
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

         if (enterInteger > 6 && nCharacter != null) {
            for (MapleCharacter chrx : nCharacter.getMap().getCharacters()) {
               if (chrx.getParty() != null && chrx.getParty().getId() == nCharacter.getParty().getId()) {
                  String bn2 = bn;
                  if (isPracticeMode) {
                     bn2 = bn + "(연습)";
                  }

                  StringBuilder sb = new StringBuilder("보스 " + bn2 + " 입장");
                  LoggingManager.putLog(new BossLog(chrx, BossLogType.EnterLog.getType(), sb));
                  this.AC(chrx, key, isPracticeMode);
                  chrx.setCurrentBossPhase(1);
               }
            }

            for (FieldSetInstanceMap fsim : this.instanceMaps) {
               if (fsim.instances.get(0) == enterInteger) {
                  FieldSetInstance fsi = new HellBlackHeavenBoss(this, fsim, nCharacter, isPracticeMode);
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
