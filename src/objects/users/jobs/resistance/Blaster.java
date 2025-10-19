package objects.users.jobs.resistance;

import constants.GameConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.effect.child.SkillEffect;
import objects.effect.child.SpecialSkillEffect;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.Warrior;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Pair;

public class Blaster extends Warrior {
   private int bulletSkillID = 0;
   private int burningBreakerCount = 0;
   public int rwMagnumBlow;
   public int rwMagnumBlowX;
   public int rwCylinderB;
   public int rwCylinderC;
   public int rwCylinderM;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      int chargeDur = 0;
      if (attack.skillID == 37110001) {
         SecondaryStatEffect eff = SkillFactory.getSkill(37110002).getEffect(attack.skillLevel);
         if (eff != null) {
            Rectangle rect = eff.calculateBoundingBox(attack.forcedPos, this.getPlayer().isFacingLeft());
            Point pos = this.getPlayer().getMap().calcDropPos(new Point(rect.x, rect.y - 23), this.getPlayer().getPosition());
            this.getPlayer().getMap().spawnMist(new AffectedArea(rect, this.getPlayer(), eff, pos, System.currentTimeMillis() + eff.getDuration()));
            chargeDur = eff.getDuration();
         }
      }

      if (attack.skillID == 37101001 || attack.skillID == 37110004 || attack.skillID == 37110001) {
         SecondaryStatEffect eff = null;
         if (this.getPlayer().getTotalSkillLevel(37120011) > 0) {
            eff = SkillFactory.getSkill(37120011).getEffect(this.getPlayer().getTotalSkillLevel(37120011));
         } else {
            eff = SkillFactory.getSkill(37100006).getEffect(this.getPlayer().getTotalSkillLevel(37100006));
         }

         if (eff != null) {
            this.getPlayer()
               .temporaryStatSet(SecondaryStatFlag.EpicDropRIncrease, eff.getSourceId(), chargeDur == 0 ? 300 : chargeDur, eff.getDamAbsorbShieldR());
         }
      }

      if (attack.skillID == 37121052) {
         this.getPlayer().temporaryStatResetBySkillID(37121052);
      }

      if ((attack.skillID == 37001000 || attack.skillID == 37101000) && this.rwCylinderB > 0) {
         List<ExtraSkillInfo> extraSkills = new ArrayList<>();
         extraSkills.add(new ExtraSkillInfo(37001001, 0));
         this.getPlayer()
            .send(CField.getRegisterExtraSkill(attack.skillID, attack.forcedPos.x, attack.forcedPos.y, (attack.display & 32768) != 0, extraSkills, 1));
      }

      if (GameConstants.isRWNeedBulletSkill(attack.skillID)) {
         int cylinder = -1;
         if (attack.skillID == 37000009 || attack.skillID == 37100008 || attack.skillID == 400011019 && this.bulletSkillID != 37111006) {
            cylinder = this.rwCylinderC + 1;
         }

         this.setRWCylinder(attack.skillID, this.rwCylinderB - 1, cylinder, -1);
         this.checkRWReloadBullet();
      }

      if (attack.skillID == 37111006 && this.bulletSkillID != 37111006) {
         this.setRWCylinder(attack.skillID, this.rwCylinderB - 1, this.rwCylinderC + 1, -1);
         this.checkRWReloadBullet();
      }

      if (GameConstants.isRWReleasePileBunker(attack.skillID)) {
         this.setRWCylinder(attack.skillID, this.rwCylinderB, -1, -1);
         this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), this.getPlayer().isFacingLeft(), attack.skillID);
      }

      if (attack.skillID == 37121000 || attack.skillID == 37121052) {
         this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), (attack.display & 32768) != 0, attack.skillID);
      }

      if (GameConstants.isRWChargingSkill(attack.skillID) && attack.canRWCharge != 0) {
         this.onRWAutoReload(attack.skillID);
      }

      if (attack.skillID == 37121003) {
         this.getPlayer().onRWCombination(attack.skillID, 100);
         this.getPlayer().giveCoolDowns(attack.skillID, System.currentTimeMillis(), 15000L);
         this.getPlayer().send(CField.skillCooldown(attack.skillID, 15000));
      }

      Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.AfterImageShock);
      if (attack.targets > 0) {
         if (v != null) {
            if (SkillFactory.getSkill(400011116).getSkillList().contains(attack.skillID)) {
               if (v > 0) {
                  List<Pair<Integer, Integer>> mobs = new ArrayList<>();
                  int delay = 168;

                  for (AttackPair pair : attack.allDamage) {
                     mobs.add(new Pair<>(pair.objectid, delay));
                     delay += 70;
                  }

                  this.getPlayer().send(CField.userBonusAttackRequest(400011133, false, mobs, 0, 0, attack.skillID));
                  v = v - 1;
                  SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.AfterImageShock, 400011116, v);
                  statManager.temporaryStatSet();
               } else {
                  this.getPlayer().temporaryStatReset(SecondaryStatFlag.AfterImageShock);
               }
            }
         } else if (SkillFactory.getSkill(400011116).getSkillList().contains(attack.skillID)) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400011116);
            if (e != null && this.getPlayer().getCooldownLimit(400011117) <= 0L) {
               SecondaryStatEffect e2 = SkillFactory.getSkill(400011117).getEffect(this.getPlayer().getTotalSkillLevel(400011116));
               if (e2 != null) {
                  List<MapleMonster> mobs = this.getPlayer()
                     .getMap()
                     .getMobsInRect(this.getPlayer().getPosition(), e2.getLt().x, e2.getLt().y, e2.getRb().x, e2.getRb().y);
                  int mobCount = e.getV();
                  List<Pair<Integer, Integer>> targets = new ArrayList<>();

                  for (MapleMonster mob : mobs) {
                     boolean f = false;

                     for (AttackPair pair : attack.allDamage) {
                        if (pair.objectid == mob.getObjectId()) {
                           f = true;
                           break;
                        }
                     }

                     if (!f) {
                        targets.add(new Pair<>(mob.getObjectId(), 0));
                     }

                     if (targets.size() >= mobCount) {
                        break;
                     }
                  }

                  if (targets.size() > 0) {
                     this.getPlayer().send(CField.userBonusAttackRequest(400011117, false, targets, 0, 0, attack.skillID));
                     this.getPlayer().giveCoolDowns(400011117, System.currentTimeMillis(), e.getX() * 1000);
                     this.getPlayer().send(CField.skillCooldown(400011117, e.getX() * 1000));
                  }
               }
            }
         }
      }

      if (SkillFactory.getSkill(400011017).getSkillList().contains(attack.skillID)
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.ProfessionalAgent) != null) {
         this.getPlayer().send(CField.userBonusAttackRequest(400011019, true, Collections.EMPTY_LIST));
      }

      if (this.getPlayer().getSkillLevel(37000007) > 0
         && attack.targets != 0
         && GameConstants.isBlasterCanonMasterySkill(attack.skillID)
         && this.rwCylinderC > 0) {
         this.getPlayer().sendRegisterExtraSkillIndex(attack.forcedPos, (attack.display & 32768) != 0, 37121000, 1);
      }

      this.bulletSkillID = attack.skillID;
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
      if (attack.skillID == 400010028) {
         this.getPlayer().onRWCombination(attack.skillID, 100);
      }

      if (attack.skillID == 37121004) {
         this.setRWCylinder(37121004, -1, -1, -1);
         MobTemporaryStatEffect eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false);
         eff.setX(this.getPlayer().getId());
         if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
            monster.applyStatus(this.getPlayer(), eff, false, effect.getDuration(), false, effect);
            monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), attack.skillID);
            this.sendRWZeroBunkerMobBind(true, monster.isAlive(), monster.getObjectId(), monster.getId(), monster.getPosition());
         } else {
            this.getPlayer()
               .send(
                  MobPacket.monsterResist(
                     monster,
                     this.getPlayer(),
                     (int)((monster.getResistSkill(MobTemporaryStatFlag.FREEZE) - System.currentTimeMillis()) / 1000L),
                     attack.skillID
                  )
               );
         }

         this.getPlayer().temporaryStatSet(37121004, 11000, SecondaryStatFlag.NotDamaged, 1);
      }

      if (totalDamage > 0L && (attack.skillID == 37111000 || attack.skillID == 37110001)) {
         monster.applyStatus(
            this.getPlayer(),
            new MobTemporaryStatEffect(MobTemporaryStatFlag.RW_CHOPPING_HAMMER, effect.getX(), attack.skillID, null, false),
            false,
            effect.getY() * 1000,
            false,
            effect
         );
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400011091) {
         this.burningBreakerCount = 0;
         this.setRWCylinder(400011091, this.rwCylinderB, this.rwCylinderC, 0);
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 37121052 || this.getActiveSkillPrepareID() == 400011028) {
         SecondaryStatEffect effect = null;
         if (this.getPlayer().getTotalSkillLevel(37120011) > 0) {
            effect = SkillFactory.getSkill(37120011).getEffect(this.getPlayer().getTotalSkillLevel(37120011));
         } else {
            effect = SkillFactory.getSkill(37100006).getEffect(this.getPlayer().getTotalSkillLevel(37100006));
         }

         if (effect != null) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.EpicDropRIncrease, this.getActiveSkillPrepareID(), 4000, effect.getDamAbsorbShieldR());
         }
      }

      if (this.getActiveSkillPrepareID() == 400011091) {
         this.getPlayer().temporaryStatSet(400011091, 5000, SecondaryStatFlag.indiePartialNotDamaged, 1);
         this.getPlayer().onRWCombination(this.getActiveSkillPrepareID(), 100);
         this.setRWCylinder(400011091, 5, -1, -1);
         this.checkRWReloadBullet();
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
      if (effect == null) {
         this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
      } else {
         if (GameConstants.isRWMultiChargeSkill(this.getActiveSkillID())) {
            this.regWarriorCharge(effect);
         }

         if (this.getActiveSkillID() != 37111000) {
            if (this.getActiveSkillID() == 37001001) {
               effect.applyTo(this.getPlayer());
            } else {
               super.beforeActiveSkill(packet);
            }
         }
      }
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 37100002:
         case 37101001:
         case 37110001:
         case 37110004:
         case 37111003:
            this.getPlayer().temporaryStatSet(this.getActiveSkillID(), effect.getSubTime() / 1000, SecondaryStatFlag.RWMovingEvar, effect.getW());
            effect.applyTo(this.getPlayer());
            return;
         case 400011103:
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400011091);
            if (eff != null && this.burningBreakerCount < eff.getW()) {
               if (this.rwCylinderB - 1 < 0) {
                  return;
               }

               this.burningBreakerCount++;
               if (this.burningBreakerCount > 5) {
                  this.burningBreakerCount = 5;
               }

               this.setRWCylinder(this.getActiveSkillID(), this.rwCylinderB - 1, -1, this.burningBreakerCount);
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400011091) {
         this.getPlayer().temporaryStatSet(this.getActiveSkillID(), 3000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      } else if (this.getActiveSkillID() == 37121052) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.RWMagnumBlow);
      } else if (this.getActiveSkillID() == 400011028) {
         this.getPlayer().temporaryStatResetBySkillID(400011028);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.RWBarrier) != null) {
         SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.RWBarrier);
         if (effect != null) {
            int y = effect.getY();
            int z = effect.getZ();
            Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.RWBarrier);
            int v = 0;
            if (value != null) {
               v = value;
            }

            if (v == 0) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.RWBarrier);
               return;
            }

            int shield = v - (int)(v * (y * 0.01) + z);
            if (shield <= 0) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.RWBarrier);
            } else {
               this.getPlayer().temporaryStatSet(effect.getSourceId(), Integer.MAX_VALUE, SecondaryStatFlag.RWBarrier, shield);
            }
         }
      }
   }

   public void regWarriorCharge(SecondaryStatEffect effect) {
      int skillID = effect.getSourceId();
      int skillLevel = effect.getLevel();
      if (skillID == 37000010) {
         this.setRWCylinder(skillID, effect.getU2(), -1, -1);
      }

      SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), skillID, skillLevel, null);
      this.getPlayer().send(e.encodeForLocal());
      this.checkRWReloadBullet();
   }

   public void onRWAutoReload(int skillID) {
      int slv = 0;
      SecondaryStatEffect effect = null;
      if ((slv = this.getPlayer().getTotalSkillLevel(37120011)) > 0) {
         effect = SkillFactory.getSkill(37120011).getEffect(slv);
      } else {
         effect = SkillFactory.getSkill(37100006).getEffect(this.getPlayer().getTotalSkillLevel(37100006));
      }

      if (effect != null) {
         SpecialSkillEffect e = new SpecialSkillEffect(this.getPlayer().getId(), effect.getSourceId(), null);
         this.getPlayer().send(e.encodeForLocal());
      }

      int bullet = effect.getW();
      this.setRWCylinder(skillID, this.rwCylinderB + bullet, -1, -1);
   }

   public void checkRWReloadBullet() {
      if (GameConstants.isBlaster(this.getPlayer().getJob())) {
         if (this.rwCylinderB == 0) {
            int slv = this.getPlayer().getTotalSkillLevel(GameConstants.getLinkedAranSkill(37000010));
            if (slv != 0) {
               SecondaryStatEffect effect = SkillFactory.getSkill(37000010).getEffect(slv);
               if (effect != null) {
                  this.setRWCylinder(37000010, effect.getU2(), -1, -1);
               }
            }
         }
      }
   }

   public void setRWMagnumBlow(int skillID, SecondaryStatEffect effect, int value, int b, int x) {
      if (b != -1) {
         this.rwMagnumBlow = b;
      }

      if (x != -1) {
         this.rwMagnumBlowX = x;
      }

      this.getPlayer().temporaryStatSet(skillID, effect.getSubTime(), SecondaryStatFlag.RWMagnumBlow, value);
   }

   public void sendRWZeroBunkerMobBind(boolean success, boolean isAlive, int mobID, int templateID, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_RW_ZERO_BUNKER_MOB_BIND.getValue());
      packet.writeInt(this.getPlayer().getId());
      packet.write(success);
      packet.write(isAlive);
      packet.writeInt(mobID);
      packet.writeInt(templateID);
      packet.writeInt(pos.x);
      packet.writeInt(pos.y);
      this.getPlayer().getMap().broadcastMessage(packet.getPacket());
   }

   public void setRWCylinder(int skillID, int bullet, int cylinder, int m) {
      byte max = 3;
      int slv = 0;
      if ((slv = this.getPlayer().getTotalSkillLevel(37120008)) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(37120008).getEffect(slv);
         if (effect != null) {
            max = (byte)(max + effect.getY());
         }
      } else if ((slv = this.getPlayer().getTotalSkillLevel(37110007)) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(37110007).getEffect(slv);
         if (effect != null) {
            max = (byte)(max + effect.getY());
         }
      } else if ((slv = this.getPlayer().getTotalSkillLevel(37100007)) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(37100007).getEffect(slv);
         if (effect != null) {
            max = (byte)(max + effect.getY());
         }
      }

      if (bullet != -1) {
         this.rwCylinderB = (byte)Math.max(0, Math.min(bullet, max));
      }

      if (cylinder != -1) {
         this.rwCylinderC = (byte)Math.max(0, Math.min(cylinder, max));
      }

      if (m != -1) {
         this.rwCylinderM = (byte)m;
      }

      if (bullet == -1 && cylinder == -1 && m == -1) {
         this.rwCylinderB = max;
      }

      if (GameConstants.isRWReleasePileBunker(skillID)) {
         this.getPlayer()
            .temporaryStatSet(
               37001002, this.getPlayer().getBuffedValue(SecondaryStatFlag.RWMaximizeCannon) != null ? 1000 : 7000, SecondaryStatFlag.RWOverHeat, 1
            );
         this.rwCylinderC = max;
      }

      this.getPlayer().temporaryStatSet(skillID, Integer.MAX_VALUE, SecondaryStatFlag.RWCylinder, 1);
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case RWMagnumBlow:
            packet.writeShort(this.rwMagnumBlow);
            packet.write(this.rwMagnumBlowX);
            break;
         case RWCylinder:
            packet.write(this.rwCylinderB);
            packet.writeShort(this.rwCylinderC);
            packet.write(this.rwCylinderM);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
