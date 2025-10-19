package objects.users.jobs.zero;

import constants.GameConstants;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.MobPacket;
import objects.effect.child.HPHeal;
import objects.fields.SecondAtom;
import objects.fields.Wreckage;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCoolDownValueHolder;
import objects.users.jobs.Warrior;
import objects.users.looks.zero.ZeroInfo;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Zero extends Warrior {
   private long lastZeroRecoveryTime = 0L;
   private long lastZeroTimeFastBuff = 0L;
   private Map<Integer, Long> limitedBreakAttack = new HashMap<>();
   private boolean limitBreakReverse = false;
   private Map<Integer, Integer> limitedBreakReverse = new HashMap<>();
   private int tagCount = 0;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
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
      if (attack.skillID == Zero.ZeroSkill.AdvancedPowerStump_2.getSkillID()) {
         SecondaryStatEffect eff = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);
         if (eff != null && eff.makeChanceResult()) {
            monster.applyStatus(
               this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, eff.getSourceId(), null, false), false, eff.getDuration(), true, eff
            );
         }
      }

      ZeroInfo zeroInfo = this.getPlayer().getZeroInfo();
      if (zeroInfo.isBeta()) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(101120110);
         if (e != null && e.makeChanceResult() && monster.getBuff(MobTemporaryStatFlag.FREEZE) == null) {
            if (monster.checkResistSkillByID(101120110)) {
               monster.applyStatus(
                  this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, 101120110, null, false), false, e.getSubTime(), false, e
               );
               monster.addResistSkillBySkillID(101120110, System.currentTimeMillis() + e.getV());
            } else {
               this.getPlayer()
                  .send(
                     MobPacket.monsterResist(
                        monster, this.getPlayer(), (int)((monster.getResistSkillBySkillID(101120110) - System.currentTimeMillis()) / 1000L), 101120110
                     )
                  );
            }
         }
      }

      if (this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.LimitBreak, 0) != 0 && this.limitBreakReverse) {
         Integer time = this.limitedBreakReverse.getOrDefault(monster.getObjectId(), 0);
         this.limitedBreakReverse.put(monster.getObjectId(), time == 0 ? 10000 : Math.max(time + 1000, 20000));
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().hasBuffBySkillID(Zero.ZeroSkill.Transcendent_Light.getSkillID())
         && attack.allDamage.size() > 0
         && (attack.skillID < 400001066 || attack.skillID > 400001090)) {
         this.getPlayer().send(CField.userBonusAttackRequest(400001068, true, Collections.EMPTY_LIST, 0, 0));
      }

      ZeroInfo zeroInfo = this.getPlayer().getZeroInfo();
      if (!zeroInfo.isBeta()) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(101120207);
         if (e != null && e.makeChanceResult()) {
            attack.allDamage
               .stream()
               .forEach(
                  ap -> {
                     MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
                     if (mob != null) {
                        mob.applyStatus(
                           this.getPlayer(),
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, Zero.ZeroSkill.DivineLeer.getSkillID(), null, false),
                           true,
                           e.getDuration(),
                           false,
                           e
                        );
                     }
                  }
               );
            if (attack.targets > 0) {
               int x = e.getX();
               int delta = (int)(x * (this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01));
               this.getPlayer().addHP(delta);
               HPHeal eff = new HPHeal(this.getPlayer().getId(), delta);
               this.getPlayer().send(eff.encodeForLocal());
            }
         }

         if (attack.targets > 0 && this.getPlayer().getSkillLevel(101110205) > 0) {
            SecondaryStatEffect combatRecovery = SkillFactory.getSkill(101110205).getEffect(this.getPlayer().getSkillLevel(101110205));
            if (Randomizer.isSuccess(combatRecovery.getY())) {
               this.getPlayer().send(CField.zeroCombatRecovery(this.getPlayer().getSkillLevel(101110205), (int)this.getPlayer().getStat().getMp()));
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.EgoWeapon) == null && this.getPlayer().getCooldownLimit(400011134) <= 0L) {
            SecondaryStatEffect e2 = SkillFactory.getSkill(400011134).getEffect(this.getPlayer().getTotalSkillLevel(400011134));
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.EgoWeapon, 400011134, 2000, 1);
         }
      } else {
         SecondaryStatEffect ex = this.getPlayer().getSkillLevelData(101110103);
         if (ex != null && ex.makeChanceResult()) {
            attack.allDamage
               .forEach(
                  ap -> {
                     MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
                     if (mob != null) {
                        MobTemporaryStatEffect stack = mob.getBuff(MobTemporaryStatFlag.MULTI_PMDR);
                        int s = mob.getMultiPMDRC();
                        s = Math.min(ex.getX(), s + 1);
                        mob.setMultiPMDRC(s);
                        mob.applyStatus(
                           this.getPlayer(),
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.MULTI_PMDR, s * ex.getY(), Zero.ZeroSkill.ArmorSplit.getSkillID(), null, false),
                           false,
                           ex.getDuration(),
                           false,
                           ex
                        );
                     }
                  }
               );
         }

         if (this.getPlayer().getCooldownLimit(400011135) <= 0L && attack.skillID != 400011135) {
            SecondaryStatEffect e2 = SkillFactory.getSkill(400011135).getEffect(this.getPlayer().getTotalSkillLevel(400011134));
            if (e2 != null) {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.EGO_WEAPON_REQUEST.getValue());
               packet.writeInt(400011135);
               packet.writeInt(this.getPlayer().getTruePosition().x);
               packet.writeInt(this.getPlayer().getTruePosition().y);
               this.getPlayer().send(packet.getPacket());
               this.getPlayer().send(CField.skillCooldown(400011135, e2.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(400011135, System.currentTimeMillis(), e2.getCooldown(this.getPlayer()));
            }
         }
      }

      if (attack.skillID != Zero.ZeroSkill.LimitBreak.getSkillID() && attack.skillID != 500061062) {
         if (attack.skillID == Zero.ZeroSkill.ShadowFlash.getSkillID() || attack.skillID == Zero.ZeroSkill.ShadowFlash_Beta.getSkillID()) {
            Point pos = attack.forcedPos;
            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), false);
            SecondaryStatEffect exx = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);
            if (exx != null) {
               AffectedArea aa = new AffectedArea(rect, this.getPlayer(), exx, pos, System.currentTimeMillis() + exx.getCooldown(this.getPlayer()));
               this.getPlayer().getMap().spawnMist(aa);
            }
         }
      } else {
         SecondaryStatEffect exx = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);
         int skillID = attack.skillID == 500061062 ? 500061064 : 400011025;
         if (!this.getPlayer().hasBuffBySkillID(skillID)) {
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indieBooster, exx.getIndieBooster());
            statups.put(SecondaryStatFlag.indieCooltimeReduce, Integer.valueOf(exx.getIndieCooltimeReduce()));
            statups.put(SecondaryStatFlag.indiePMDR, exx.getIndiePMdR());
            this.getPlayer().temporaryStatSet(attack.skillID, attack.skillLevel, exx.getDuration(), statups);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.LimitBreak, skillID, exx.getDuration(), 1);
            int lock = this.getPlayer().getOneInfoQuestInteger(1544, "400011015");
            this.limitBreakReverse = lock == 1;
            this.limitedBreakReverse.clear();
         }

         if (exx != null) {
            attack.allDamage
               .forEach(
                  ap -> {
                     MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
                     if (mob != null) {
                        if (this.limitBreakReverse) {
                           int time = this.limitedBreakReverse.getOrDefault(mob.getObjectId(), 0);
                           this.limitedBreakReverse.put(mob.getObjectId(), time == 0 ? 10000 : Math.max(time + 1000, 20000));
                        } else if (mob.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
                           SecondaryStatEffect e2 = SkillFactory.getSkill(skillID - 1).getEffect(attack.skillLevel);
                           mob.applyStatus(
                              this.getPlayer(),
                              new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, skillID - 1, null, false),
                              false,
                              e2.getDuration(),
                              false,
                              e2
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
               );
            this.limitedBreakAttack.clear();
         }
      }

      if (this.getPlayer().hasBuffBySkillID(Zero.ZeroSkill.LimitBreak.getSkillID())) {
         attack.allDamage.stream().forEach(ap -> {
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
            if (mob != null) {
               ap.attack.forEach(d -> {
                  long value = 0L;
                  if (this.limitedBreakAttack.containsKey(mob.getObjectId())) {
                     value = this.limitedBreakAttack.get(mob.getObjectId());
                  }

                  this.limitedBreakAttack.put(mob.getObjectId(), value + d.left);
               });
            }
         });
      }

      if (!attack.tag && attack.skillID != 101120206) {
         this.tryApplyZeroTimeFastBuff(GameConstants.LinkedZeroSkil(attack.skillID), attack);
      }

      if (attack.skillID == 101000101) {
         this.getPlayer().send(CField.finalAttackRequest(true, 101000102, 101000101, 56, Collections.emptyList()));
      }

      if (attack.skillID != 101141012 && attack.skillID != 101141013) {
         if (this.getPlayer().getTotalSkillLevel(101141012) > 0 && this.getPlayer().getWreckageCount() > 0) {
            List<Wreckage> wreckages = this.getPlayer()
               .getMap()
               .getWreckageInRect(this.getPlayer().getPosition(), -500, -500, 500, 500, this.getPlayer().getId());
            if (this.getPlayer().getWreckageCount() != this.getPlayer().getMap().getWrekageCount(this.getPlayer().getId())) {
               this.getPlayer().setWreckageCount(this.getPlayer().getMap().getWrekageCount(this.getPlayer().getId()));
            }

            if (wreckages.size() > 0) {
               int activeAtomSkillID = 101141013;
               if (this.getPlayer().getZeroInfo().isBeta()) {
                  activeAtomSkillID = 101141012;
               }

               List<Wreckage> toRemove = new ArrayList<>();

               for (Wreckage wreckage : wreckages) {
                  if (wreckage.getSkillID() == activeAtomSkillID) {
                     toRemove.add(wreckage);
                  }
               }

               List<SecondAtom.Atom> atoms = new ArrayList<>();

               for (Wreckage wreckagex : toRemove) {
                  int xpos = wreckagex.getPosition().x;
                  int ypos = wreckagex.getPosition().y;
                  Point pos = new Point(xpos, ypos);
                  SecondAtom.Atom a = new SecondAtom.Atom(
                     this.getPlayer().getMap(),
                     this.getPlayer().getId(),
                     activeAtomSkillID,
                     SecondAtom.SN.getAndAdd(1),
                     activeAtomSkillID == 101141012 ? SecondAtom.SecondAtomType.ZeroTimePieceAlpha : SecondAtom.SecondAtomType.ZeroTimePieceBeta,
                     0,
                     null,
                     pos
                  );
                  SecondAtomData dd = SkillFactory.getSkill(101141012).getSecondAtomData();
                  a.setPlayerID(this.getPlayer().getId());
                  a.setExpire(5000);
                  a.setAttackableCount(1);
                  a.setEnableDelay(dd.getEnableDelay());
                  a.setCreateDelay(dd.getCreateDelay());
                  a.setSkillID(activeAtomSkillID);
                  int atomangle = -130 + ThreadLocalRandom.current().nextInt(260);
                  a.setAngle(atomangle);
                  this.getPlayer().addSecondAtom(a);
                  atoms.add(a);
                  this.getPlayer().getMap().broadcastMessage(CField.DelWreckage(wreckagex.getOwner().getId(), Collections.singletonList(wreckagex), false));
                  wreckagex.removeWreckage(this.getPlayer().getMap(), false);
               }

               if (atoms.size() > 0) {
                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), activeAtomSkillID, atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
               }
            }
         }

         if (attack.tag && attack.targets > 0) {
            this.tagCount++;
            int atomSkillID = 101141012;
            if (this.getPlayer().getZeroInfo().isBeta()) {
               atomSkillID = 101141013;
            }

            if (this.getPlayer().getRemainCooltime(atomSkillID) <= 0L) {
               if (this.tagCount++ % 3 == 0) {
                  this.tagCount = 0;
               }

               SecondaryStatEffect eff = SkillFactory.getSkill(101141012).getEffect(this.getPlayer().getTotalSkillLevel(101141012));

               for (int i = 0; i < eff.getV(); i++) {
                  if (this.getPlayer().getWreckageCount() < eff.getX()) {
                     Point pos = this.getPlayer().getPosition();
                     pos.x = pos.x + (ThreadLocalRandom.current().nextInt(500) - 250);
                     pos.y = pos.y + (ThreadLocalRandom.current().nextInt(300) - 350);
                     this.getPlayer()
                        .getMap()
                        .spawnWreckage(new Wreckage(this.getPlayer(), eff.getDuration() / 1000, atomSkillID, this.getPlayer().incAndGetWreckageCount(), pos));
                  }
               }

               this.getPlayer().addCooldown(atomSkillID, System.currentTimeMillis(), 2000L);
               this.getPlayer().send(CField.skillCooldown(atomSkillID, 2000));
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
      switch (Zero.ZeroSkill.get(this.getActiveSkillID())) {
         case ThrowingWeapon:
         case AdvancedThrowingWeapon: {
            SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            if (effect == null) {
               return;
            }

            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            Summoned s = new Summoned(
               this.getPlayer(),
               this.getActiveSkillID(),
               this.getActiveSkillLevel(),
               new Point(x, y),
               SummonMoveAbility.FIX_V_MOVE,
               isLeft,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(s, effect.getDuration());
            this.getPlayer().addSummon(s);
            if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.indieSummon) != null) {
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeTill(SecondaryStatFlag.indieSummon, effect.getSourceId(), 1000);
            } else {
               effect.applyTo(this.getPlayer());
            }

            this.tryApplyZeroTimeFastBuff(this.getActiveSkillID(), null);
            break;
         }
         case TimeDistortion: {
            Point pos = new Point(packet.readShort(), packet.readShort());
            packet.skip(4);
            AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(Zero.ZeroSkill.TimeDistortion.getSkillID(), this.getPlayer().getId());
            if (area != null) {
               this.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), Zero.ZeroSkill.TimeDistortion.getSkillID(), false));
               this.getPlayer().getMap().removeMapObject(area);
            }

            SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            if (effect != null) {
               Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), false);
               AffectedArea aa = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration());
               this.getPlayer().getMap().spawnMist(aa);
               effect.applyTo(this.getPlayer(), true);
            }
            break;
         }
         case TimeHolding: {
            new ArrayList<>(this.getPlayer().getCooldowns()).forEach(cooltime -> {
               Skill skill = SkillFactory.getSkill(cooltime.skillId);
               if (skill != null && GameConstants.isResettableCooltimeSkill(cooltime.skillId) && !skill.isHyper()) {
                  this.getPlayer().clearCooldown(cooltime.skillId);
               }
            });
            SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            if (effect != null) {
               Map<SecondaryStatFlag, Integer> statupsx = new HashMap<>();
               statupsx.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
               statupsx.put(SecondaryStatFlag.indieBlockSkill, 1);
               this.getPlayer().temporaryStatSet(Zero.ZeroSkill.TimeHolding.getSkillID(), this.getActiveSkillLevel(), effect.getDuration(), statupsx);
               if (this.getPlayer().getLevel() >= 200) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieDamR, 100001281, effect.getX() * 1000, effect.getY());
               }
            }
            break;
         }
         case TimeRewind: {
            SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            if (effect != null) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.PreReviveOnce, this.getActiveSkillID(), Integer.MAX_VALUE, effect.getX());
            }
            break;
         }
         case IntensiveTime: {
            packet.skip(4);
            packet.skip(4);
            SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            if (effect != null) {
               Map<SecondaryStatFlag, Integer> statupsx = new HashMap<>();
               statupsx.put(SecondaryStatFlag.indieMadR, effect.getX());
               statupsx.put(SecondaryStatFlag.indiePadR, effect.getX());
               this.getPlayer().temporaryStatSet(this.getActiveSkillID(), this.getActiveSkillLevel(), effect.getDuration(), statupsx);
            }
            break;
         }
         case LimitBreak:
            byte count = packet.readByte();

            for (int ix = 0; ix < count; ix++) {
               int mobObjectID = packet.readInt();
               SecondaryStatEffect effectx = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
               if (effectx != null) {
                  MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(mobObjectID);
                  if (mob != null && mob.getBuff(MobTemporaryStatFlag.SPEED) == null) {
                     if (this.limitBreakReverse) {
                        this.limitedBreakReverse.put(mob.getObjectId(), 10000);
                     } else {
                        mob.applyStatus(
                           this.getPlayer(),
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effectx.getY(), Zero.ZeroSkill.LimitBreak.getSkillID(), null, false),
                           false,
                           effectx.getZ() * 1000,
                           false,
                           effectx
                        );
                     }
                  }
               }
            }
            break;
         case JointAttack: {
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            statups.put(SecondaryStatFlag.indieJointAttack, 1);
            this.getPlayer().temporaryStatSet(this.getActiveSkillID(), this.getActiveSkillLevel(), Integer.MAX_VALUE, statups);
            break;
         }
         case EgoWeapon_Beta:
            count = (byte)packet.readInt();

            for (int i = 0; i < count; i++) {
               Point posx = new Point(packet.readInt(), packet.readInt());
               SecondaryStatEffect effectx = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
               if (effectx != null) {
                  Rect rect = new Rect(posx, effectx.getLt(), effectx.getRb(), false);
                  AffectedArea aa = new AffectedArea(rect, this.getPlayer(), effectx, posx, System.currentTimeMillis() + 2000L);
                  this.getPlayer().getMap().spawnMist(aa);
                  effectx.applyTo(this.getPlayer(), true);
               }
            }
            break;
         case TranscendentRhinnesPrayer:
            this.getPlayer().getCooldowns().stream().collect(Collectors.toList()).forEach(cooltime -> {
               Skill skill = SkillFactory.getSkill(cooltime.skillId);
               if (skill != null && GameConstants.isResettableCooltimeSkill(cooltime.skillId) && !skill.isHyper()) {
                  this.getPlayer().clearCooldown(cooltime.skillId);
               }
            });
            break;
         case Transcendent: {
            SecondaryStatEffect eff = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            int lightTime = eff.getTargetPlus();
            int lifeTime = eff.getDOTTime();
            int timeTime = eff.getU2();
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.Transcendents, 7);
            statups.put(SecondaryStatFlag.indieFlyAcc, 1);
            this.getPlayer().temporaryStatSet(Zero.ZeroSkill.Transcendent_Light.skillID, this.getActiveSkillLevel(), lightTime * 1000, statups);
            statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indieSummon, 1);
            statups.put(SecondaryStatFlag.indiePMDR, 5);
            statups.put(SecondaryStatFlag.indieAntiMagicShell, 3);
            this.getPlayer().temporaryStatSet(Zero.ZeroSkill.Transcendent_Time.skillID, this.getActiveSkillLevel(), timeTime * 1000, statups);
            Map<SecondaryStatFlag, Integer> var36 = new HashMap();
            var36.put(SecondaryStatFlag.indieSummon, 1);
            this.getPlayer().temporaryStatSet(Zero.ZeroSkill.Transcendent_Life.skillID, this.getActiveSkillLevel(), lifeTime * 1000, var36);
            break;
         }
         case Transcendent_Time: {
            Point position = new Point(packet.readShort(), packet.readShort() - 1);
            byte isLeft = packet.readByte();
            SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            Summoned summonAlpha = new Summoned(
               this.getPlayer(),
               400001071,
               this.getActiveSkillLevel(),
               position,
               SummonMoveAbility.TRANSCENDENT_TIME,
               isLeft,
               System.currentTimeMillis() + effect.getDuration(),
               false
            );
            this.getPlayer().getMap().spawnSummon(summonAlpha, effect.getDuration());
            this.getPlayer().addSummon(summonAlpha);
            Summoned summonBeta = new Summoned(
               this.getPlayer(),
               400001071,
               this.getActiveSkillLevel(),
               position,
               SummonMoveAbility.TRANSCENDENT_TIME,
               isLeft,
               System.currentTimeMillis() + effect.getDuration(),
               true
            );
            this.getPlayer().getMap().spawnSummon(summonBeta, effect.getDuration());
            this.getPlayer().addSummon(summonBeta);
         }
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void activeSkillCancel() {
      switch (this.getActiveSkillID()) {
         case 400011015:
         case 500061062:
            if (this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.LimitBreak, 0) != 0) {
               this.getPlayer()
                  .send(CField.userBonusAttackRequest(this.getActiveSkillID() == 400011015 ? 400011025 : 500061064, true, Collections.emptyList()));
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.LimitBreak, this.getActiveSkillID(), 0);
               statManager.temporaryStatSet();
               if (this.limitBreakReverse) {
                  SecondaryStatEffect effect = SkillFactory.getSkill(Zero.ZeroSkill.LimitBreak.getSkillID())
                     .getEffect(this.getPlayer().getTotalSkillLevel(Zero.ZeroSkill.LimitBreak.getSkillID()));

                  for (Entry<Integer, Integer> entry : this.limitedBreakReverse.entrySet()) {
                     int objId = entry.getKey();
                     MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(objId);
                     if (mob != null) {
                        mob.applyStatus(
                           this.getPlayer(),
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getY(), Zero.ZeroSkill.LimitBreak.getSkillID(), null, false),
                           false,
                           effect.getZ() * 1000,
                           false,
                           effect
                        );
                     }
                  }
               }
            }
            break;
         case 400011039:
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.indieJointAttack);
            this.getPlayer().send(CField.finalAttackRequest(false, 400011064, 0, 0, Collections.emptyList()));
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.lastZeroRecoveryTime == 0L || System.currentTimeMillis() - this.lastZeroRecoveryTime >= 4000L) {
         ZeroInfo zeroInfo = this.getPlayer().getZeroInfo();
         if (zeroInfo != null) {
            int maxHP = this.getPlayer().getZeroInfo().getCalcSubMHP();
            int recovery = (int)(maxHP * 0.16);
            int subHP = zeroInfo.getSubHP();
            subHP = Math.min(maxHP, subHP + recovery);
            if (subHP != zeroInfo.getSubHP()) {
               zeroInfo.setSubHP(subHP);
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.ZERO_INFO_SUB_HP.getValue());
               packet.write(zeroInfo.isBeta());
               packet.writeInt(subHP);
               this.getPlayer().send(packet.getPacket());
            }
         }

         this.lastZeroRecoveryTime = System.currentTimeMillis();
      }
   }

   public void tryApplyZeroTimeFastBuff(int skillID, AttackInfo attack) {
      SecondaryStatEffect timeFast = this.getPlayer().getSkillLevelData(Zero.ZeroSkill.TimeFast.getSkillID());
      if (timeFast != null) {
         if (this.lastZeroTimeFastBuff == 0L || System.currentTimeMillis() - this.lastZeroTimeFastBuff >= timeFast.getCooltimeMS()) {
            List<Integer> skills = new ArrayList<>();
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.SKILL_COOLTIME_REDUCE.getValue());
            packet.writeInt(timeFast.getX());
            this.getPlayer()
               .getCooldowns()
               .stream()
               .collect(Collectors.toList())
               .forEach(
                  cooltime -> {
                     if (cooltime.skillId != skillID
                        && GameConstants.getSkillRootFromSkill(cooltime.skillId) != 10000
                        && GameConstants.isResettableCooltimeSkill(cooltime.skillId)) {
                        this.reduceSkillCooltimeMS(cooltime.skillId, -timeFast.getX());
                        skills.add(cooltime.skillId);
                     }
                  }
               );
            packet.write(skills.size());

            for (Integer skill : skills) {
               packet.writeInt(skill);
            }

            this.getPlayer().send(packet.getPacket());
            Skill timeFastASkill = SkillFactory.getSkill(Zero.ZeroSkill.TimeFastA.getSkillID());
            Skill timeFastBSkill = SkillFactory.getSkill(Zero.ZeroSkill.TimeFastB.getSkillID());
            SecondaryStatEffect timeFastAEffect = timeFastASkill.getEffect(1);
            SecondaryStatEffect timeFastBEffect = timeFastBSkill.getEffect(1);
            boolean isBeta = this.getPlayer().getZeroInfo().isBeta();
            if (isBeta) {
               int timeFastBValue = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.TimeFastBBuff, 0);
               timeFastBValue = Math.min(timeFastBSkill.getMaxLevel(), timeFastBValue + 1);
               this.getPlayer()
                  .temporaryStatSet(
                     SecondaryStatFlag.TimeFastBBuff,
                     Zero.ZeroSkill.TimeFastB.getSkillID(),
                     timeFastBEffect.getDuration(timeFastBEffect.getDuration(), this.getPlayer()),
                     timeFastBValue
                  );
            } else {
               int timeFastAValue = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.TimeFastABuff, 0);
               timeFastAValue = Math.min(timeFastASkill.getMaxLevel(), timeFastAValue + 1);
               this.getPlayer()
                  .temporaryStatSet(
                     SecondaryStatFlag.TimeFastABuff,
                     Zero.ZeroSkill.TimeFastA.getSkillID(),
                     timeFastAEffect.getDuration(timeFastAEffect.getDuration(), this.getPlayer()),
                     timeFastAValue
                  );
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.EmpressBless) != null) {
               this.getPlayer().getSecondaryStat().EmpressBlessX = 400001056;
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.EmpressBless, 400001045, this.getPlayer().getBuffedValue(SecondaryStatFlag.EmpressBless));
               statManager.temporaryStatSet();
            }

            this.lastZeroTimeFastBuff = System.currentTimeMillis();
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.EmpressBless) != null && skillID != 400001056 && attack != null) {
               int startValue = 0;
               List<Pair<Integer, Integer>> bonusAttack = new ArrayList<>();

               for (AttackPair ap : attack.allDamage) {
                  MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
                  if (mob != null) {
                     bonusAttack.add(new Pair<>(ap.objectid, Math.min(280, startValue)));
                     startValue += 70;
                  }
               }

               if (bonusAttack.size() > 0) {
                  this.getPlayer().send(CField.userBonusAttackRequest(400001056, false, bonusAttack));
               }
            }
         }
      }
   }

   public Map<Integer, Long> getLimitedBreakAttack() {
      return this.limitedBreakAttack;
   }

   public void reduceSkillCooltimeMS(int skillID, int ms) {
      if (this.getPlayer().skillisCooling(skillID)) {
         for (MapleCoolDownValueHolder mcdvh : this.getPlayer().getCooldowns()) {
            if (mcdvh.skillId == skillID) {
               long startTime = mcdvh.startTime;
               long length = mcdvh.length;
               long delta = System.currentTimeMillis() - startTime;
               this.getPlayer().addCooldown(skillID, System.currentTimeMillis(), length - delta + ms);
               break;
            }
         }
      }
   }

   public static enum ZeroSkill {
      DivineForce(100001263),
      DivineSwift(100001264),
      RhinneProtection(100001268),
      TranscendentalWill(100001284),
      AdvancedPowerStump_2(101000102),
      ThrowingWeapon(101100100),
      AdvancedThrowingWeapon(101100101),
      DivineLeer(101120207),
      ArmorSplit(101110103),
      CriticalBind(101120110),
      TimeFast(100000267),
      TimeFastA(100000276),
      TimeFastB(100000277),
      TimeDistortion(100001261),
      TimeRewind(100001272),
      TimeHolding(100001274),
      ShadowRain(100001283),
      IntensiveTime(100001005),
      LimitBreak(400011015),
      JointAttack(400011039),
      ShadowFlash(400011098),
      ShadowFlash_Beta(400011100),
      EgoWeapon(400011134),
      EgoWeapon_Beta(400011135),
      TranscendentRhinnesPrayer(400001045),
      Transcendent(400001066),
      Transcendent_Light(400001067),
      Transcendent_Life(400001070),
      Transcendent_Time(400001071),
      Default_Skill(-1);

      private int skillID;

      private ZeroSkill(int skillID) {
         this.skillID = skillID;
      }

      public int getSkillID() {
         return this.skillID;
      }

      public static Zero.ZeroSkill get(int skillID) {
         if (skillID == 500061062) {
            skillID = 400011015;
         }

         for (Zero.ZeroSkill zeroSkill : values()) {
            if (zeroSkill.getSkillID() == skillID) {
               return zeroSkill;
            }
         }

         return Default_Skill;
      }
   }
}
