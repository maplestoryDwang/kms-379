package objects.users.jobs.resistance;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.fields.ForceAtom;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Thief;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_DoubleArray;
import objects.users.skills.TeleportAttackData_DoubleArray_Elem;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Xenon extends Thief {
   private long xenonSurplusTime = 0L;
   private long virtualProjectionHP = 0L;
   private Point fusionPos = null;
   private boolean fusionIsFlip = false;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (opcode != RecvPacketOpcode.SHOOT_ATTACK && attack.skillID == 36121053) {
         SecondaryStatEffect eff = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);
         if (eff != null) {
            List<MobTemporaryStatFlag> cancel_ = new ArrayList<>();
            cancel_.add(MobTemporaryStatFlag.P_GUARD_UP);
            cancel_.add(MobTemporaryStatFlag.M_GUARD_UP);
            cancel_.add(MobTemporaryStatFlag.POWER_UP);
            cancel_.add(MobTemporaryStatFlag.MAGIC_UP);
            cancel_.add(MobTemporaryStatFlag.HARD_SKIN);

            for (AttackPair ap : attack.allDamage) {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
               if (mob != null) {
                  if (!mob.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
                     this.getPlayer()
                        .send(
                           MobPacket.monsterResist(
                              mob,
                              this.getPlayer(),
                              (int)((mob.getResistSkill(MobTemporaryStatFlag.FREEZE) - System.currentTimeMillis()) / 1000L),
                              attack.skillID
                           )
                        );
                  } else {
                     for (MobTemporaryStatFlag stat : cancel_) {
                        mob.cancelStatus(stat);
                     }

                     Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                     mse.put(MobTemporaryStatFlag.FREEZE, new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false));
                     mse.put(MobTemporaryStatFlag.MAGIC_CRASH, new MobTemporaryStatEffect(MobTemporaryStatFlag.MAGIC_CRASH, 1, attack.skillID, null, false));
                     mob.applyMonsterBuff(mse, attack.skillID, eff.getDuration(), null, Collections.EMPTY_LIST);
                     mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), attack.skillID);
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
      if (attack.skillID != 36110004 && attack.skillID != 36001005 && attack.skillID != 36110005) {
         this.applyTriangulationOnMob(monster);
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 36121052) {
         this.getPlayer().temporaryStatSet(36121052, 3500, SecondaryStatFlag.indiePartialNotDamaged, 1);
         SecondaryStatEffect eff = SkillFactory.getSkill(36121055).getEffect(attack.skillLevel);
         if (eff != null) {
            this.getPlayer().temporaryStatSet(36121055, eff.getDuration(), SecondaryStatFlag.indieDamR, eff.getIndieDamR());
         }
      }

      if (attack.targets > 0 && attack.skillID == 36121052) {
         for (AttackPair pair : attack.allDamage) {
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(pair.objectid);
            if (mob != null && mob.isAlive()) {
               Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new HashMap<>();
               stats.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, -effect.getX(), attack.skillID, null, false));
               stats.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, -effect.getX(), attack.skillID, null, false));
               mob.applyMonsterBuff(stats, attack.skillID, effect.getDuration(), null, Collections.EMPTY_LIST);
            }
         }
      }

      if (attack.targets > 0) {
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.PinPointRocket) != null
            && attack.skillID != 36110004
            && attack.skillID != 36001005
            && this.getPlayer().getCooldownLimit(36001005) == 0L) {
            SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.PinPointRocket);
            if (eff != null && eff.makeChanceResult()) {
               Point basePoint = this.getPlayer().getPosition();
               List<MapleMonster> mobs_ = this.getPlayer()
                  .getMap()
                  .getMobsInRect(
                     basePoint, basePoint.x - eff.getRange(), basePoint.y + eff.getRange(), basePoint.x + eff.getRange(), basePoint.y - eff.getRange()
                  );
               List<Integer> mobList = new LinkedList<>();
               int i = 0;

               for (MapleMonster mo : mobs_) {
                  mobList.add(mo.getObjectId());
                  if (++i >= eff.getBulletCount()) {
                     break;
                  }
               }

               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initPinPointRocket();
               ForceAtom forceAtom = new ForceAtom(
                  info,
                  36001005,
                  this.getPlayer().getId(),
                  false,
                  true,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.PIN_POINT_ROCKET,
                  mobList,
                  eff.getBulletCount()
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               this.getPlayer().send(CField.skillCooldown(36001005, eff.getX() * 1000));
               this.getPlayer().addCooldown(36001005, System.currentTimeMillis(), eff.getX() * 1000L);
            }
         }

         if (this.getPlayer().hasBuffBySkillID(36141503) && this.getPlayer().getRemainCooltime(36141501) <= 0L) {
            List<Integer> list = SkillFactory.getSkill(36141501).getSkillList();
            if (list.contains(attack.skillID)) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 36141500);
               this.getPlayer().send(CField.skillCooldown(36141501, 1800));
               this.getPlayer().addCooldown(36141501, System.currentTimeMillis(), 1800L);
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 400041044) {
         this.fusionPos = packet.readPos();
         this.fusionIsFlip = packet.readByte() == 1;
         packet.skip(2);
         if (this.getPlayer().getSummonByMovementType(SummonMoveAbility.STATIONARY) != null) {
            for (Summoned summon : this.getPlayer().getSummons()) {
               if (summon.getSkill() == 36121002 || summon.getSkill() == 36121013 || summon.getSkill() == 36121014) {
                  SecondaryStatEffect notFusionEffect = SkillFactory.getSkill(summon.getSkill()).getEffect(summon.getSkillLevel());
                  Rect notFusionRect = new Rect(summon.getPosition(), notFusionEffect.getLt(), notFusionEffect.getRb(), summon.isFacingLeft());
                  SecondaryStatEffect fusionEffect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
                  Rect fusionRect = new Rect(this.fusionPos, fusionEffect.getLt(), fusionEffect.getRb(), this.fusionIsFlip);
                  if (Rect.Intersect(fusionRect, notFusionRect) != null) {
                     this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                     this.getPlayer().getMap().removeMapObject(summon);
                     this.getPlayer().removeVisibleMapObject(summon);
                     this.getPlayer().removeSummon(summon);
                     this.getPlayer().temporaryStatResetBySkillID(summon.getSkill());
                  }
               }
            }
         }
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      if (this.getActiveSkillID() != 36111006 || this.getPlayer().getBuffedValue(SecondaryStatFlag.ShadowPartner) == null) {
         switch (this.getActiveSkillID()) {
            case 36111008:
               if (this.getPlayer().getSkillLevel(36111008) > 0) {
                  this.getPlayer().gainXenonSurplus((short)10);
               }

               this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
               break;
            case 36121007: {
               Point pos = packet.readPos();
               byte facingLeft = packet.readByte();
               Rectangle rect = effect.calculateBoundingBox(pos, facingLeft == 1);
               AffectedArea area = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration());
               area.setStartTime(System.currentTimeMillis());
               this.getPlayer().getMap().spawnMist(area);
               PacketEncoder p = new PacketEncoder();
               p.writeShort(SendPacketOpcode.SIT_ON_TIME_CAPSULE.getValue());
               this.getPlayer().send(p.getPacket());
               this.getPlayer().getMap().broadcastMessage(CField.showChair(this.getPlayer(), 3010587, null, this.getPlayer().getMesoChairCount()));
               this.getPlayer().setChair(3010587);
               effect.applyTo(this.getPlayer(), true);
               break;
            }
            case 36121054:
               this.getPlayer().gainXenonSurplus((short)20);
               effect.applyTo(this.getPlayer(), true);
               break;
            case 36141503:
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.ArtificialEvolution, this.getActiveSkillID(), effect.getDuration(), 1);
               break;
            case 400001017: {
               Point pos = packet.readPos();
               int option = packet.readInt();
               int flip = packet.readByte();
               Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), flip == 1);
               AffectedArea area = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration());
               area.setRlType(flip);
               this.getPlayer().getMap().spawnMist(area);
               effect.applyTo(this.getPlayer(), true);
               break;
            }
            case 400041044:
               effect.applyTo(this.getPlayer(), this.fusionPos, (byte)(this.fusionIsFlip ? 1 : 0), this.exclusive);
            case 36111006:
               this.virtualProjectionHP = this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 10L;
            default:
               super.onActiveSkill(skill, effect, packet);
               break;
            case 400041058:
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.PhotonRay);
               List<SecondAtom.Atom> atoms = new ArrayList<>();
               Skill s = SkillFactory.getSkill(400041058);

               for (TeleportAttackElement e : this.teleportAttackAction.actions) {
                  TeleportAttackData_DoubleArray data = (TeleportAttackData_DoubleArray)e.data;
                  if (data != null) {
                     for (TeleportAttackData_DoubleArray_Elem d : data.data) {
                        int objectID = d.data1;
                        int bulletCount = d.data2;

                        for (int i = 0; i < bulletCount; i++) {
                           SecondAtom.Atom a = new SecondAtom.Atom(
                              this.getPlayer().getMap(),
                              this.getPlayer().getId(),
                              400041058,
                              SecondAtom.SN.getAndAdd(1),
                              SecondAtom.SecondAtomType.PhotonRay,
                              0,
                              null,
                              this.getPlayer().getTruePosition()
                           );
                           SecondAtomData dd = skill.getSecondAtomData();
                           a.setPlayerID(this.getPlayer().getId());
                           a.setTargetObjectID(objectID);
                           a.setExpire(dd.getExpire());
                           a.setAttackableCount(dd.getAttackableCount());
                           a.setEnableDelay(dd.getEnableDelay());
                           a.setCreateDelay(150 + i * 30);
                           a.setSkillID(400041058);
                           List<SecondAtom.Custom> l = dd.getCustoms();
                           a.setCustoms(l);
                           a.setAngle(l.get(atoms.size()).getValue());
                           this.getPlayer().addSecondAtom(a);
                           atoms.add(a);
                        }
                     }
                  }
               }

               if (atoms.size() > 0) {
                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400041058, atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
               }

               effect.applyTo(this.getPlayer());
               this.getPlayer().setPhotonRayCharge(0);
         }
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      this.updateXenonSurplus();
      this.updateOverloadMode();
   }

   public void skillUpdatePerTick(int skillID) {
      if (skillID == 400041031) {
         this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getTruePosition(), this.getPlayer().isFacingLeft(), skillID, 1);
      }
   }

   public void updateXenonSurplus() {
      if (this.xenonSurplusTime == 0L || System.currentTimeMillis() - this.xenonSurplusTime >= this.getXenonSurplusCycle()) {
         if (this.getPlayer().getMaxSupply() > this.getPlayer().getXenonSurplus()) {
            this.getPlayer().gainXenonSurplus((short)1);
         }

         this.xenonSurplusTime = System.currentTimeMillis();
      }
   }

   public boolean isActiveOverloadMode() {
      return this.getPlayer().getBuffedValue(SecondaryStatFlag.OverloadMode) != null;
   }

   public void updateOverloadMode() {
      if (this.isActiveOverloadMode()) {
         int skillID = 400041029;
         if (this.getPlayer().getTotalSkillLevel(500061059) > 0) {
            skillID = 500061059;
         }

         SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillID);
         if (effect != null) {
            int consumeMP = (int)(this.getPlayer().getStat().getMaxMp() * (effect.getQ() * 0.01)) + effect.getY();
            if (this.getPlayer().getStat().getMp() < consumeMP) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.OverloadMode);
            } else {
               this.getPlayer().addMP(-consumeMP);
               if (Randomizer.nextInt(100) < 10) {
                  this.skillUpdatePerTick(400041031);
               }
            }
         }
      }
   }

   public void applyTriangulationOnMob(MapleMonster mob) {
      int skillID = 36110005;
      int skillLevel = this.getPlayer().getTotalSkillLevel(skillID);
      if (skillLevel != 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillLevel);
         if (effect != null) {
            int value = 1;
            MobTemporaryStatEffect eff = mob.getBuff(MobTemporaryStatFlag.EXPLOSION);
            if (eff != null) {
               value = eff.getX() + 1;
               if (value >= 5) {
                  return;
               }
            } else {
               eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.EXPLOSION, value, skillID, null, false);
            }

            if (effect.makeChanceResult() || value >= 4) {
               eff.setX(value);
               Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new HashMap<>();
               stats.put(MobTemporaryStatFlag.EXPLOSION, eff);
               stats.put(MobTemporaryStatFlag.EVA, new MobTemporaryStatEffect(MobTemporaryStatFlag.EVA, effect.getX() * -value, skillID, null, false));
               stats.put(MobTemporaryStatFlag.BLIND, new MobTemporaryStatEffect(MobTemporaryStatFlag.BLIND, effect.getX() * value, skillID, null, false));
               mob.applyMonsterBuff(stats, skillID, effect.getDuration(), null, Collections.EMPTY_LIST);
            }
         }
      }
   }

   public int getXenonSurplusCycle() {
      return this.getPlayer().getBuffedValue(SecondaryStatFlag.OverloadMode) != null ? 2000 : 4000;
   }
}
