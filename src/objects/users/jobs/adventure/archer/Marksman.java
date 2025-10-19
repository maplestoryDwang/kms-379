package objects.users.jobs.adventure.archer;

import java.util.HashMap;
import java.util.Map;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackAction;
import objects.users.skills.TeleportAttackData_Unk;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;

public class Marksman extends DefaultArcher {
   public int Piercing = 3201011;
   public int EnhanceArrow = 3210016;
   public int EnhancePiercing = 3211017;
   public int AdvEnhanceArrow = 3220021;
   public int Sniping = 3221007;
   public int SnipingVI = 3241000;
   public int EnhanceSnipingVI = 3241001;
   public int EnhanceSnipingVI_ = 3241002;
   public int UltimateSnipingVI = 3241003;
   public int UltimateSnipingVI_ = 3241004;
   public int PiercingFragment = 3221019;
   public int FocusOn = 3221022;
   public int AdvEnhancePiercing = 3221023;
   public int AdvEnhancePiercingFragment = 3221024;
   public int EnhanceSniping = 3221025;
   public int EnhanceSnipingBonusAttack = 3221026;
   public int AdvPiercing = 3221027;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 400031006 || attack.skillID == 400031010) {
         int x = Math.max(0, this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.TrueSniping, 0) - 1);
         if (x == 0) {
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.TrueSniping);
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            this.getPlayer().temporaryStatSet(400031006, this.getPlayer().getTotalSkillLevel(400031006), 2000, statups);
         } else {
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.TrueSniping, 400031006, x);
            statManager.temporaryStatSet();
         }
      }

      if (attack.skillID == 400031056) {
         TeleportAttackAction action = attack.teleportAttackAction;
         if (action != null) {
            for (TeleportAttackElement ele : action.actions) {
               if (ele.data != null && ele.data instanceof TeleportAttackData_Unk) {
                  TeleportAttackData_Unk info = (TeleportAttackData_Unk)ele.data;
                  if (info.arrowCount == 1) {
                     Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.RepeatingCrossbowCartridge);
                     if (value != null) {
                        SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.RepeatingCrossbowCartridge);
                        if (e != null) {
                           if (value <= 1) {
                              this.getPlayer().temporaryStatReset(SecondaryStatFlag.RepeatingCrossbowCartridge);
                           } else {
                              value = value - 1;
                              SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                              statManager.changeStatValue(SecondaryStatFlag.RepeatingCrossbowCartridge, 400031055, value);
                              statManager.temporaryStatSet();
                           }
                        }
                     }
                     break;
                  }
               }
            }
         }
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
      if (monster.isAdventurerMarkSet() && skill.getId() != 400031001) {
         this.setEnhanceSniping(monster, false);
      } else {
         if (attack.skillID != this.EnhanceSniping) {
         }

         this.setEnhanceSniping(monster, true);
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().getJob() >= 321 && this.getPlayer().getJob() <= 322) {
         this.giveEnhanceArrowStack(attack.targets, attack.skillID);
         if (attack.targets > 0) {
            this.giveAggressiveResistanceBuff(totalDamage);
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   public void giveAggressiveResistanceBuff(long totalDamage) {
      int skillID = 3210013;
      int skillLevel = this.getPlayer().getTotalSkillLevel(skillID);
      if (skillLevel != 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillLevel);
         if (effect != null) {
            int nOption = 0;
            nOption = (int)Math.min(
               (int)totalDamage * (effect.getY() * 0.01) + nOption, this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) / (effect.getZ() * 0.01)
            );
            int skillID2 = 3210013;
            int skillLevel2 = this.getPlayer().getTotalSkillLevel(skillID2);
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            if (skillLevel2 > 0) {
               statups.put(SecondaryStatFlag.PowerTransferGauge, 1);
            }

            statups.put(SecondaryStatFlag.DamAbsorbShield, nOption);
            this.getPlayer().temporaryStatSet(skillID, skillLevel, effect.getDuration(), statups);
         }
      }
   }

   public void giveEnhanceArrowStack(int targets, int skillID) {
      System.out.println(this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.EnhanceArrow, 0));
      SecondaryStatEffect enhanceArrowEff = null;
      SecondaryStatEffect advEnhanceArrowEff = null;
      int enhanceArrowLv;
      if ((enhanceArrowLv = this.getPlayer().getTotalSkillLevel(this.EnhanceArrow)) > 0) {
         enhanceArrowEff = SkillFactory.getSkill(this.EnhanceArrow).getEffect(enhanceArrowLv);
      }

      int advEnhanceArrowLv;
      if ((advEnhanceArrowLv = this.getPlayer().getTotalSkillLevel(this.AdvEnhanceArrow)) > 0) {
         advEnhanceArrowEff = SkillFactory.getSkill(this.AdvEnhanceArrow).getEffect(advEnhanceArrowLv);
      }

      if (enhanceArrowEff != null) {
         if (skillID != this.EnhancePiercing
            && skillID != this.AdvEnhancePiercing
            && skillID != this.EnhanceSniping
            && skillID != this.EnhanceSnipingVI
            && skillID != this.UltimateSnipingVI) {
            if (skillID == this.Piercing || skillID == this.AdvPiercing) {
               System.out.println("dlrjs?");
               int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.EnhanceArrow, 0) + 1;
               value = Math.min(3, value);
               System.out.println("인핸스 :" + value);
               if (targets > 0) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.EnhanceArrow, enhanceArrowEff.getSourceId(), Integer.MAX_VALUE, value);
               }
            } else if (skillID == this.Sniping || skillID == this.SnipingVI) {
               int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.EnhanceSniping, 0) + 1;
               value = Math.min(3, value);
               if (targets > 0) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.EnhanceSniping, enhanceArrowEff.getSourceId(), Integer.MAX_VALUE, value);
               }
            }
         } else {
            if (skillID != this.EnhanceSniping && skillID != this.EnhanceSnipingVI && skillID != this.UltimateSnipingVI) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.EnhanceArrow, enhanceArrowEff.getSourceId(), Integer.MAX_VALUE, 0);
            } else {
               if (skillID == this.UltimateSnipingVI) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.UltimateSniping, enhanceArrowEff.getSourceId(), Integer.MAX_VALUE, 0);
               }

               if (skillID == this.EnhanceSnipingVI) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.UltimateSniping, enhanceArrowEff.getSourceId(), Integer.MAX_VALUE, 1);
               }

               this.getPlayer().temporaryStatSet(SecondaryStatFlag.EnhanceSniping, enhanceArrowEff.getSourceId(), Integer.MAX_VALUE, 0);
            }

            if (advEnhanceArrowEff != null) {
               SkillFactory.getSkill(this.FocusOn).getEffect(advEnhanceArrowLv).applyTo(this.getPlayer());
            }
         }
      }
   }

   public void setEnhanceSniping(MapleMonster target, boolean isSet) {
      target.setAdventurerMark(isSet);
      if (!isSet) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ATTACK_ADVENTURER_MARK.getValue());
         packet.writeInt(this.EnhanceSnipingBonusAttack);
         packet.writeInt(3221025);
         packet.writeInt(0);
         packet.writeInt(1);
         packet.writeInt(target.getObjectId());
         packet.writeInt(586);
         this.getPlayer().send(packet.getPacket());
      }

      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_ADVENTURER_MARK.getValue());
      packet.writeInt(this.EnhanceSniping);
      packet.write(isSet);
      if (isSet) {
         packet.writeInt(1);
         packet.writeInt(target.getObjectId());
         packet.writeInt(1);
         packet.writeInt(0);
         packet.writeInt(5000);
         packet.writeInt(599);
      }

      this.getPlayer().send(packet.getPacket());
   }
}
