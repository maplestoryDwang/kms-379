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
import objects.fields.fieldset.instance.HellJinHillahBoss;
import objects.users.MapleCharacter;

public class HellJinHillahEnter extends FieldSet {
   public HellJinHillahEnter(int channel) {
      super("HellJinHillahEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(450010400, 450010500, 450010550))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 250;
      this.maxLv = 1000;
      this.qexKey = 1234569;
      this.keyValue = "jinhillah_clear";
      this.canTimeKey = "jinhillah_can_time";
      this.bossName = "진힐라";
      this.difficulty = "헬";
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
   public synchronized int enter(int CharacterID, int tier) {
      synchronized (this.fInstance) {
         int enterInteger = this.tryEnterPartyQuest(CharacterID, false, true);
         GameServer gs = GameServer.getInstance(this.channel);
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
         String bn = this.difficulty + " " + this.bossName;
         String key = bn + "c";
         if (enterInteger > 6 && nCharacter != null) {
            int dailyLimit = this.dailyLimit;

            for (MapleCharacter chr : nCharacter.getMap().getCharacters()) {
               if (chr.getParty() != null && chr.getParty().getId() == nCharacter.getParty().getId()) {
                  String clearCheck = chr.getOneInfoQuest(this.qexKey, this.keyValue);
                  if (clearCheck != null && !clearCheck.isEmpty() && clearCheck.equals("1")) {
                     return -3;
                  }

                  if (DBConfig.isGanglim && chr.getBossTier() < tier) {
                     return -5;
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
                  FieldSetInstance fsi = new HellJinHillahBoss(this, fsim, nCharacter);
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
