package objects.users.jobs.nova;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.RecvPacketOpcode;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.Warrior;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Kaiser extends Warrior {
   private long lastDragonBlazeFlameTime = 0L;
   private long lastKaiserSelfRecoveryTime = 0L;
   private long lastWillOfSwordCharge = 0L;
   public int SmashStackX = 0;
   public int SmashStackC = 0;
   public int SmashStackA = 60000;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 400011079 || attack.skillID == 400011080) {
         this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), (attack.display & 32768) != 0, attack.skillID);
      }

      if (attack.skillID == 61121052) {
         this.getPlayer().temporaryStatSet(61121052, 3000, SecondaryStatFlag.NotDamaged, 1);
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
      if ((attack.skillID == 61101101 || attack.skillID == 61111217) && effect.makeChanceResult()) {
         monster.applyStatus(
            this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, attack.skillID, null, false), false, effect.getDuration(), true, effect
         );
      }

      if ((attack.skillID == 61111100 || attack.skillID == 61111113 || attack.skillID == 61111218) && effect.makeChanceResult()) {
         int bonusTime = 0;
         if (this.getPlayer().getTotalSkillLevel(61120044) > 0) {
            SecondaryStatEffect e = SkillFactory.getSkill(61120044).getEffect(this.getPlayer().getTotalSkillLevel(61120044));
            if (e != null) {
               bonusTime = e.getTime() * 1000;
            }
         }

         monster.applyStatus(
            this.getPlayer(),
            new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getZ(), attack.skillID, null, false),
            false,
            effect.getSubTime() + bonusTime,
            true,
            effect
         );
      }

      if ((attack.skillID == 61111101 || attack.skillID == 61111219) && effect.makeChanceResult()) {
         monster.applyStatus(
            this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, attack.skillID, null, false), false, 1000L, true, effect
         );
      }

      this.setSmashStack(attack.skillID);
      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.targets > 0) {
         if (attack.skillID == 61121105 || attack.skillID == 61121222) {
            SecondaryStatEffect mist = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);
            if (mist != null) {
               for (Point pos : attack.affectedSpawnPos) {
                  Point p = this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition());
                  Rect rect = new Rect(p, mist.getLt2(), mist.getRb2(), false);
                  int time = mist.getDuration() + this.getPlayer().getSkillLevelDataOne(attack.skillID, SecondaryStatEffect::getDuration);
                  AffectedArea aa = new AffectedArea(rect, this.getPlayer(), mist, p, System.currentTimeMillis() + time);
                  this.getPlayer().getMap().spawnMist(aa);
               }
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DevilishPower) != null) {
            if (this.getPlayer().getCooldownLimit(400111119) <= 0L && (attack.skillID < 400011118 || attack.skillID > 400011120) && attack.skillID != 400011130
               )
             {
               this.getPlayer()
                  .addCooldown(
                     400111119,
                     System.currentTimeMillis(),
                     (int)SkillFactory.getSkill(400011118).getEffect(this.getPlayer().getSkillLevel(400011118)).getT() * 1000
                  );
               this.getPlayer().send(CField.userBonusAttackRequest(400011130, true, Collections.EMPTY_LIST));
            }
         } else if (this.getPlayer().getCooldownLimit(400011118) > 0L) {
            SecondaryStatEffect e2 = this.getPlayer().getSkillLevelData(400011118);
            if (e2 != null && this.getPlayer().checkInterval(this.lastDragonBlazeFlameTime, e2.getV2() * 1000)) {
               List<MapleMonster> mobs = this.getPlayer().getMap().getMobsInRect(this.getPlayer().getPosition(), -500, -500, 500, 500);
               if (mobs != null && mobs.size() > 0) {
                  Skill s = SkillFactory.getSkill(400011120);
                  SecondaryStatEffect e = s.getEffect(this.getPlayer().getTotalSkillLevel(400011118));
                  if (e != null) {
                     int mobCount_ = e.getMobCount();
                     List<SecondAtom.Atom> atoms = new ArrayList<>();

                     for (int i = 0; i < mobCount_; i++) {
                        Collections.shuffle(mobs);
                        MapleMonster m = mobs.stream().findAny().orElse(null);
                        if (m != null) {
                           SecondAtomData data = s.getSecondAtomData();
                           if (data != null) {
                              Point pos = new Point(this.getPlayer().getTruePosition());
                              if (i == 0) {
                                 pos.x -= 150;
                              } else if (i == 1) {
                                 pos.x += 150;
                              } else if (i == 2) {
                                 pos.x -= 75;
                                 pos.y -= 150;
                              } else if (i == 3) {
                                 pos.x += 75;
                                 pos.y -= 150;
                              } else if (i == 4) {
                                 pos.x -= 75;
                                 pos.y += 150;
                              } else if (i == 5) {
                                 pos.x += 75;
                                 pos.y += 150;
                              }

                              SecondAtom.Atom a = new SecondAtom.Atom(
                                 this.getPlayer().getMap(),
                                 this.getPlayer().getId(),
                                 400011120,
                                 SecondAtom.SN.getAndAdd(1),
                                 SecondAtom.SecondAtomType.DragonBlazeFlame,
                                 0,
                                 null,
                                 pos
                              );
                              a.setPlayerID(this.getPlayer().getId());
                              a.setTargetObjectID(m.getObjectId());
                              a.setExpire(data.getExpire());
                              a.setAttackableCount(data.getAttackableCount());
                              a.setEnableDelay(data.getEnableDelay());
                              a.setSkillID(400011120);
                              a.setAngle(Randomizer.rand(data.getFirstAngleStart(), data.getFirstAngleRange()));
                              this.getPlayer().addSecondAtom(a);
                              atoms.add(a);
                           }
                        }
                     }

                     SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400011120, atoms);
                     this.getPlayer().getMap().createSecondAtom(secondAtom);
                  }

                  this.lastDragonBlazeFlameTime = System.currentTimeMillis();
               }
            }
         }
      }

      SecondaryStatEffect e = SkillFactory.getSkill(400011068).getEffect(this.getPlayer().getTotalSkillLevel(400011068));
      if (e != null) {
         int w = 0;
         if (attack.skillID == 400011068) {
            w = e.getW();
         }

         if (attack.skillID == 400011069) {
            w = e.getW2();
         }

         int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * w);
         this.getPlayer().addHP(hp, false);
      }

      if (attack.skillID == 61121221 || attack.skillID == 61121225) {
         this.getPlayer().send(CField.userBonusAttackRequest(61121116, true, Collections.EMPTY_LIST));
      }

      if (attack.skillID == 61121124 || attack.skillID == 61121104) {
         this.getPlayer().send(CField.userBonusAttackRequest(61121223, true, Collections.EMPTY_LIST));
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      int slv = 0;
      if ((slv = this.getPlayer().getTotalSkillLevel(61110006)) > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(61110006).getEffect(slv);
         if (eff != null && this.getPlayer().isAlive() && this.getPlayer().checkInterval(this.lastKaiserSelfRecoveryTime, eff.getW() * 1000)) {
            int x = eff.getX();
            int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * x);
            int mp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * x);
            this.getPlayer().addMPHP(hp, mp);
            this.lastKaiserSelfRecoveryTime = System.currentTimeMillis();
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.StopForceAtomInfo) != null
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.StopForceAtomInfo) == 0) {
         SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.StopForceAtomInfo);
         if (effect != null && this.getPlayer().checkInterval(this.lastWillOfSwordCharge, effect.getCoolTime())) {
            this.getPlayer()
               .temporaryStatSet(SecondaryStatFlag.StopForceAtomInfo, effect.getSourceId(), Integer.MAX_VALUE, effect.getLevel(), effect.getMobCount());
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DevilishPower) != null) {
         int rand = Randomizer.rand(1, 6);
         List<SecondAtom.Atom> atoms = new ArrayList<>();
         SecondaryStatEffect e = SkillFactory.getSkill(400011119).getEffect(this.getPlayer().getTotalSkillLevel(400011118));
         if (e != null) {
            for (int i = 0; i < rand; i++) {
               SecondAtom.Atom a = new SecondAtom.Atom(
                  this.getPlayer().getMap(),
                  this.getPlayer().getId(),
                  400011119,
                  SecondAtom.SN.getAndAdd(1),
                  SecondAtom.SecondAtomType.RedEnergy,
                  0,
                  null,
                  this.getPlayer().getTruePosition()
               );
               a.setPlayerID(this.getPlayer().getId());
               a.setExpire(e.getDuration());
               a.setAttackableCount(-1);
               a.setSkillID(400011119);
               a.setUnk2(1);
               this.getPlayer().addSecondAtom(a);
               atoms.add(a);
            }
         }

         if (atoms.size() > 0) {
            SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400011119, atoms);
            this.getPlayer().getMap().createSecondAtom(secondAtom);
         }
      }
   }

   public void setSmashStack(int skillID) {
      if (GameConstants.isKaiser(this.getPlayer().getJob())) {
         SecondaryStatEffect sld = this.getPlayer().getSkillLevelData(60000219);
         if (sld != null) {
            SecondaryStat ss = this.getPlayer().getSecondaryStat();
            if (ss != null) {
               if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Morph) == null) {
                  int max = sld.getS();
                  int x0 = sld.getS();
                  int x1 = sld.getU();
                  int x2 = sld.getV();
                  if (this.getPlayer().getJob() == 6110) {
                     max = sld.getU();
                  }

                  if (this.getPlayer().getJob() >= 6111) {
                     max = sld.getV();
                  }

                  this.getPlayer().smashStack = (short)Math.min(max, this.getPlayer().smashStack + GameConstants.getKaiserStackIncrement(skillID));
                  if (this.getPlayer().smashStack < x0) {
                     this.SmashStackX = 0;
                  } else if (this.getPlayer().smashStack >= x0 && this.getPlayer().smashStack < x1) {
                     this.SmashStackX = 1;
                  } else if (this.getPlayer().smashStack >= x1) {
                     this.SmashStackX = 2;
                  }

                  this.getPlayer().temporaryStatSet(61111008, Integer.MAX_VALUE, SecondaryStatFlag.SmashStack, this.getPlayer().smashStack);
               }
            }
         }
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case SmashStack:
            packet.writeInt(this.SmashStackX);
            packet.writeInt(this.SmashStackC);
            packet.writeInt(this.SmashStackA);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
