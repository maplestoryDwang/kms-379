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
import objects.fields.fieldset.instance.KalosBoss;
import objects.users.MapleCharacter;

public class NormalKalosEnter extends FieldSet {
   public NormalKalosEnter(int channel) {
      super("NormalKalosEnter", channel);
      this.instanceMaps.add(new FieldSetInstanceMap(new ArrayList<>(Arrays.asList(410006000, 410006020, 410006040, 410006060, 410006080))));
      this.minMember = 1;
      this.maxMember = 6;
      this.minLv = 265;
      this.maxLv = 1000;
      this.qexKey = 1234569;
      this.keyValue = "kalos_clear";
      this.bossName = "칼로스";
      this.difficulty = "노말";
      this.dailyLimit = 3;
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
                  this.field(map).resetFully(true);
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
         int enterInteger = this.tryEnterBoss(CharacterID, isPracticeMode, false);
         GameServer gs = GameServer.getInstance(this.channel);
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(CharacterID);
         String bn = this.difficulty + " " + this.bossName;
         String key = bn + "c";
         if (enterInteger > 6 && nCharacter != null) {
            for (MapleCharacter chr : nCharacter.getMap().getCharacters()) {
               if (chr.getParty() != null && chr.getParty().getId() == nCharacter.getParty().getId()) {
                  boolean clearCheck = chr.bClearCheck(this.qexKey, this.keyValue, isPracticeMode);
                  if (clearCheck) {
                     return -3;
                  }

                  if (DBConfig.isGanglim && chr.getBossTier() < tier) {
                     return -5;
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
                  chrx.setCurrentBossPhase(1);
               }
            }

            for (FieldSetInstanceMap fsim : this.instanceMaps) {
               if (fsim.instances.get(0) == enterInteger) {
                  KalosBoss fsi = new KalosBoss(this, fsim, nCharacter, isPracticeMode, "normal");
                  if (nCharacter.getIsSkipIntro()) {
                     enterInteger += 20;
                  }

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
