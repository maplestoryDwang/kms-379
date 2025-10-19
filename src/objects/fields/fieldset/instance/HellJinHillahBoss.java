package objects.fields.fieldset.instance;

import java.util.ArrayList;
import java.util.Properties;
import network.models.CField;
import network.models.MobPacket;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;

public class HellJinHillahBoss extends FieldSetInstance {
   private Properties Var = new Properties();

   public HellJinHillahBoss(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.Var.put("hell", "1");
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
      super.userEnter(user);
      user.temporaryStatSet(80002636, Integer.MAX_VALUE, SecondaryStatFlag.BlackMageCursePmdReduce, 95);
      if (!user.hasBuffBySkillID(1221054)) {
         user.temporaryStatSet(1221054, 5000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
      if (user.getBossMode() == 1 && to.getId() != 450010400 && to.getId() != 450010500) {
         user.setBossMode(0);
      }

      if (!this.fs.instanceMaps.contains(to.getId())) {
         user.temporaryStatResetBySkillID(80002636);
      }
   }

   @Override
   public void userDead(MapleCharacter user) {
      boolean fail = false;

      for (MapleCharacter us : user.getMap().getCharacters()) {
         if (user.getParty().getId() == us.getParty().getId() && user.isJinHillahDeathCountOut()) {
            fail = true;
            us.send(CField.showEffect("killing/fail"));
            us.setClock(30);
            this.restartTimeOut(30000);
         }
      }

      if (fail) {
         for (MapleMonster mob : user.getMap().getAllMonster()) {
            if (mob.getController() != null) {
               mob.getController().send(MobPacket.stopControllingMonster(mob.getObjectId()));
            }

            mob.getMap().broadcastMessage(MobPacket.killMonster(mob.getObjectId(), -1));
         }
      }
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
}
