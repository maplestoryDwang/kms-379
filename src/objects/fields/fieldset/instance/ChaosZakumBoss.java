package objects.fields.fieldset.instance;

import com.google.common.collect.UnmodifiableIterator;
import constants.QuestExConstants;
import database.DBConfig;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import network.game.GameServer;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class ChaosZakumBoss extends FieldSetInstance {
   private Properties Var = new Properties();

   public ChaosZakumBoss(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.fieldSeteventTime = 1800000;
      this.setFieldSetStartTime(System.currentTimeMillis());
      this.fieldSetEndTime = this.getFieldSetStartTime() + this.fieldSeteventTime;
      this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);

      for (Integer map : this.fsim.instances) {
         this.field(map).resetFully(false);
         this.field(map).setFieldSetInstance(this);
      }

      this.fs.fInstance.putIfAbsent(this, new ArrayList<>());

      for (PartyMemberEntry mpc : this.leader.getParty().getPartyMemberList()) {
         this.fs.fInstance.get(this).add(mpc.getId());
      }

      this.userList = this.fs.fInstance.get(this);
      this.updateBossLimit();
      this.initDeathCount(5);
      this.timeOut(this.fieldSeteventTime);
   }

   @Override
   public void userEnter(MapleCharacter user) {
      super.userEnter(user);
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
   }

   @Override
   public void userDead(MapleCharacter user) {
      super.userDead(user);
   }

   @Override
   public void userLeftParty(MapleCharacter user) {
      super.userLeftParty(user);
   }

   @Override
   public void userDisbandParty() {
      super.userDisbandParty();
   }

   @Override
   public void userDisconnected(MapleCharacter user) {
      super.userDisconnected(user);
   }

   @Override
   public void mobDead(MapleMonster mMob) {
      UnmodifiableIterator var2 = QuestExConstants.bossQuests.keySet().iterator();

      while (var2.hasNext()) {
         Integer mob = (Integer)var2.next();
         if (mob.equals(mMob.getId())) {
            this.restartTimeOut(300000);
            int questId = (Integer)QuestExConstants.bossQuests.get(mob);
            boolean multi = false;
            if (!DBConfig.isGanglim) {
               multi = this.fs.fInstance.get(this).size() > 1;
            }

            for (Integer playerId : this.fs.fInstance.get(this)) {
               boolean find = false;

               for (GameServer gs : GameServer.getAllInstances()) {
                  MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(playerId);
                  if (nCharacter != null) {
                     nCharacter.addGuildContributionByBoss(mMob.getId());
                     find = true;
                     nCharacter.updateOneInfo(questId, "count", String.valueOf(nCharacter.getOneInfoQuestInteger(questId, "count") + 1));
                     nCharacter.updateOneInfo(questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                     if (DBConfig.isGanglim) {
                        nCharacter.updateOneInfo(questId, "mobDead", "1");
                     } else if (multi) {
                        nCharacter.updateOneInfo(questId, "mobDeadMulti", "1");
                     } else {
                        nCharacter.updateOneInfo(questId, "mobDeadSingle", "1");
                     }

                     if (nCharacter.isQuestStarted(501544)) {
                        if (nCharacter.getOneInfoQuestInteger(501544, "value") < 1) {
                           nCharacter.updateOneInfo(501544, "value", "1");
                        }

                        if (nCharacter.getOneInfoQuestInteger(501524, "state") < 2) {
                           nCharacter.updateOneInfo(501524, "state", "2");
                        }
                     }
                  }
               }

               if (!find) {
                  this.updateOfflineBossLimit(playerId, questId, "count", "1");
                  this.updateOfflineBossLimit(playerId, questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                  if (DBConfig.isGanglim) {
                     this.updateOfflineBossLimit(playerId, questId, "mobDead", "1");
                  } else if (multi) {
                     this.updateOfflineBossLimit(playerId, questId, "mobDeadMulti", "1");
                  } else {
                     this.updateOfflineBossLimit(playerId, questId, "mobDeadSingle", "1");
                  }
               }
            }
            break;
         }
      }
   }

   private void updateBossLimit() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      GameServer gs = GameServer.getInstance(this.channel);

      for (Integer cha : this.fs.fInstance.get(this)) {
         MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(cha);
         if (nCharacter != null) {
            nCharacter.updateOneInfo(15166, "lasttime", sdf.format(new Date()));
            nCharacter.updateOneInfo(15166, "mobDead", "0");
         }
      }
   }
}
