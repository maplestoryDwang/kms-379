package objects.users.jobs.koc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.effect.child.SkillEffect;
import objects.fields.SecondAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Warrior;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackAction;
import objects.users.skills.TeleportAttackData;
import objects.users.skills.TeleportAttackData_Unk;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class SoulMaster extends Warrior {
   private long lastWillOfStillTime = 0L;
   private int elementSoulStack = 0;
   private int lastAttackSkillID = 0;
   private int elisionCount = 0;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 11141500 && !this.getPlayer().hasBuffBySkillID(SecondaryStatFlag.indiePMDR, 11141500)) {
         int duration = effect.getW2() * 1000;
         int value = effect.getQ2();
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePMDR, 11141500, duration, value);
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
      if (totalDamage > 0L) {
         if (attack.skillID == 11121004) {
            SecondaryStatEffect eff = SkillFactory.getSkill(11121004).getEffect(this.getPlayer().getTotalSkillLevel(11121004));
            if (eff != null && eff.makeChanceResult()) {
               if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
                  Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                  mse.put(MobTemporaryStatFlag.FREEZE, new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false));
                  MobTemporaryStatEffect mobEffect = new MobTemporaryStatEffect(MobTemporaryStatFlag.SOUL_EXPLOSTION, eff.getX(), attack.skillID, null, false);
                  mobEffect.setW(this.getPlayer().getId());
                  mse.put(MobTemporaryStatFlag.SOUL_EXPLOSTION, mobEffect);
                  monster.applyMonsterBuff(mse, attack.skillID, eff.getDuration(), null, Collections.EMPTY_LIST);
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
         }

         try {
            if (this.isSunSkill(skill.getId()) || this.isMoonSkill(skill.getId())) {
               if ((this.lastAttackSkillID == 0 || this.isSunSkill(this.lastAttackSkillID)) && this.isMoonSkill(skill.getId())) {
                  this.handleElementSoul();
               }

               if ((this.lastAttackSkillID == 0 || this.isMoonSkill(this.lastAttackSkillID)) && this.isSunSkill(skill.getId())) {
                  this.handleElementSoul();
               }

               this.lastAttackSkillID = skill.getId();
            }
         } catch (NullPointerException var13) {
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 11121157 || attack.skillID == 11121257) {
         SecondaryStatEffect level = this.getPlayer().getSkillLevelData(11121257);
         if (level != null) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 11121157, (int)(level.getT() * 1000.0), 1);
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.CosmikOrb) != null && this.getPlayer().getOneInfoQuestInteger(1544, "11121018") > 0) {
         Skill cosmikBurst = SkillFactory.getSkill(11121018);
         if (cosmikBurst != null && cosmikBurst.getSkillList2().contains(attack.skillID) && this.getPlayer().getRemainCooltime(11121018) == 0L) {
            int orbCount = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.CosmikOrb, 0);
            int maxStack = 0;
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(11001022);
            SecondaryStatEffect level2 = this.getPlayer().getSkillLevelData(11100034);
            SecondaryStatEffect level3 = this.getPlayer().getSkillLevelData(11110031);
            SecondaryStatEffect level4 = this.getPlayer().getSkillLevelData(11120019);
            if (level != null) {
               maxStack = level.getX();
               if (level2 != null) {
                  maxStack += level2.getX();
               }

               if (level3 != null) {
                  maxStack += level3.getX();
               }

               if (level4 != null) {
                  maxStack += level4.getX();
               }
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.CosmikForge) != null) {
               SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.CosmikForge);
               if (e != null) {
                  maxStack += e.getX() != 0 ? e.getX() : 5;
               }
            }

            if (maxStack <= orbCount) {
               SecondaryStatEffect e = cosmikBurst.getEffect(this.getPlayer().getTotalSkillLevel(11121018));
               if (e != null) {
                  List<MapleMonster> mobList = this.getPlayer()
                     .getMap()
                     .getMobsInRect(this.getPlayer().getTruePosition(), e.getLt().x, e.getLt().y, e.getRb().x, e.getRb().y, (attack.display & 32768) != 0);
                  List<SecondAtom.Atom> atoms = new ArrayList<>();

                  for (int next = 0; next < orbCount; next++) {
                     SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(),
                        this.getPlayer().getId(),
                        11121018,
                        SecondAtom.SN.getAndAdd(1),
                        SecondAtom.SecondAtomType.CosmikBurst,
                        next,
                        null,
                        0
                     );
                     SecondAtomData.atom atom = cosmikBurst.getSecondAtomData().getAtoms().get(next);
                     a.setPlayerID(this.getPlayer().getId());
                     int target = 0;
                     if (!mobList.isEmpty()) {
                        if (mobList.size() > next) {
                           target = mobList.get(next).getObjectId();
                        } else {
                           target = mobList.get(Randomizer.rand(0, mobList.size() - 1)).getObjectId();
                        }
                     }

                     a.setUnk4(1);
                     a.setTargetObjectID(target);
                     a.setCreateDelay(atom.getCreateDelay());
                     a.setEnableDelay(atom.getEnableDelay());
                     a.setSkillID(11121018);
                     a.setExpire(atom.getExpire());
                     a.setIndex(next);
                     a.setAttackableCount(1);
                     int posX = this.getPlayer().getTruePosition().x + atom.getPos().x;
                     int posY = this.getPlayer().getTruePosition().y + atom.getPos().y;
                     a.setPos(new Point(posX, posY));
                     atoms.add(a);
                  }

                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 11121018, atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
                  this.getPlayer().temporaryStatReset(SecondaryStatFlag.CosmikOrb);
                  this.elisionCount = 0;
               }
            }
         }
      }

      if (this.getPlayer().getJob() == 1112) {
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.PoseType) != null && this.getPlayer().getCooldownLimit(400011048) <= 0L && attack.targets > 0) {
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400011048);
            if (eff != null) {
               if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ShadowServant) != null) {
                  this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), this.getPlayer().isFacingLeft(), 400011049);
               } else {
                  this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), this.getPlayer().isFacingLeft(), 400011048);
               }

               SkillEffect e = new SkillEffect(
                  this.getPlayer().getId(), this.getPlayer().getLevel(), 400011048, this.getPlayer().getTotalSkillLevel(400011048), null
               );
               this.getPlayer().send(e.encodeForLocal());
               this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
               this.getPlayer().send(CField.skillCooldown(400011048, eff.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(400011048, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
            }
         }

         if (this.getPlayer().getJob() >= 1000 && this.getPlayer().getJob() <= 1112 && attack.bySummonedID > 0) {
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400011005);
            if (eff != null && this.getPlayer().getLastDanceTime() + eff.getS2() * 1000 <= System.currentTimeMillis()) {
               this.getPlayer().send(CField.userBonusAttackRequest(400011022, true, Collections.EMPTY_LIST));
               this.getPlayer().send(CField.userBonusAttackRequest(400011023, true, Collections.EMPTY_LIST));
               this.getPlayer().setLastDanceTime(System.currentTimeMillis());
            }

            SecondaryStatEffect eff2 = this.getPlayer().getSkillLevelData(400011048);
            if (eff2 != null) {
               this.getPlayer().changeCooldown(400011048, -((int)(eff2.getT() * 3000.0)));
            }
         }
      }

      if (attack.skillID == 400011056) {
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ElementSoul) != null && this.getPlayer().getBuffedValue(SecondaryStatFlag.Ellision) != null) {
            SecondaryStatEffect levelx = this.getPlayer().getSkillLevelData(11001022);
            if (levelx != null) {
               this.elisionCount++;
               int maxStackx = 0;
               SecondaryStatEffect level2x = this.getPlayer().getSkillLevelData(11100034);
               SecondaryStatEffect level3x = this.getPlayer().getSkillLevelData(11110031);
               SecondaryStatEffect level4x = this.getPlayer().getSkillLevelData(11120019);
               maxStackx = levelx.getX();
               if (level2x != null) {
                  maxStackx += level2x.getX();
               }

               if (level3x != null) {
                  maxStackx += level3x.getX();
               }

               if (level4x != null) {
                  maxStackx += level4x.getX();
               }

               SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.CosmikForge);
               if (this.getPlayer().getBuffedValue(SecondaryStatFlag.CosmikForge) != null && e != null) {
                  maxStackx += e.getX() != 0 ? e.getX() : 5;
               }

               if (e != null && this.elisionCount % 5 == 0 && this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.CosmikOrb, 0) != maxStackx) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.CosmikOrb, 11001030, e.getDuration(), maxStackx);
                  this.elisionCount = 0;
               }
            }
         }

         Summoned summoned = null;
         if ((summoned = this.getPlayer().getSummonBySkillID(400011065)) != null) {
            SecondaryStatEffect elys = SkillFactory.getSkill(400011065).getEffect(attack.skillLevel);
            if (elys != null) {
               SecondaryStatEffect styx = this.getPlayer().getSkillLevelData(400011056);
               if (styx != null) {
                  Rect crackRect = new Rect(summoned.getTruePosition(), elys.getLt2(), elys.getRb2(), summoned.getSummonRLType() == 1);
                  Rect attackRect = new Rect(this.getPlayer().getTruePosition(), styx.getLt(), styx.getRb(), (attack.display & 32768) != 0);
                  Rect r = Rect.Intersect(crackRect, attackRect);
                  if (r != null) {
                     TeleportAttackAction teleportAttackAction = attack.teleportAttackAction;
                     if (teleportAttackAction != null) {
                        for (TeleportAttackElement element : teleportAttackAction.actions) {
                           TeleportAttackData data = element.data;
                           if (data != null && data instanceof TeleportAttackData_Unk) {
                              TeleportAttackData_Unk unk = (TeleportAttackData_Unk)element.data;
                              if (unk.arrowCount == 1) {
                                 this.getPlayer().getMap().broadcastMessage(CField.summonCrystalToggleSkill(this.getPlayer(), summoned, 1));
                                 this.getPlayer().giveCoolDowns(400011065, System.currentTimeMillis(), elys.getCooldown(this.getPlayer()));
                                 this.getPlayer().send(CField.skillCooldown(400011065, elys.getCooldown(this.getPlayer())));
                                 SecondaryStatEffect eff2 = this.getPlayer().getSkillLevelData(400011048);
                                 if (eff2 != null) {
                                    this.getPlayer().changeCooldown(400011048, -((int)(eff2.getT() * 1500.0)));
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else if (this.getPlayer().getCooldownLimit(400011065) == 0L) {
            SecondaryStatEffect ex = SkillFactory.getSkill(400011065).getEffect(attack.skillLevel);
            if (ex != null) {
               Summoned summon = new Summoned(
                  this.getPlayer(),
                  400011065,
                  attack.skillLevel,
                  this.getPlayer().getMap().calcDropPos(this.getPlayer().getPosition(), this.getPlayer().getTruePosition()),
                  SummonMoveAbility.STATIONARY,
                  (byte)((attack.display & 32768) > 0 ? 1 : 0),
                  ex.getDuration()
               );
               this.getPlayer().getMap().spawnSummon(summon, ex.getDuration());
               this.getPlayer().addSummon(summon);
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.GlimmeringTime) != null
         && attack.bySummonedID == 0
         && skill.getId() != 11121257
         && skill.getId() != 11121157
         && (this.isMoonSkill(skill.getId()) || this.isSunSkill(skill.getId()))) {
         int posType = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.PoseType, 0);
         if (posType == 1) {
            this.handlePoseType(11001025, this.getPlayer().getTotalSkillLevel(11001025));
         } else if (posType == 2) {
            this.handlePoseType(11001024, this.getPlayer().getTotalSkillLevel(11001024));
         }
      }

      if (attack.skillID == 11121018) {
         this.getPlayer().changeCooldown(11121018, -1000L);
      }
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 11121052) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(11121052);
         if (eff != null) {
            eff.applyTo(this.getPlayer());
            this.getPlayer().temporaryStatSet(11121052, Integer.MAX_VALUE, SecondaryStatFlag.indiePartialNotDamaged, 1);
            this.getPlayer().giveCoolDowns(11121052, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
            this.getPlayer().send(CField.skillCooldown(11121052, eff.getCooldown(this.getPlayer())));
         }
      }

      if ((this.getActiveSkillID() == 11121055 || this.getActiveSkillPrepareID() == 11121056)
         && this.getPlayer().getBuffedEffect(SecondaryStatFlag.indiePartialNotDamaged, 11121052) != null) {
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.indiePartialNotDamaged, 11121052);
      }

      if (this.getActiveSkillPrepareID() == 400011056) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(11121052);
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 11121052 || this.getActiveSkillID() == 11121055 || this.getActiveSkillID() == 11121056) {
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.indiePartialNotDamaged, 11121052);
      }

      if (this.getActiveSkillID() == 11001025) {
         if (this.getPlayer().hasBuffBySkillID(11101031)) {
            this.getPlayer().temporaryStatResetBySkillID(11101031);
         }

         if (this.getPlayer().hasBuffBySkillID(11101033)) {
            this.getPlayer().temporaryStatResetBySkillID(11101033);
         }
      }

      if (this.getActiveSkillID() == 11001024) {
         if (this.getPlayer().hasBuffBySkillID(11101031)) {
            this.getPlayer().temporaryStatResetBySkillID(11101031);
         }

         if (this.getPlayer().hasBuffBySkillID(11101032)) {
            this.getPlayer().temporaryStatResetBySkillID(11101032);
         }
      }

      super.activeSkillCancel();
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 11101022:
         case 11111022:
            int attackCount = packet.readInt();
            this.getPlayer().getSecondaryStat().setAttackCount(attackCount);
            break;
         case 11121054:
            this.getPlayer().send(CField.skillCooldown(11121054, 180000));
            this.getPlayer().addCooldown(11121054, System.currentTimeMillis(), 180000L);
            SecondaryStatEffect effect = Objects.requireNonNull(SkillFactory.getSkill(11121054)).getEffect(1);
            effect.setDuration(60000);
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 11001022:
            if (this.getPlayer().hasBuffBySkillID(11001022)) {
               this.getPlayer().temporaryStatResetBySkillID(11001022);
               return;
            }

            this.getPlayer().temporaryStatSet(SecondaryStatFlag.ElementSoul, effect.getSourceId(), Integer.MAX_VALUE, effect.getLevel(), 10);
            break;
         case 11001024:
         case 11001025:
         case 11101031:
            this.handlePoseType(this.getActiveSkillID(), this.getActiveSkillLevel());
            break;
         case 11111023:
            packet.skip(4);
            byte mobSize = packet.readByte();
            List<MapleMonster> mobs = new ArrayList<>();

            for (int i = 0; i < mobSize; i++) {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(packet.readInt());
               if (mob != null) {
                  mobs.add(mob);
               }
            }

            SecondaryStatEffect eff = SkillFactory.getSkill(11111023).getEffect(this.getPlayer().getTotalSkillLevel(11111023));
            if (eff != null) {
               if (eff.makeChanceResult()) {
                  int xx = eff.getX();
                  int durationx = eff.getDuration();
                  SecondaryStatEffect drHyper = SkillFactory.getSkill(11120045).getEffect(this.getPlayer().getTotalSkillLevel(11120045));
                  if (drHyper != null) {
                     xx += drHyper.getX();
                  }

                  SecondaryStatEffect timeHyper = SkillFactory.getSkill(11120043).getEffect(this.getPlayer().getTotalSkillLevel(11120043));
                  if (timeHyper != null) {
                     durationx += timeHyper.getDuration();
                  }

                  Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                  mse.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, xx, 11111023, null, false));
                  mse.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, xx, 11111023, null, false));
                  mse.put(
                     MobTemporaryStatFlag.ADD_DAM_SKILL_2, new MobTemporaryStatEffect(MobTemporaryStatFlag.ADD_DAM_SKILL_2, eff.getS(), 11111023, null, false)
                  );
                  SecondaryStatEffect elemHyper = SkillFactory.getSkill(11120044).getEffect(this.getPlayer().getTotalSkillLevel(11120044));
                  if (elemHyper != null) {
                     MobTemporaryStatEffect mobEffect = new MobTemporaryStatEffect(MobTemporaryStatFlag.TRUE_SIGHT, 1, 11111023, null, false);
                     if (this.getPlayer().getParty() != null) {
                        mobEffect.setW(1);
                        mobEffect.setP(this.getPlayer().getParty().getId());
                     } else {
                        mobEffect.setW(0);
                        mobEffect.setP(0);
                     }

                     mobEffect.setC(this.getPlayer().getId());
                     mobEffect.setU(elemHyper.getY());
                     mse.put(MobTemporaryStatFlag.TRUE_SIGHT, mobEffect);
                  }

                  for (MapleMonster mob : mobs) {
                     mob.applyMonsterBuff(mse, 11111023, durationx, null, Collections.EMPTY_LIST);
                  }
               }

               eff.applyTo(this.getPlayer(), true);
            }
            break;
         case 11111029: {
            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            int duration = effect.getDuration();
            int u = effect.getU();
            int orb = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.CosmikOrb, 0);
            if (orb == 0) {
               return;
            }

            Summoned toRemove = null;

            for (Summoned summon : this.getPlayer().getMap().getAllSummonsThreadsafe()) {
               if (summon != null && summon.getSkill() == 11111029 && summon.getOwnerId() == this.getPlayer().getId()) {
                  toRemove = summon;
               }
            }

            if (toRemove != null) {
               this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(toRemove, true));
               this.getPlayer().getMap().removeMapObject(toRemove);
               this.getPlayer().removeVisibleMapObject(toRemove);
               this.getPlayer().removeSummon(toRemove);
            }

            duration += orb * u * 1000;
            Summoned s = new Summoned(
               this.getPlayer(),
               11111029,
               this.getActiveSkillLevel(),
               new Point(x, y),
               SummonMoveAbility.STATIONARY,
               isLeft,
               System.currentTimeMillis() + duration
            );
            this.getPlayer().getMap().spawnSummon(s, duration);
            this.getPlayer().addSummon(s);
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.CosmikOrb);
            break;
         }
         case 11121018:
            byte targetSize = packet.readByte();
            List<Integer> targets = new ArrayList<>();

            for (int i = 0; i < targetSize; i++) {
               targets.add(packet.readInt());
            }

            packet.skip(3);
            int xx = packet.readInt();
            int yx = packet.readInt();
            boolean facingLeft = packet.readByte() == 1;
            int orbCount = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.CosmikOrb, 0);
            if (orbCount == 0) {
               return;
            }

            List<SecondAtom.Atom> atoms = new ArrayList<>();

            for (int next = 0; next < orbCount; next++) {
               SecondAtom.Atom a = new SecondAtom.Atom(
                  this.getPlayer().getMap(),
                  this.getPlayer().getId(),
                  11121018,
                  SecondAtom.SN.getAndAdd(1),
                  SecondAtom.SecondAtomType.CosmikBurst,
                  next,
                  null,
                  0
               );
               SecondAtomData.atom atom = skill.getSecondAtomData().getAtoms().get(next);
               a.setPlayerID(this.getPlayer().getId());
               int target = 0;
               if (!targets.isEmpty()) {
                  if (targetSize > next) {
                     target = targets.get(next);
                  } else {
                     target = targets.get(Randomizer.rand(0, targetSize - 1));
                  }
               }

               a.setUnk4(1);
               a.setTargetObjectID(target);
               a.setCreateDelay(atom.getCreateDelay());
               a.setEnableDelay(atom.getEnableDelay());
               a.setSkillID(this.getActiveSkillID());
               a.setExpire(atom.getExpire());
               a.setIndex(next);
               a.setAttackableCount(1);
               int posX = xx + atom.getPos().x;
               int posY = yx + atom.getPos().y;
               a.setPos(new Point(posX, posY));
               atoms.add(a);
            }

            SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 11121018, atoms);
            this.getPlayer().getMap().createSecondAtom(secondAtom);
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.CosmikOrb);
            this.getPlayer().changeCooldown(11121018, -(orbCount * 1000));
            this.elisionCount = 0;
            break;
         case 11121054:
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(effect.getIndieDamR() != 0 ? effect.getIndieDamR() : 10));
            statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, 0);
            statups.put(SecondaryStatFlag.CosmikForge, 1);
            this.getPlayer()
               .temporaryStatSet(
                  this.getActiveSkillID(),
                  this.getActiveSkillLevel(),
                  effect.getDuration(effect.getDuration(), this.getPlayer()),
                  statups,
                  false,
                  0,
                  true,
                  false
               );
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ElementSoul) != null) {
               SecondaryStatEffect level = this.getPlayer().getSkillLevelData(11001022);
               if (level != null) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.CosmikOrb, 11001030, effect.getDuration(), 10);
               }
            }
            break;
         case 400011055:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.Ellision, 400011055, effect.getDuration(), 1);
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ElementSoul) != null) {
               SecondaryStatEffect level = this.getPlayer().getSkillLevelData(11001022);
               if (level != null) {
                  int maxStack = 0;
                  SecondaryStatEffect level2 = this.getPlayer().getSkillLevelData(11100034);
                  SecondaryStatEffect level3 = this.getPlayer().getSkillLevelData(11110031);
                  SecondaryStatEffect level4 = this.getPlayer().getSkillLevelData(11120019);
                  maxStack = level.getX();
                  if (level2 != null) {
                     maxStack += level2.getX();
                  }

                  if (level3 != null) {
                     maxStack += level3.getX();
                  }

                  if (level4 != null) {
                     maxStack += level4.getX();
                  }

                  SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.CosmikForge);
                  if (this.getPlayer().getBuffedValue(SecondaryStatFlag.CosmikForge) != null && e != null) {
                     maxStack += e.getX() != 0 ? e.getX() : 5;
                  }

                  if (e != null && this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.CosmikOrb, 0) != maxStack) {
                     this.getPlayer().temporaryStatSet(SecondaryStatFlag.CosmikOrb, 11001030, e.getDuration(), maxStack);
                  }
               }
            }
            break;
         case 400011088: {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 400011089, 3500, 1);
            Summoned summon = new Summoned(
               this.getPlayer(),
               400011088,
               this.getActiveSkillLevel(),
               this.getPlayer().getMap().calcDropPos(this.getPlayer().getPosition(), this.getPlayer().getTruePosition()),
               SummonMoveAbility.SHADOW_SERVANT_EXTEND,
               (byte)0,
               effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(summon, effect.getDuration());
            this.getPlayer().addSummon(summon);
            break;
         }
         case 400011089:
            this.getPlayer().temporaryStatResetBySkillID(400011088);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 400011089, 5000, 1);
            Skill eclipse = SkillFactory.getSkill(400011088);
            if (eclipse == null) {
               return;
            }

            if (eclipse.getExtraSkillInfo() == null || eclipse.getExtraSkillInfo().isEmpty()) {
               return;
            }

            List<ExtraSkillInfo> extraSkills = new ArrayList<>(eclipse.getExtraSkillInfo());
            List<ExtraSkillInfo> newExtraSkills = new ArrayList<>();
            ExtraSkillInfo extra = extraSkills.get(0);

            for (int ix = 0; ix < 5; ix++) {
               newExtraSkills.add(extra);
            }

            eclipse.setExtraSkillInfo(newExtraSkills);
            this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), this.getPlayer().isFacingLeft(), 400011088);
            break;
         case 400011142:
            int cosmikOrb = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.CosmikOrb, 0);
            if (cosmikOrb == 0) {
               cosmikOrb = 1;
            }

            this.getPlayer().temporaryStatReset(SecondaryStatFlag.CosmikOrb);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.Cosmos, 400011142, effect.getDuration() / 1000, cosmikOrb);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   public void removeSummon(Summoned s) {
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getJob() >= 1100 && this.getPlayer().getJob() <= 1112) {
         this.handleWillOfStill();
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case PoseType:
            int pose = 0;

            try {
               pose = this.getPlayer().getOneInfoQuestInteger(1544, "11001025");
            } catch (Exception var5) {
            }

            packet.write(pose);
            packet.write(this.getPlayer().getBuffedValue(SecondaryStatFlag.GlimmeringTime) != null);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public void handleWillOfStill() {
      SecondaryStatEffect effect = SkillFactory.getSkill(11110025).getEffect(this.getPlayer().getTotalSkillLevel(11110025));
      if (effect != null
         && this.getPlayer().isAlive()
         && (this.lastWillOfStillTime == 0L || System.currentTimeMillis() - this.lastWillOfStillTime >= effect.getW() * 1000)) {
         int y = effect.getY();
         int delta = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01) * y;
         this.getPlayer().addHP(delta, false);
         this.lastWillOfStillTime = System.currentTimeMillis();
      }
   }

   public void handlePoseType(int skillID, int skillLevel) {
      if (skillID == 11101031) {
         int posType = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.PoseType, 0);
         if (posType == 0) {
            return;
         }

         Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
         this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), false));
         statups.put(SecondaryStatFlag.GlimmeringTime, 1);
         this.getPlayer().temporaryStatSet(11101031, skillLevel, Integer.MAX_VALUE, statups, false, 0, true, false);
         if (posType == 1) {
            this.doRisingSun(11101033, this.getPlayer().getTotalSkillLevel(11101031), true);
         } else {
            this.doFallingMoon(11101032, this.getPlayer().getTotalSkillLevel(11101031), true);
         }
      } else if (skillID == 11001025) {
         if (this.getPlayer().hasBuffBySkillID(11001024)) {
            this.getPlayer().temporaryStatResetBySkillID(11001024, true);
         }

         if (this.getPlayer().hasBuffBySkillID(11101033)) {
            this.getPlayer().temporaryStatResetBySkillID(11101033, true);
         }

         Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
         this.getPlayer().temporaryStatSet(11101033, skillLevel, Integer.MAX_VALUE, statups, false, 0, true, false);
         this.doRisingSun(skillID, skillLevel, false);
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.GlimmeringTime) != null) {
            this.doFallingMoon(11101032, this.getPlayer().getTotalSkillLevel(11101031), true);
         }
      } else if (skillID == 11001024) {
         if (this.getPlayer().hasBuffBySkillID(11001025)) {
            this.getPlayer().temporaryStatResetBySkillID(11001025, true);
         }

         if (this.getPlayer().hasBuffBySkillID(11101032)) {
            this.getPlayer().temporaryStatResetBySkillID(11101032, true);
         }

         this.doFallingMoon(skillID, skillLevel, false);
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.GlimmeringTime) != null) {
            this.doRisingSun(11101033, this.getPlayer().getTotalSkillLevel(11101031), true);
         }
      }

      this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), false));
      SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400011048);
      if (eff != null) {
         this.getPlayer().changeCooldown(400011048, (int)(-(eff.getT() * 1000.0)));
      }
   }

   public void doRisingSun(int skillID, int skillLevel, boolean glimmeringTime) {
      SecondaryStatEffect effect = SkillFactory.getSkill(11001025).getEffect(this.getPlayer().getTotalSkillLevel(11001025));
      this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), false));
      if (effect != null) {
         Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
         int indieBooster = effect.getIndieBooster();
         int indiePMDR = effect.getIndiePMdR();
         int enhanceLevel = 0;
         if ((enhanceLevel = this.getPlayer().getTotalSkillLevel(11120009)) > 0) {
            SecondaryStatEffect enhance = SkillFactory.getSkill(11120010).getEffect(enhanceLevel);
            if (enhance != null) {
               indieBooster = enhance.getIndieBooster();
               indiePMDR = enhance.getIndiePMdR();
            }
         } else if ((enhanceLevel = this.getPlayer().getTotalSkillLevel(11110032)) > 0) {
            SecondaryStatEffect enhance = SkillFactory.getSkill(11110033).getEffect(enhanceLevel);
            if (enhance != null) {
               indieBooster = enhance.getIndieBooster();
               indiePMDR = enhance.getIndiePMdR();
            }
         } else if ((enhanceLevel = this.getPlayer().getTotalSkillLevel(11101031)) > 0) {
            SecondaryStatEffect enhance = SkillFactory.getSkill(11101033).getEffect(enhanceLevel);
            if (enhance != null) {
               indieBooster = enhance.getIndieBooster();
               indiePMDR = enhance.getIndiePMdR();
            }
         }

         statups.put(SecondaryStatFlag.indieBooster, indieBooster);
         statups.put(SecondaryStatFlag.indiePMDR, indiePMDR);
         if (!glimmeringTime) {
            statups.put(SecondaryStatFlag.PoseType, 2);
         }

         this.getPlayer().temporaryStatSet(skillID, skillLevel, Integer.MAX_VALUE, statups, false, 0, false, false);
      }
   }

   public void doFallingMoon(int skillID, int skillLevel, boolean glimmeringTime) {
      SecondaryStatEffect effect = SkillFactory.getSkill(11001024).getEffect(this.getPlayer().getTotalSkillLevel(11001024));
      this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), false));
      Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
      int indieCR = effect.getIndieCr();
      int enhanceLevel = 0;
      if ((enhanceLevel = this.getPlayer().getTotalSkillLevel(11120009)) > 0) {
         SecondaryStatEffect enhance = SkillFactory.getSkill(11120009).getEffect(enhanceLevel);
         if (enhance != null) {
            indieCR = enhance.getIndieCr();
         }
      } else if ((enhanceLevel = this.getPlayer().getTotalSkillLevel(11110032)) > 0) {
         SecondaryStatEffect enhance = SkillFactory.getSkill(11110032).getEffect(enhanceLevel);
         if (enhance != null) {
            indieCR = enhance.getIndieCr();
         }
      } else if ((enhanceLevel = this.getPlayer().getTotalSkillLevel(11101031)) > 0) {
         SecondaryStatEffect enhance = SkillFactory.getSkill(11101032).getEffect(enhanceLevel);
         if (enhance != null) {
            indieCR = enhance.getIndieCr();
         }
      }

      statups.put(SecondaryStatFlag.BuckShot, 10);
      statups.put(SecondaryStatFlag.indieCR, indieCR);
      if (!glimmeringTime) {
         statups.put(SecondaryStatFlag.PoseType, 1);
      }

      this.getPlayer().temporaryStatSet(skillID, skillLevel, Integer.MAX_VALUE, statups, false, 0, false, false);
   }

   public void handleElementSoul() {
      int maxStack = 0;
      int y = 0;
      int z = 0;
      SecondaryStatEffect level = this.getPlayer().getSkillLevelData(11001022);
      SecondaryStatEffect level2 = this.getPlayer().getSkillLevelData(11100034);
      SecondaryStatEffect level3 = this.getPlayer().getSkillLevelData(11110031);
      SecondaryStatEffect level4 = this.getPlayer().getSkillLevelData(11120019);
      if (level != null) {
         maxStack = level.getX();
         y = level.getY();
         if (level2 != null) {
            maxStack += level2.getX();
            y += level2.getY();
         }

         if (level3 != null) {
            maxStack += level3.getX();
            y += level3.getY();
         }

         if (level4 != null) {
            maxStack += level4.getX();
            z = level4.getZ();
            y += level4.getY();
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.CosmikForge) != null) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.CosmikForge);
         if (e != null) {
            maxStack += e.getX() != 0 ? e.getX() : 5;
         }
      }

      SecondaryStatEffect cosmikOrb = this.getPlayer().getSkillLevelData(11001030);
      if (cosmikOrb != null) {
         SecondaryStatEffect level5 = this.getPlayer().getSkillLevelData(11001027);
         if (level5 != null) {
            int stack = Math.min(maxStack, this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.CosmikOrb, 0) + 1);
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.CosmikOrb, stack);
            this.getPlayer().temporaryStatSet(11001030, level.getLevel(), cosmikOrb.getDuration(), statups, false, 0, false, false);
            statups.clear();
            statups.put(SecondaryStatFlag.indiePAD, y);
            statups.put(SecondaryStatFlag.indiePMDR, z);
            this.getPlayer()
               .temporaryStatSet(11001027, level.getLevel(), level5.getDuration(level5.getDuration(), this.getPlayer()), statups, false, 0, false, false);
         }
      }
   }

   public void setPoseType() {
   }

   public int getElementSoulStack() {
      return this.elementSoulStack;
   }

   public void setElementSoulStack(int elementSoulStack) {
      this.elementSoulStack = elementSoulStack;
   }

   public boolean isSunSkill(int skillID) {
      switch (skillID) {
         case 11001226:
         case 11100228:
         case 11110228:
         case 11111230:
         case 11120217:
         case 11121257:
         case 11141200:
            return true;
         default:
            return false;
      }
   }

   public boolean isMoonSkill(int skillID) {
      switch (skillID) {
         case 11001126:
         case 11100128:
         case 11110128:
         case 11111130:
         case 11120117:
         case 11121157:
         case 11141100:
            return true;
         default:
            return false;
      }
   }

   public boolean isCrossTheSticks(int skillID) {
      switch (skillID) {
         case 11121052:
         case 11121055:
         case 11121056:
            return true;
         case 11121053:
         case 11121054:
         default:
            return false;
      }
   }
}
