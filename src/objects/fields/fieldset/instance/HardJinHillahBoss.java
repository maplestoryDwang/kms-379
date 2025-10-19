package objects.fields.fieldset.instance;

import java.util.ArrayList;
import java.util.Properties;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class HardJinHillahBoss extends FieldSetInstance {
   public boolean isPracticeMode = false;
   boolean genesisQuestMode = false;
   private Properties Var = new Properties();

   public HardJinHillahBoss(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader, boolean isPracticeMode, boolean genesisQuestMode) {
      super(fs, fsim, leader);
      this.isPracticeMode = isPracticeMode;
      this.genesisQuestMode = genesisQuestMode;
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
      this.initDeathCount(5);
      this.timeOut(this.fieldSeteventTime);
   }

   @Override
   public void userEnter(MapleCharacter user) {
      if (this.genesisQuestMode && this.leader.getParty().getPartyMemberList().size() > 1) {
         user.applyBMCurse2(3);
      }

      super.userEnter(user);
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
      if (user.getBossMode() == 1 && to.getId() != 450010400 && to.getId() != 450010500) {
         user.setBossMode(0);
      }
   }

   @Override
   public void userDead(MapleCharacter user) {
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
   }

   public boolean isPracticeMode() {
      return this.isPracticeMode;
   }
}
