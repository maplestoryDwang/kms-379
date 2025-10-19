package objects.users.jobs.adventure.pirate;

import java.awt.Point;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;

public class Captain extends DefaultPirate {
   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 400051021) {
         this.getPlayer().temporaryStatSet(400051021, 1320, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      super.prepareAttack(attack, effect, opcode);
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
      if (attack.skillID == 5221015) {
         this.getPlayer().setGuidedBulletUser(this.getPlayer().getId());
         this.getPlayer().setGuidedBulletTarget(monster.getObjectId());
         this.getPlayer().temporaryStatSet(attack.skillID, Integer.MAX_VALUE, SecondaryStatFlag.GuidedBullet, effect.getX());
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.targets > 0 && attack.skillID == 5221013 && this.getPlayer().getTotalSkillLevel(400051040) > 0) {
         long cool = this.getPlayer().getRemainCooltime(400051040);
         SecondaryStatEffect eff = SkillFactory.getSkill(400051040).getEffect(this.getPlayer().getTotalSkillLevel(400051040));
         if (eff != null && cool < eff.getW() * 1000) {
            this.getPlayer().giveCoolDowns(400051040, System.currentTimeMillis(), eff.getW() * 1000);
            this.getPlayer().send(CField.skillCooldown(400051040, eff.getW() * 1000));
         }
      }

      if (attack.targets > 0 && this.getPlayer().getTotalSkillLevel(5220055) > 0) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.QuickDraw);
         Skill sk = SkillFactory.getSkill(5220055);
         if (value != null && value != 0) {
            if (value >= 1 && sk.getSkillList().contains(attack.skillID)) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.QuickDraw);
            }
         } else {
            SecondaryStatEffect effect_ = sk.getEffect(this.getPlayer().getTotalSkillLevel(5220055));
            if (effect_ != null && effect_.makeChanceResult()) {
               this.getPlayer().temporaryStatSet(5220055, Integer.MAX_VALUE, SecondaryStatFlag.QuickDraw, 1);
            }
         }
      }

      if (attack.targets > 0 && attack.skillID == 5221015 && this.getPlayer().getOneInfoQuest(1544, "5221029").equals("1")) {
         SkillFactory.getSkill(5221029).getEffect(this.getPlayer().getSkillLevel(5221029)).applyTo(this.getPlayer(), attack.affectedSpawnPos.get(0));
      }

      if (totalDamage > 0L) {
         if (this.getPlayer().getTotalSkillLevel(5220012) > 0) {
            SecondaryStatEffect offenseForm = this.getPlayer().getSkillLevelData(5220012);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieDamR, 5220012, offenseForm.getDuration(), offenseForm.getIndieDamR());
         }

         if (attack.skillID == 5241000) {
            Integer value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.RapidFire, 0);
            if (value > 49) {
               value = 49;
            }

            this.getPlayer().temporaryStatSet(SecondaryStatFlag.RapidFire, 5241000, Integer.MAX_VALUE, value + 1);
         }

         if (attack.skillID == 5241001) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.RapidFire, 5241000, Integer.MAX_VALUE, 0);
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 5201005:
            this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
            break;
         case 5201012:
         case 5210015:
            Point chrPos = packet.readPos();
            byte rlType = packet.readByte();
            packet.skip(2);
            Summoned toSummon = new Summoned(
               this.getPlayer(),
               this.getActiveSkillID(),
               this.getActiveSkillLevel(),
               new Point(packet.readInt(), packet.readInt()),
               SummonMoveAbility.FLAME_SUMMON,
               rlType,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(toSummon, effect.getDuration());
            this.getPlayer().addSummon(toSummon);
            if (this.getPlayer().getTotalSkillLevel(5220019) > 0) {
               SecondaryStatEffect crewEffect = this.getPlayer().getSkillLevelData(5220019);
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.CrewCommandership, 5220019, effect.getDuration(), toSummon.getSkill());
            }

            if (this.getActiveSkillID() == 5210015) {
               toSummon = new Summoned(
                  this.getPlayer(),
                  5211019,
                  this.getActiveSkillLevel(),
                  new Point(packet.readInt(), packet.readInt()),
                  SummonMoveAbility.FLAME_SUMMON,
                  rlType,
                  System.currentTimeMillis() + effect.getDuration()
               );
               this.getPlayer().getMap().spawnSummon(toSummon, effect.getDuration());
               this.getPlayer().addSummon(toSummon);
               if (this.getPlayer().getTotalSkillLevel(5220019) > 0) {
                  SecondaryStatEffect crewEffect = this.getPlayer().getSkillLevelData(5220019);
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.CrewCommandership, 5220019, effect.getDuration(), toSummon.getSkill());
               }
            }
            break;
         case 5221022:
            Point pos = packet.readPos();
            rlType = packet.readByte();
            Summoned summon1;
            if ((summon1 = this.getPlayer().getSummonBySkillID(5221022)) != null) {
               Summoned summon2;
               if ((summon2 = this.getPlayer().getSummonBySkillID(5221027)) != null) {
                  Summoned removeTarget;
                  if (summon1.getSummonRemoveTime() < summon2.getSummonRemoveTime()) {
                     removeTarget = summon1;
                  } else {
                     removeTarget = summon2;
                  }

                  this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(removeTarget, true));
                  this.getPlayer().getMap().removeMapObject(removeTarget);
                  this.getPlayer().removeVisibleMapObject(removeTarget);
                  this.getPlayer().removeSummon(removeTarget);
                  this.getPlayer().temporaryStatResetBySkillID(removeTarget.getSkill());
                  toSummon = new Summoned(
                     this.getPlayer(),
                     removeTarget.getSkill(),
                     this.getActiveSkillLevel(),
                     pos,
                     SummonMoveAbility.STATIONARY,
                     rlType,
                     System.currentTimeMillis() + effect.getDuration()
                  );
               } else {
                  toSummon = new Summoned(
                     this.getPlayer(),
                     5221027,
                     this.getActiveSkillLevel(),
                     pos,
                     SummonMoveAbility.STATIONARY,
                     rlType,
                     System.currentTimeMillis() + effect.getDuration()
                  );
               }
            } else {
               toSummon = new Summoned(
                  this.getPlayer(),
                  this.getActiveSkillID(),
                  this.getActiveSkillLevel(),
                  pos,
                  SummonMoveAbility.STATIONARY,
                  rlType,
                  System.currentTimeMillis() + effect.getDuration()
               );
            }

            this.getPlayer().getMap().spawnSummon(toSummon, effect.getDuration());
            this.getPlayer().addSummon(toSummon);
            break;
         case 5241502:
            Summoned s = new Summoned(
               this.getPlayer(),
               5241502,
               this.getActiveSkillLevel(),
               this.getPlayer().getPosition(),
               SummonMoveAbility.STATIONARY,
               (byte)(this.getPlayer().isFacingLeft() ? 1 : 0),
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(s, effect.getDuration());
            this.getPlayer().addSummon(s);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400051006) {
         this.getPlayer().temporaryStatResetBySkillID(400051006);
      }

      if (this.getActiveSkillID() == 5210015) {
         this.getPlayer().temporaryStatResetBySkillID(5220019);
         if (this.getPlayer().getSummonBySkillID(5211019) != null) {
            this.getPlayer().removeSummonsByReset(5211019);
         }
      }

      super.activeSkillCancel();
   }
}
