package objects.users.jobs.kinesis;

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
import objects.fields.ForceAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.Magician;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;

public class Kinesis extends Magician {
   private int lawOfGravityTargetID = 0;
   private int PPoint = 0;
   private int MaxPPoint = 0;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 400021074) {
         this.givePPoint(attack.skillID, false, attack.targets);
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
      if (attack.skillID == 400021096) {
         SecondaryStatEffect eff = SkillFactory.getSkill(400021096).getEffect(this.getPlayer().getTotalSkillLevel(400021096));
         if (eff != null) {
            this.lawOfGravityTargetID = monster.getObjectId();
            eff.applyTo(this.getPlayer());
            this.getPlayer().send(CField.skillCooldown(400021096, eff.getCooldown(this.getPlayer())));
            this.getPlayer().addCooldown(400021096, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
         }
      }

      if (attack.skillID != 142001002 && attack.skillID != 142120000 && attack.skillID != 142120002 && attack.skillID != 142141000) {
         if (attack.skillID == 142121031) {
            if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
               monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false),
                  false,
                  effect.getDuration(),
                  false,
                  effect
               );
               monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), attack.skillID);
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
         }
      } else if (effect.makeChanceResult()) {
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> statups = new HashMap<>();
         statups.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, -effect.getX(), attack.skillID, null, false));
         statups.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, -effect.getX(), attack.skillID, null, false));
         monster.applyMonsterBuff(statups, attack.skillID, effect.getDuration(), null, Collections.EMPTY_LIST);
      }

      if (monster.getStats().isBoss()
         && (
            attack.skillID == 142111002
               || attack.skillID == 142120000
               || attack.skillID == 142110003
               || attack.skillID == 142110015
               || attack.skillID == 142120001
               || attack.skillID == 142120014
         )
         && this.getPlayer().getTotalSkillLevel(142120033) > 0) {
         this.gainPP(1);
      }

      if (attack.skillID == 142111002
         || attack.skillID == 142120000
         || attack.skillID == 142110003
         || attack.skillID == 142110015
         || attack.skillID == 142120001
         || attack.skillID == 142120014) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(142120035);
         if (eff != null) {
            int buffTime = eff.getDuration(eff.getDuration(), this.getPlayer());
            this.getPlayer().temporaryStatResetBySkillID(142120035);
            this.getPlayer().temporaryStatSet(142120035, buffTime, SecondaryStatFlag.indiePddR, eff.getX());
         }
      }

      if (attack.skillID == 142101003) {
         List<MobTemporaryStatFlag> cancel_ = new ArrayList<>();
         cancel_.add(MobTemporaryStatFlag.P_GUARD_UP);
         cancel_.add(MobTemporaryStatFlag.M_GUARD_UP);
         cancel_.add(MobTemporaryStatFlag.POWER_UP);
         cancel_.add(MobTemporaryStatFlag.MAGIC_UP);
         cancel_.add(MobTemporaryStatFlag.HARD_SKIN);
         monster.cancelStatus(cancel_);
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(142110011);
      if (attack.targets > 0 && attack.skillID != 142110011 && eff != null) {
         boolean chance = false;
         if (this.getPlayer().getTotalSkillLevel(142120039) > 0) {
            chance = eff.makeChanceWithBonusResult(30);
         } else {
            chance = eff.makeChanceResult();
         }

         if (chance) {
            int bulletCount = eff.getX();
            if (this.getPlayer().getTotalSkillLevel(142120041) > 0) {
               bulletCount++;
            }

            List<Integer> targets = new ArrayList<>();
            int count = 0;

            for (MapleMonster mob : this.getPlayer().getMap().getMobsInRect(this.getPlayer().getPosition(), -400, -400, 400, 400)) {
               targets.add(mob.getObjectId());
               if (++count >= bulletCount) {
                  break;
               }
            }

            if (!targets.isEmpty()) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initTeleKinesis();
               ForceAtom forceAtom = new ForceAtom(
                  info, 142110011, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.TELE_KINESIS, targets, targets.size()
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
         }
      }

      if (attack.skillID == 142121004) {
         int count = 0;

         for (AttackPair oned : attack.allDamage) {
            MapleMonster monster = this.getPlayer().getMap().getMonsterByOid(oned.objectid);
            if (monster != null) {
               if (monster.getStats().isBoss()) {
                  count += effect.getX();
               } else {
                  count++;
               }
            }
         }

         count = Math.min(count, effect.getW());
         int buffTime = effect.getDuration(effect.getDuration(), this.getPlayer());
         this.getPlayer().temporaryStatSet(attack.skillID, buffTime, SecondaryStatFlag.indiePMDR, count * effect.getIndiePMdR());
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 142111010) {
         this.givePPoint(this.getActiveSkillID(), true, (byte)0);
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 142121008:
            this.gainPP(this.getMaxPP() / 2);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 142141500:
            this.gainPP(-5);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   public void gainPP(int pp) {
      this.MaxPPoint = this.getMaxPP();
      this.PPoint = Math.min(this.MaxPPoint, this.PPoint + pp);
      this.getPlayer().temporaryStatSet(140001289, Integer.MAX_VALUE, SecondaryStatFlag.KinesisPsychicPoint, this.PPoint);
   }

   public int getPPoint() {
      return this.PPoint;
   }

   public void givePPoint(int skillID, boolean psychicFirst, byte targets) {
      if (skillID != 142120002 && skillID != 142111007 && skillID != 400021009) {
         SecondaryStatEffect effects = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getTotalSkillLevel(skillID));
         this.MaxPPoint = this.getMaxPP();
         if (effects != null) {
            if (effects.getPPCon() <= 0) {
               if (skillID == 142111010) {
                  this.PPoint = this.PPoint - effects.getX();
               } else if (effects.getPPRecovery() > 0) {
                  this.PPoint = this.PPoint + effects.getPPRecovery();
               } else if (skillID == 142121008) {
                  this.PPoint += 15;
               } else if (skillID == 142121005) {
                  if (targets > 0) {
                     this.PPoint--;
                  }
               } else {
                  this.PPoint++;
               }
            } else if (psychicFirst && (skillID == 400021008 || skillID == 400021048 || skillID == 400021053)
               || skillID != 400021008 && skillID != 400021053 && skillID != 400021048) {
               int pp = effects.getPPCon();
               Integer value;
               if ((value = this.getPlayer().getBuffedValue(SecondaryStatFlag.KinesisPsychicOver)) != null) {
                  pp = (100 - Math.max(0, Math.min(100, value))) * pp / 100;
               }

               this.PPoint -= pp;
            }

            if (skillID == 400021074 && targets > 0) {
               this.PPoint = this.PPoint + effects.getPPRecovery();
            }
         }

         if (this.PPoint < 0) {
            this.PPoint = 0;
         }

         if (this.MaxPPoint < this.PPoint) {
            this.PPoint = this.MaxPPoint;
         }

         this.getPlayer().temporaryStatSet(140001289, Integer.MAX_VALUE, SecondaryStatFlag.KinesisPsychicPoint, this.PPoint);
      }
   }

   public int getMaxPP() {
      this.MaxPPoint = 0;
      switch (this.getPlayer().getJob()) {
         case 14200:
            this.MaxPPoint = 10;
            break;
         case 14210:
            this.MaxPPoint = 15;
            break;
         case 14211:
            this.MaxPPoint = 20;
            break;
         case 14212:
            this.MaxPPoint = 30;
      }

      int hyperLevel = this.getPlayer().getHyperStat().getStat().getSkillLevel(80000406);
      if (hyperLevel > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(80000406).getEffect(hyperLevel);
         if (eff != null) {
            this.MaxPPoint = this.MaxPPoint + eff.getMPP();
         }
      }

      return this.MaxPPoint;
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.KinesisPsychicOver) != null) {
         this.gainPP(1);
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case LawOfGravity:
            packet.writeInt(this.lawOfGravityTargetID);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
