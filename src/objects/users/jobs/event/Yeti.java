package objects.users.jobs.event;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.Pirate;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_PointWithDirection;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Rect;

public class Yeti extends Pirate {
   final int YetiPlease00 = 135001000;
   final int YetiPlease01 = 135001001;
   final int YetiPlease02 = 135001002;
   final int YetiAngry = 135001005;
   final int ImadeThisBooster = 135001007;
   final int ImadeThisBoosterRage = 135001010;
   final int YetiSmartReading = 135001008;
   final int YetiSmartReadingRage = 135002008;
   final int MyFriendPepe = 135001015;
   final int MyFriendPepeForceAtom = 135002015;
   final int YetiSmartBombDismantling = 135001011;
   final int YetiSmartBombDismantlingRage = 135002011;
   final int YetiChef = 135001009;
   final int YetiSmartScienceExperiment = 135001016;
   final int YetiSmartScienceExperimentRage = 135002016;
   final int ElNathWarrior = 135001013;
   final int ElNathWarriorRage = 135002013;
   final int ImadeThisSnowMan = 135001012;
   final int YetiSmartViolin = 135001019;
   final int YetiSmartViolinRage = 135002019;
   final int YetiSpicy = 135001017;
   final int RageEnhancementRoarofRage = 135001020;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      super.prepareAttack(attack, effect, opcode);
      switch (attack.skillID) {
         case 135001007:
         case 135001010:
            Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.AutoChargeStack);
            if (value != null) {
               if (value == 2 && attack.skillID == 135001007) {
                  this.getPlayer().setYetiBoosterTill(System.currentTimeMillis());
               } else if (value == 3 && attack.skillID == 135001010) {
                  this.getPlayer().setYetiBoosterTill(System.currentTimeMillis());
               } else if (value == 3 && attack.skillID == 135001007) {
                  value = 2;
                  this.getPlayer().setYetiBoosterTill(System.currentTimeMillis());
               }

               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.AutoChargeStack, attack.skillID, value - 1);
               statManager.temporaryStatSet();
            }
      }
   }

   @Override
   public void onAttack(
      MapleMonster monster,
      boolean boss,
      AttackPair attackPair,
      Skill skill,
      long totalDamage,
      AttackInfo attack,
      SecondaryStatEffect effect,
      RecvPacketOpcode opcode
   ) {
      long totalDmg = 0L;
      int incYetiRage = 0;

      for (Pair<Long, Boolean> dmg : attackPair.attack) {
         totalDmg += dmg.left;
      }

      boolean mobAlive = monster.getHp() - totalDmg > 0L;
      if (boss) {
         incYetiRage += 3;
      } else if (!mobAlive) {
         incYetiRage++;
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Yeti_Rage) == null) {
         this.getPlayer().temporaryStatSet(13500, 210000000, SecondaryStatFlag.Yeti_Rage, 0);
      }

      int value = this.getPlayer().getBuffedValue(SecondaryStatFlag.Yeti_Rage);
      SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
      if (monster.getBuff(MobTemporaryStatFlag.SPEED) != null
         && skill.getId() != 135001012
         && monster.getBuff(MobTemporaryStatFlag.SPEED).getSkillID() == 135001012
         && !mobAlive) {
         value += 2;
      }

      switch (skill.getId()) {
         case 135001000:
         case 135001001:
         case 135001002:
            if (incYetiRage > 0) {
               statManager.changeStatValue(SecondaryStatFlag.Yeti_Rage, 13500, Math.min(300, value + incYetiRage));
               statManager.temporaryStatSet();
            }
            break;
         case 135001008:
         case 135001011:
         case 135001016:
         case 135001019:
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Yeti_Spicy) != null) {
               value += 3;
            }

            statManager.changeStatValue(SecondaryStatFlag.Yeti_Rage, 13500, Math.min(300, value + 3));
            statManager.temporaryStatSet();
            break;
         case 135001012:
            Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
            mse.put(
               MobTemporaryStatFlag.Indie_Additional_DamageR,
               new MobTemporaryStatEffect(MobTemporaryStatFlag.Indie_Additional_DamageR, effect.getY(), 135001012, null, false)
            );
            mse.put(MobTemporaryStatFlag.SPEED, new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getZ(), 135001012, null, false));
            monster.applyMonsterBuff(mse, 135001012, 2000L, null, Collections.EMPTY_LIST);
            if (!mobAlive) {
               statManager.changeStatValue(SecondaryStatFlag.Yeti_Rage, 13500, Math.min(300, value + 2));
               statManager.temporaryStatSet();
            }
            break;
         case 135002015:
            statManager.changeStatValue(SecondaryStatFlag.Yeti_Rage, 13500, Math.min(300, value + 1));
            statManager.temporaryStatSet();
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      this.attackMyFriendPepe(skill.getId());
      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      super.activeSkillPrepare(packet);
      switch (this.getActiveSkillPrepareID()) {
         case 135001020:
            this.getPlayer().temporaryStatSet(135001020, 3160, SecondaryStatFlag.indiePartialNotDamaged, 1);
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.Yeti_RageOn, 135001020, 0);
            statManager.temporaryStatSet();
      }
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(1);
      switch (this.getActiveSkillID()) {
         case 135001005:
            Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.Yeti_Rage);
            if (value != null && value == 300) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.Yeti_Spicy);
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.Yeti_Rage, 13500, 0);
               statManager.changeStatValue(SecondaryStatFlag.AutoChargeStack, 135001010, 3);
               statManager.temporaryStatSet();
            }
            break;
         case 135001012:
            HashMap<SecondaryStatFlag, Integer> flags = new HashMap<>();
            flags.put(SecondaryStatFlag.indieSummon, 1);
            flags.put(SecondaryStatFlag.indieFlyAcc, 1);
            this.getPlayer().temporaryStatSet(135001012, 1, effect.getDuration(), flags);
            List<TeleportAttackElement> elements = this.teleportAttackAction.actions;
            Point spawnPoint = null;
            boolean isLeft = false;

            for (TeleportAttackElement element : elements) {
               if (element.type == 7) {
                  TeleportAttackData_PointWithDirection pos = (TeleportAttackData_PointWithDirection)element.data;
                  spawnPoint = new Point(pos.x, pos.y);
                  isLeft = pos.direction > 0;
                  break;
               }
            }

            Rect rect = new Rect(spawnPoint, effect.getLt(), effect.getRb(), isLeft);
            AffectedArea area = new AffectedArea(rect, this.getPlayer(), effect, spawnPoint, System.currentTimeMillis() + effect.getDuration());
            area.setRlType(isLeft ? 1 : 0);
            this.getPlayer().getMap().spawnMist(area);
            break;
         case 135001013:
            this.getPlayer().temporaryStatSet(this.getActiveSkillID(), effect.getDuration(), SecondaryStatFlag.indieStatR, this.getPlayer().getLevel() / 5);
         case 135001015:
      }

      if (effect.getCooldown(this.getPlayer()) > 0) {
         int skillid = this.getActiveSkillID();
         this.getPlayer().send(CField.skillCooldown(skillid, effect.getCooldown(this.getPlayer())));
         this.getPlayer().addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown(this.getPlayer()));
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      Integer chargeStack = this.getPlayer().getBuffedValue(SecondaryStatFlag.AutoChargeStack);
      if (chargeStack != null) {
         long till = this.getPlayer().getYetiBoosterTill();
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Yeti_RageOn) == null) {
            if (chargeStack < 2 && till + 10000L <= System.currentTimeMillis()) {
               this.getPlayer().setYetiBoosterTill(System.currentTimeMillis());
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.AutoChargeStack, 135001007, chargeStack + 1);
               statManager.temporaryStatSet();
            }
         } else if (chargeStack < 3 && till + 6000L <= System.currentTimeMillis()) {
            this.getPlayer().setYetiBoosterTill(System.currentTimeMillis());
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.AutoChargeStack, 135001010, chargeStack + 1);
            statManager.temporaryStatSet();
         }
      }
   }

   private void attackMyFriendPepe(int skillId) {
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Yeti_MyFriendPepe) != null) {
         Skill myp = SkillFactory.getSkill(135001015);
         if (myp.getSkillList().contains(skillId) && !this.getPlayer().skillisCooling(135002015)) {
            this.getPlayer().giveCoolDowns(135002015, System.currentTimeMillis(), 3000L);
            SecondaryStatEffect pepeAtom = SkillFactory.getSkill(135002015).getEffect(1);
            List<Integer> mobList = new ArrayList<>();

            for (MapleMonster mob : this.getPlayer()
               .getMap()
               .getMobsInRect(this.getPlayer().getTruePosition(), pepeAtom.getLt().x, pepeAtom.getLt().y, pepeAtom.getRb().x, pepeAtom.getRb().y)) {
               if (mob.isAlive() && !mob.getStats().isFriendly()) {
                  mobList.add(mob.getObjectId());
               }

               if (mobList.size() >= pepeAtom.getY()) {
                  break;
               }
            }

            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            ForceAtom forceAtom = new ForceAtom(
               info, 135002015, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.CLONE_ATTACK, mobList, mobList.size()
            );
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
         }
      }
   }
}
