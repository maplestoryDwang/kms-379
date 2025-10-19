package objects.fields.fieldset.instance;

import com.google.common.collect.UnmodifiableIterator;
import constants.QuestExConstants;
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

public class EasyMagnusBoss extends FieldSetInstance {
   private Properties Var = new Properties();

   public EasyMagnusBoss(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
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
      this.initDeathCount(15);
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

            for (Integer playerId : this.fs.fInstance.get(this)) {
               boolean find = false;

               for (GameServer gs : GameServer.getAllInstances()) {
                  MapleCharacter nCharacter = gs.getPlayerStorage().getCharacterById(playerId);
                  if (nCharacter != null) {
                     find = true;
                     nCharacter.updateOneInfo(questId, "count", String.valueOf(nCharacter.getOneInfoQuestInteger(questId, "count") + 1));
                     nCharacter.updateOneInfo(questId, "mobid", String.valueOf(mMob.getId()));
                     nCharacter.updateOneInfo(questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                     nCharacter.updateOneInfo(questId, "mobDead", "1");
                  }
               }

               if (!find) {
                  this.updateOfflineBossLimit(playerId, questId, "count", "1");
                  this.updateOfflineBossLimit(playerId, questId, "mobid", String.valueOf(mMob.getId()));
                  this.updateOfflineBossLimit(playerId, questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                  this.updateOfflineBossLimit(playerId, questId, "mobDead", "1");
               }
            }
            break;
         }
      }
   }
}
