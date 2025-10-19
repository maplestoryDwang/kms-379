package objects.users.jobs.nova;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.MobPacket;
import objects.effect.child.SkillEffect;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.Thief;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.WeaponvVrietyFlag;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Cadena extends Thief {
   private int lastUseCadenaSkill = 0;
   private int weaponVarietyFlag = 0;
   private int weaponVarietyFinaleStack = 0;
   private boolean needleBatFirst = false;
   private boolean cooltimeBuff = false;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 60021278 || attack.skillID == 60021279 || attack.skillID == 64001001 || attack.skillID == 64001006) {
         this.getPlayer().temporaryStatSet(64001001, 1000, SecondaryStatFlag.NextAttackEnhance, 10);
      }

      if (attack.skillID == 64121003) {
         this.needleBatFirst = true;
      }

      if (attack.skillID == 64121016) {
         this.needleBatFirst = false;
      }

      if (attack.skillID == 64121011 || this.getPlayer().getBuffedValue(SecondaryStatFlag.EmpressBless) == null) {
         this.cooltimeBuff = false;
      }

      if (attack.skillID == 64001000 || attack.skillID >= 64001007 && attack.skillID <= 64001012) {
         PacketEncoder p = new PacketEncoder();
         p.write(attack.display & 32768);
         p.writeInt(0);
         p.writeInt(attack.position2.x);
         p.writeInt(attack.position2.y);
         SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), attack.skillID, attack.skillLevel, p);
         this.getPlayer().send(e.encodeForLocal());
         this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
         this.getPlayer().temporaryStatSet(64001010, 500, SecondaryStatFlag.DarkSight, 10);

         for (AttackPair ap : attack.allDamage) {
            SecondaryStatEffect eff = SkillFactory.getSkill(64001000).getEffect(attack.skillLevel);
            if (eff != null) {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
               if (mob != null) {
                  int duration = eff.getS2() * 1000;
                  if (Randomizer.isSuccess(eff.getS())) {
                     mob.applyStatus(
                        this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, -30, 64001000, null, false), false, duration, false, eff
                     );
                  }
               }
            }
         }
      }

      if (opcode != RecvPacketOpcode.SHOOT_ATTACK) {
         if (attack.skillID == 64121011) {
            SecondaryStatEffect eff = SkillFactory.getSkill(64121003).getEffect(attack.skillLevel);
            int prob = eff.getS2();
            if (Randomizer.isSuccess(prob)) {
               for (AttackPair apx : attack.allDamage) {
                  MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(apx.objectid);
                  if (mob != null) {
                     int duration = eff.getS() * 1000;
                     Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                     mse.put(MobTemporaryStatFlag.MAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.MAD, -20, attack.skillID, null, false));
                     mse.put(MobTemporaryStatFlag.PAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.PAD, -20, attack.skillID, null, false));
                     mob.applyMonsterBuff(mse, attack.skillID, duration, null, Collections.EMPTY_LIST);
                  }
               }
            }
         }

         if (attack.skillID == 64121001) {
            SecondaryStatEffect eff = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);

            for (AttackPair apxx : attack.allDamage) {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(apxx.objectid);
               if (mob != null) {
                  if (mob.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
                     mob.applyStatus(
                        this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false), false, 10000L, false, eff
                     );
                     mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), attack.skillID);
                  } else {
                     this.getPlayer()
                        .send(
                           MobPacket.monsterResist(
                              mob,
                              this.getPlayer(),
                              (int)((System.currentTimeMillis() - mob.getResistSkill(MobTemporaryStatFlag.FREEZE)) / 1000L),
                              attack.skillID
                           )
                        );
                  }
               }
            }
         }

         if (attack.skillID == 64111003) {
            SecondaryStatEffect eff = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);
            if (eff.makeChanceResult()) {
               for (AttackPair apxxx : attack.allDamage) {
                  MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(apxxx.objectid);
                  if (mob != null) {
                     Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                     mse.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, -30, attack.skillID, null, false));
                     mob.applyMonsterBuff(mse, attack.skillID, eff.getDuration(), null, Collections.EMPTY_LIST);
                  }
               }
            }
         }

         int sLevel = 0;
         if ((sLevel = this.getPlayer().getSkillLevel(64120007)) > 0) {
            SecondaryStatEffect eff = SkillFactory.getSkill(64120007).getEffect(sLevel);
            if (eff.makeChanceResult()) {
               long dmg = (long)(this.getPlayer().getStat().getCurrentMaxBaseDamage() * (eff.getDOT() * 0.01));
               int dotTime = eff.getDOTTime();
               long dotDmg = dmg / dotTime;

               for (AttackPair apxxxx : attack.allDamage) {
                  MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(apxxxx.objectid);
                  if (mob != null) {
                     MobTemporaryStatEffect me = new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, 64120007, null, false);
                     me.setPoisonDamage(dotDmg, this.getPlayer());
                     me.setDotCount(dotTime);
                     mob.applyStatus(this.getPlayer(), me, false, dotTime * 1000, false, eff);
                  }
               }
            }
         }

         if (attack.targets > 0
            && this.applyWeaponVariety(attack.skillID, attack.skillID == 64121002, attack)
            && attack.skillID != 64121053
            && attack.skillID != 64121055
            && this.getPlayer().getBuffedValue(SecondaryStatFlag.ProfessionalAgent) != null) {
            List<Pair<Integer, Integer>> l = new ArrayList<>();

            for (AttackPair apxxxxx : attack.allDamage) {
               if (apxxxxx != null) {
                  l.add(new Pair<>(apxxxxx.objectid, 500));
                  this.getPlayer().send(CField.userBonusAttackRequest(64121055, false, l));
                  break;
               }
            }
         }

         super.prepareAttack(attack, effect, opcode);
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
      if (attack.skillID == 400041041) {
         monster.applyStatus(
            this.getPlayer(),
            new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getZ(), attack.skillID, null, false),
            false,
            effect.getS() * 1000,
            false,
            effect
         );
      }

      if (attack.skillID == 64121052 && attack.targets > 0) {
         SecondaryStatEffect eff = null;
         int sLevel = 0;
         if ((sLevel = this.getPlayer().getSkillLevel(64120006)) > 0) {
            eff = SkillFactory.getSkill(64120006).getEffect(sLevel);
         } else if ((sLevel = this.getPlayer().getSkillLevel(64110005)) > 0) {
            eff = SkillFactory.getSkill(64110005).getEffect(sLevel);
         } else if ((sLevel = this.getPlayer().getSkillLevel(64100004)) > 0) {
            eff = SkillFactory.getSkill(64100004).getEffect(sLevel);
         }

         if (eff != null) {
            this.getPlayer().temporaryStatSet(eff.getSourceId(), 15000, SecondaryStatFlag.WeaponVariety, 8);
         }
      }

      if (totalDamage > 0L
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.ChainArtsFury) != null
         && attack.skillID != 40041036
         && (this.getPlayer().getLastActiveChainArtsFuryTime() == 0L || System.currentTimeMillis() - this.getPlayer().getLastActiveChainArtsFuryTime() > 600L)) {
         this.getPlayer().setLastActiveChainArtsFuryTime(System.currentTimeMillis());
         this.getPlayer().send(CField.getActiveChainArtsFury(monster.getTruePosition().x, monster.getTruePosition().y));
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
      if (attack.skillID == 64141001 || attack.skillID == 64141002) {
         if (attack.skillID == 64141002) {
            Integer count = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.ChainArtsStrokeVI, 0);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.ChainArtsStrokeVI, 64141000, Integer.MAX_VALUE, Math.max(0, count - 1));
            this.getPlayer().send(CField.userBonusAttackRequest(64141003, true, Collections.emptyList()));
         }

         for (AttackPair pair : attack.allDamage) {
            int objId = pair.objectid;
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(objId);
            if (mob != null) {
               mob.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getZ(), attack.skillID, null, false),
                  false,
                  effect.getDuration(),
                  false,
                  effect
               );
            }
         }
      }
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 64001012:
            this.getPlayer().temporaryStatSet(64001010, 2000, SecondaryStatFlag.DarkSight, 10);
            break;
         case 400041041:
            Pair<Point, Integer> pair = this.teleportAttackAction.getPointWithDirection();
            if (pair != null) {
               boolean flip = pair.right == 1;
               Rect rect = new Rect(pair.left, effect.getLt(), effect.getRb(), flip);
               AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(400041041, this.getPlayer().getId());
               if (area != null) {
                  this.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 400041041, false));
                  this.getPlayer().getMap().removeMapObject(area);
               }

               AffectedArea aa = new AffectedArea(
                  rect, this.getPlayer(), effect, this.getPlayer().getMap().calcPointBelow(pair.left), System.currentTimeMillis() + effect.getDuration()
               );
               aa.setRlType(flip ? 1 : 0);
               this.getPlayer().getMap().spawnMist(aa);
            }
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   private void extraBonusAttackWork() {
      if (!this.getPlayer().hasBuffBySkillID(64121025)) {
         SecondaryStatEffect darkSight = SkillFactory.getSkill(64121025).getEffect(this.getActiveSkillLevel());
         SecondaryStatEffect shield = SkillFactory.getSkill(64121026).getEffect(this.getActiveSkillLevel());
         if (darkSight == null || shield == null) {
            darkSight = SkillFactory.getSkill(64121025).getEffect(1);
            shield = SkillFactory.getSkill(64121026).getEffect(1);
         }

         this.getPlayer().temporaryStatSet(SecondaryStatFlag.DarkSight, 64121025, darkSight.getDuration(), 1);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieBarrier, 64121026, shield.getDuration(), shield.getX());
      }
   }

   public boolean applyWeaponVariety(int skillID, boolean takeDown, AttackInfo attack) {
      SecondaryStatEffect eff = null;
      int sLevel = 0;
      if ((sLevel = this.getPlayer().getSkillLevel(64120006)) > 0) {
         eff = SkillFactory.getSkill(64120006).getEffect(sLevel);
      } else if ((sLevel = this.getPlayer().getSkillLevel(64110005)) > 0) {
         eff = SkillFactory.getSkill(64110005).getEffect(sLevel);
      } else if ((sLevel = this.getPlayer().getSkillLevel(64100004)) > 0) {
         eff = SkillFactory.getSkill(64100004).getEffect(sLevel);
      }

      if (skillID == 64121001) {
         takeDown = true;
      }

      if (eff != null) {
         WeaponvVrietyFlag flag = WeaponvVrietyFlag.getFlagBySkillID(skillID);
         if (flag != WeaponvVrietyFlag.NONE || takeDown) {
            if (this.lastUseCadenaSkill == this.getRealKadenaSkillID(skillID) && !takeDown) {
               return false;
            }

            int stack = 0;
            int f = this.weaponVarietyFlag;
            if (flag == WeaponvVrietyFlag.TAKE_DOWN && takeDown) {
               if (f != WeaponvVrietyFlag.TAKE_DOWN.getFlag()) {
                  int var15 = 8;
                  this.weaponVarietyFlag = WeaponvVrietyFlag.TAKE_DOWN.getFlag();
               }
            } else if (flag != WeaponvVrietyFlag.TAKE_DOWN && (f & flag.getFlag()) == 0) {
               f |= flag.getFlag();
               this.weaponVarietyFlag = f;
            }

            stack = this.getWeaponVarietyStack();
            Integer varietyValue = this.getPlayer().getBuffedValue(SecondaryStatFlag.WeaponVariety);
            if (varietyValue != null) {
               int buffStack = varietyValue >= stack ? varietyValue : stack;
               this.getPlayer().temporaryStatSet(eff.getSourceId(), eff.getDuration(), SecondaryStatFlag.WeaponVariety, buffStack);
            } else {
               this.getPlayer().temporaryStatSet(eff.getSourceId(), eff.getDuration(), SecondaryStatFlag.WeaponVariety, stack);
            }

            this.setlastUseCadenaSkill(skillID);
            if (eff.getSourceId() == 64100004) {
               this.getPlayer().send(CField.userBonusAttackRequest(64101009, true, Collections.EMPTY_LIST));
            } else if (eff.getSourceId() == 64110005) {
               this.getPlayer().send(CField.userBonusAttackRequest(64111013, true, Collections.EMPTY_LIST));
            } else if (eff.getSourceId() == 64120006) {
               this.getPlayer().send(CField.userBonusAttackRequest(64121020, true, Collections.EMPTY_LIST));
               this.extraBonusAttackWork();
            }

            if (attack != null) {
               Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.WeaponVarietyFinale);
               if (value != null) {
                  SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400041074);
                  if (e != null) {
                     this.weaponVarietyFinaleStack++;
                     if (this.weaponVarietyFinaleStack >= e.getW()) {
                        value = value - 1;
                        this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), (attack.display & 32768) != 0, 400041074, 5);
                        this.weaponVarietyFinaleStack = 0;
                        if (value <= 0) {
                           this.getPlayer().temporaryStatReset(SecondaryStatFlag.WeaponVarietyFinale);
                        }
                     }

                     this.getPlayer().temporaryStatSet(400041074, Integer.MAX_VALUE, SecondaryStatFlag.WeaponVarietyFinale, value);
                  }
               }
            }
         }
      }

      return true;
   }

   public int getWeaponVarietyStack() {
      int flag = this.weaponVarietyFlag;
      int count = 0;
      if ((flag & WeaponvVrietyFlag.SUMMON_CUTTING_SCIMITAR.getFlag()) > 0) {
         count++;
      }

      if ((flag & WeaponvVrietyFlag.SUMMON_SCRATCHING_CLAW.getFlag()) > 0) {
         count++;
      }

      if ((flag & WeaponvVrietyFlag.SUMMON_THROWING_WING_DAGGER.getFlag()) > 0) {
         count++;
      }

      if ((flag & WeaponvVrietyFlag.SUMMON_SHOOTING_SHOTGUN.getFlag()) > 0) {
         count++;
      }

      if ((flag & WeaponvVrietyFlag.SUMMON_SLASHING_KNIFE.getFlag()) > 0) {
         count++;
      }

      if ((flag & WeaponvVrietyFlag.SUMMON_RELEASING_BOMB.getFlag()) > 0) {
         count++;
      }

      if ((flag & WeaponvVrietyFlag.SUMMON_STRIKING_BRICK.getFlag()) > 0) {
         count++;
      }

      if ((flag & WeaponvVrietyFlag.SUMMON_BITING_NEEDLE_BAT.getFlag()) > 0) {
         count++;
      }

      if (flag == WeaponvVrietyFlag.TAKE_DOWN.getFlag()) {
         count = 8;
      }

      return count;
   }

   public int getRealKadenaSkillID(int skillID) {
      int skill = 0;
      switch (skillID) {
         case 64001002:
         case 64001013:
            skill = 64001002;
            break;
         case 64101001:
            skill = 64101001;
            break;
         case 64101002:
         case 64101008:
            skill = 64101002;
            break;
         case 64111002:
            skill = 64111002;
            break;
         case 64111003:
            skill = 64111003;
            break;
         case 64111004:
         case 64111012:
            skill = 64111004;
            break;
         case 64121001:
         case 64121012:
         case 64121013:
         case 64121014:
         case 64121015:
         case 64121017:
         case 64121018:
         case 64121019:
            skill = 64121001;
            break;
         case 64121003:
         case 64121011:
         case 64121016:
            skill = 64121003;
            break;
         case 64121021:
         case 64121022:
         case 64121023:
         case 64121024:
            skill = 64121021;
            break;
         default:
            skill = skillID;
      }

      return skill;
   }

   public void setlastUseCadenaSkill(int skillID) {
      this.lastUseCadenaSkill = this.getRealKadenaSkillID(skillID);
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case WeaponVariety:
            packet.writeInt(this.weaponVarietyFlag);
            break;
         case WeaponVarietyFinale:
            packet.writeInt(this.weaponVarietyFinaleStack);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      super.activeSkillCancel();
   }
}
