package objects.fields.fieldset.instance;

import java.util.ArrayList;
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

public class HellDemianBoss extends FieldSetInstance {
   public HellDemianBoss(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.Var.put("hell", "1");
      this.fieldSeteventTime = 1200000;
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
      if (user.getMap().getId() == 350160100 || user.getMap().getId() == 350160140) {
         user.send(CField.UIPacket.setInGameDirectionMode(0));
         user.send(CField.UIPacket.endInGameDirectionMode(0));
      }

      if (!user.hasBuffBySkillID(1221054)) {
         user.temporaryStatSet(1221054, 5000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      super.userEnter(user);
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
      if (to.getFieldSetInstance() != null && !to.getFieldSetInstance().equals(this) || to.getFieldSetInstance() == null) {
         user.temporaryStatResetBySkillID(80002404);
         user.temporaryStatResetBySkillID(80002636);
      }
   }

   @Override
   public void userDead(MapleCharacter user) {
      boolean fail = false;

      for (MapleCharacter us : user.getMap().getCharacters()) {
         if (user.getParty().getId() == us.getParty().getId()) {
            int deathCount = us.getDeathCount();
            if (deathCount > 0) {
               us.setDeathCount(deathCount - 1);
               us.setDecrementDeathCount(us.getDecrementDeathCount() + 1);
               if (us.getDeathCount() == 0) {
                  fail = true;
                  us.send(CField.showEffect("killing/fail"));
                  us.setClock(30);
                  this.restartTimeOut(30000);
               }
            }
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
