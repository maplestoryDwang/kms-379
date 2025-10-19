package objects.users.jobs.koc;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.MobPacket;
import objects.fields.ForceAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.Element;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Thief;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class NightWalker extends Thief {
   long shadowStichStartTime = 0L;
   boolean isFirstRapidThrow = true;
   int rapidThrowCount = 0;
   int shadowMomentumStack = 0;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 14000028) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(14000027);
         if (eff != null) {
            int x = eff.getX();
            int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * x);
            this.getPlayer().healHP(hp);
         }
      }

      if (attack.skillID == 14121052) {
         this.getPlayer().temporaryStatSet(14121052, 2350, SecondaryStatFlag.NotDamaged, 1);
         Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
         statups.put(SecondaryStatFlag.Dominion, 1);
         statups.put(SecondaryStatFlag.indieCR, 100);
         statups.put(SecondaryStatFlag.indiePMDR, effect.getIndiePMdR());
         this.getPlayer().temporaryStatSet(14121052, effect.getLevel(), effect.getDuration(), statups);
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
      if (attack.skillID == 400041059 && this.isFirstRapidThrow) {
         this.isFirstRapidThrow = false;
         this.rapidThrowCount++;
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.DarkSight, 14001023);
         this.getPlayer().temporaryStatSet(14001023, 4000, SecondaryStatFlag.DarkSight, 1);
      } else if (attack.skillID == 400041059 && !this.isFirstRapidThrow && this.rapidThrowCount == 0) {
         this.rapidThrowCount++;
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.DarkSight, 14001023);
         this.getPlayer().temporaryStatSet(14001023, 4000, SecondaryStatFlag.DarkSight, 1);
      }

      if (attack.skillID == 14121004) {
         if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
            int s = effect.getS();
            double t = effect.getT() * 1000.0;
            int delta = (int)(System.currentTimeMillis() - this.shadowStichStartTime);
            delta = (int)(delta / t);
            int duration = effect.getDuration() + Math.min(s, delta) * 1000;
            monster.applyStatus(
               this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, 14121004, null, false), false, duration, false, effect
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

      if (totalDamage > 0L) {
         if (attack.skillID == 14120020) {
            int recovery = (int)(this.getPlayer().getStat().getCurrentMaxHp() * 0.01);
            this.getPlayer().healHP(recovery);
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ElementDarkness) != null) {
            SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ElementDarkness);
            int prob = eff.getProb();
            int maxMultiplier = eff.getX();
            if (this.getPlayer().getSkillLevel(14100026) > 0) {
               SecondaryStatEffect eff2 = SkillFactory.getSkill(14100026).getEffect(this.getPlayer().getTotalSkillLevel(14100026));
               if (eff2 != null) {
                  prob += eff2.getProb();
                  maxMultiplier += eff2.getX();
               }
            }

            if (this.getPlayer().getSkillLevel(14110028) > 0) {
               SecondaryStatEffect eff2 = SkillFactory.getSkill(14110028).getEffect(this.getPlayer().getTotalSkillLevel(14110028));
               if (eff2 != null) {
                  prob += eff2.getProb();
                  maxMultiplier += eff2.getX();
               }
            }

            if (this.getPlayer().getSkillLevel(14120007) > 0) {
               SecondaryStatEffect eff2 = SkillFactory.getSkill(14120007).getEffect(this.getPlayer().getTotalSkillLevel(14120007));
               if (eff2 != null) {
                  prob += eff2.getProb();
                  maxMultiplier += eff2.getX();
               }
            }

            boolean dominian = false;
            if (this.getPlayer().hasBuffBySkillID(14121052)) {
               prob = 100;
               dominian = true;
            }

            if (eff != null && Randomizer.nextInt(100) < prob) {
               int burnedSize = monster.getBurnedSizeBySkillID(eff.getSourceId());
               int b = burnedSize;
               if (dominian && burnedSize < maxMultiplier) {
                  b = maxMultiplier;
               }

               if (b <= maxMultiplier) {
                  for (int i = 0; i < b - burnedSize + 1; i++) {
                     monster.applyStatus(
                        this.getPlayer(),
                        new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, eff.getSourceId(), null, false),
                        true,
                        eff.getDOTTime() * 1000,
                        true,
                        eff,
                        maxMultiplier
                     );
                  }

                  monster.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.ELEMENT_DARKNESS, b, eff.getSourceId(), null, false),
                     false,
                     eff.getDOTTime() * 1000,
                     false,
                     eff
                  );
               }

               this.tryApplySiphonVitality();
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400041037 || attack.skillID == 500061035) {
         int count = 0;
         int bossCount = 0;
         List<Integer> bossObjList = new ArrayList<>();
         ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();

         for (AttackPair oned : attack.allDamage) {
            if (!bossObjList.contains(oned.objectid)) {
               MapleMonster monster = this.getPlayer().getMap().getMonsterByOid(oned.objectid);
               boolean checked = false;
               if (monster != null && monster.getLinkCID() <= 0) {
                  if (monster.getStats().isBoss()) {
                     bossCount++;
                     checked = true;
                  }

                  if (!monster.isAlive()) {
                     count++;
                     checked = true;
                  }
               } else {
                  count++;
                  checked = true;
                  if (oned.isBoss()) {
                     bossCount++;
                  }
               }

               if (checked) {
                  info.initShadowBite(oned.point);
                  ForceAtom forceAtom = new ForceAtom(
                     info,
                     400041037,
                     this.getPlayer().getId(),
                     true,
                     true,
                     oned.objectid,
                     ForceAtom.AtomType.SHADOW_BITE,
                     Collections.singletonList(oned.objectid),
                     1
                  );
                  this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               }

               bossObjList.add(oned.objectid);
            }
         }

         SecondaryStatEffect eff = SkillFactory.getSkill(400041037).getEffect(attack.skillLevel);
         int maxPMDR = 10 + attack.skillLevel / 5;
         int bossPMDR = 8 + attack.skillLevel / 4;
         if (attack.skillID == 500061035 && this.getPlayer().getTotalSkillLevel(500004074) > 0) {
            eff = SkillFactory.getSkill(500061035).getEffect(attack.skillLevel);
            maxPMDR = eff.getQ();
            bossPMDR = eff.getW();
         }

         if (eff != null) {
            this.getPlayer()
               .temporaryStatSet(attack.skillID, eff.getDuration(), SecondaryStatFlag.indiePMDR, Math.min(count * eff.getY() + bossCount * bossPMDR, maxPMDR));
         }
      }

      if (attack.targets > 0) {
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.NightWalkerBat) != null
            && attack.skillID != 14000028
            && attack.skillID != 14000029
            && attack.targets > 0) {
            this.tryShadowBat(attack.skillID, attack);
         }

         if (attack.targets > 0
            && GameConstants.isNightWalker(this.getPlayer().getJob())
            && attack.skillID != 14000028
            && attack.skillID != 14000029
            && attack.skillID != 14121003) {
            Skill s = SkillFactory.getSkill(attack.skillID);
            if (s != null && s.getElement() == Element.Dark && this.getPlayer().getCooldownLimit(14121003) != 0L) {
               SecondaryStatEffect effx = SkillFactory.getSkill(14121003).getEffect(this.getPlayer().getTotalSkillLevel(14121003));
               if (effx != null) {
                  this.getPlayer().changeCooldown(14121003, -effx.getY());
               }
            }
         }

         this.tryShadowSpear(attack.skillID, attack);
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 14121003) {
         this.getPlayer().getDarknessOmenTargets().clear();
         this.getPlayer().setDarknessOmenBatCount(0);
      } else if (this.getActiveSkillPrepareID() == 14121004) {
         this.shadowStichStartTime = System.currentTimeMillis();
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 14111030:
            if (this.getPlayer().hasBuffBySkillID(14111030)) {
               this.getPlayer().temporaryStatResetBySkillID(14111030, true);
               return;
            }

            this.getPlayer().temporaryStatSet(SecondaryStatFlag.ReviveOnce, this.getActiveSkillID(), Integer.MAX_VALUE, effect.getX());
            break;
         case 14121003: {
            if (this.getPlayer().getSummonBySkillID(14121003) != null) {
               Summoned summoned = this.getPlayer().getSummonBySkillID(14121003);
               this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summoned, true));
               this.getPlayer().getMap().removeMapObject(summoned);
               summoned.getOwner().removeVisibleMapObject(summoned);
               this.getPlayer().removeSummon(summoned);
               this.getPlayer().temporaryStatResetBySkillID(14121003);
            }

            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            Summoned s = new Summoned(
               this.getPlayer(),
               14121003,
               this.getActiveSkillLevel(),
               new Point(x, y),
               SummonMoveAbility.STATIONARY,
               isLeft,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(s, effect.getDuration());
            this.getPlayer().addSummon(s);
            effect.applyTo(this.getPlayer());
            break;
         }
         case 14141502: {
            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            Summoned s = new Summoned(
               this.getPlayer(),
               14141502,
               this.getActiveSkillLevel(),
               new Point(x, y),
               SummonMoveAbility.FOLLOW,
               isLeft,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(s, effect.getDuration());
            this.getPlayer().addSummon(s);
            break;
         }
         case 400041060:
            this.getPlayer().temporaryStatSet(14001023, 4000, SecondaryStatFlag.DarkSight, 1);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.ShadowMomentum, 0) != 0) {
         this.shadowMomentumStack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.ShadowMomentum, 0);
      } else {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(14110032);
         if (this.shadowMomentumStack != 0) {
            if (this.shadowMomentumStack == 3) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.ShadowMomentum, 14110032, eff.getDuration(), this.shadowMomentumStack - 1);
            } else if (this.shadowMomentumStack == 2) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.ShadowMomentum, 14110032, eff.getDuration(), this.shadowMomentumStack - 1);
            }

            this.shadowMomentumStack--;
         }
      }

      if (this.getPlayer().hasBuffBySkillID(14121052)) {
         this.tryShadowBat(14121052, null);
         SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ShadowMomentum);
         if (effect == null) {
            effect = SkillFactory.getSkill(14110032).getEffect(this.getPlayer().getSkillLevel(14110032));
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.ShadowMomentum, effect.getSourceId(), effect.getDuration(), effect.getX());
         } else if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ShadowMomentum) == null
            || this.getPlayer().getBuffedValue(SecondaryStatFlag.ShadowMomentum) != effect.getX()) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.ShadowMomentum, effect.getSourceId(), effect.getDuration(), effect.getX());
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DarkSight) == null) {
         this.isFirstRapidThrow = true;
         this.rapidThrowCount = 0;
      } else {
         this.isFirstRapidThrow = false;
      }
   }

   public void tryShadowBat(int skillID, AttackInfo attacks) {
      SecondaryStatEffect eff = SkillFactory.getSkill(14000027).getEffect(this.getPlayer().getTotalSkillLevel(14001027));
      if (eff != null) {
         int batLimit = eff.getY();
         int prob = eff.getProb();
         int[] skills = new int[]{14100027, 14110029, 14120008};
         SecondaryStatEffect batSummon = null;

         for (int skillID_ : skills) {
            if (this.getPlayer().getTotalSkillLevel(skillID_) > 0) {
               SecondaryStatEffect e = SkillFactory.getSkill(skillID_).getEffect(this.getPlayer().getTotalSkillLevel(skillID_));
               if (e != null) {
                  if (skillID_ != 14110029) {
                     prob += e.getProb();
                  }

                  batLimit += e.getY();
                  batSummon = e;
               }
            }
         }

         if (this.getPlayer().hasBuffBySkillID(14121052)) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(14121052);
            if (e != null) {
               batLimit += e.getW();
            }
         }

         List<Summoned> summonList = new ArrayList<>();

         try {
            for (Summoned summon : this.getPlayer().getSummonsReadLock()) {
               if (summon.getSkill() == 14000027
                  || summon.getSkill() == 14100027
                  || summon.getSkill() == 14110029
                  || summon.getSkill() == 14120008
                  || summon.getSkill() == 14110033
                  || summon.getSkill() == 14120017
                  || summon.getSkill() == 14120019
                  || summon.getSkill() == 14141005
                  || summon.getSkill() == 14141008
                  || summon.getSkill() == 14141013
                  || summon.getSkill() == 14141015) {
                  summonList.add(summon);
               }
            }
         } finally {
            this.getPlayer().unlockSummonsReadLock();
         }

         int var25 = summonList.size();
         if (skillID == 14121003) {
            SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(skillID);
            if (effect != null) {
               for (AttackPair pair : attacks.allDamage) {
                  if (!this.getPlayer().getDarknessOmenTargets().contains(pair.objectid)) {
                     this.getPlayer().getDarknessOmenTargets().add(pair.objectid);
                  }

                  if (this.getPlayer().getDarknessOmenTargets().size() >= effect.getX() && this.getPlayer().getDarknessOmenBatCount() < effect.getZ()) {
                     this.getPlayer().summonShadowBat(batSummon, this.getPlayer().getPosition());
                     this.getPlayer().setDarknessOmenBatCount(this.getPlayer().getDarknessOmenBatCount() + 1);
                  }
               }
            }
         }

         if (skillID == 14121052) {
            int needSummon = batLimit - var25;

            for (int i = 0; i < needSummon; i++) {
               this.getPlayer().summonShadowBat(batSummon, this.getPlayer().getPosition());
            }
         }

         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.NightWalkerBat);
         if (e == null) {
            return;
         }

         int batAtomSkillID = 14000028;
         if (e.getSourceId() == 14121016) {
            batAtomSkillID = 14120020;
         }

         if (e.getSourceId() == 14141004) {
            batAtomSkillID = 14141006;
         }

         if (e.getSourceId() == 14141012) {
            batAtomSkillID = 14141016;
         }

         if (skillID == 400041037 || skillID == 500061035 && attacks != null) {
            int i = 0;

            for (AttackPair pair : attacks.allDamage) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initShadowBat(attacks.affectedSpawnPos.get(i++), 2000, batAtomSkillID == 14120020 ? 4 : (batAtomSkillID == 14141016 ? 4 : 2));
               ForceAtom forceAtom = new ForceAtom(
                  info,
                  batAtomSkillID,
                  this.getPlayer().getId(),
                  false,
                  true,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.SHADOW_BAT,
                  Collections.singletonList(pair.objectid),
                  1
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
         }

         if (GameConstants.isNightWalkerThrowingSkill(skillID) && batSummon != null) {
            if (var25 >= 1
               && skillID != 14121003
               && skillID != 14121052
               && attacks.isAttackWithDarkSight
               && skillID != 14121002
               && skillID != 14111021
               && skillID != 14101021
               && Randomizer.nextInt(100) < prob) {
               SecondaryStatEffect batEffect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.NightWalkerBat);
               if (batEffect.getSourceId() == 14121016) {
                  List<MapleMonster> mobs_ = this.getPlayer().getMap().getMobsInRect(this.getPlayer().getPosition(), -600, -600, 600, 600);
                  int highestStack = 0;
                  if (!mobs_.isEmpty()) {
                     Collections.sort(mobs_, (o1, o2) -> Long.compare(o2.getStats().getMaxHp(), o1.getStats().getMaxHp()));
                     MapleMonster highest = mobs_.get(0);

                     for (AttackPair pair : attacks.allDamage) {
                        MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(pair.objectid);
                        if (mob != null && mob.isAlive()) {
                           MobTemporaryStatEffect mse = null;
                           if ((mse = mob.getBuff(MobTemporaryStatFlag.ELEMENT_DARKNESS)) != null && highestStack < mse.getX()) {
                              highestStack = mse.getX();
                           }
                        }
                     }

                     var25 = Math.max(1, Math.min(var25, highestStack));

                     for (int i = 0; i < var25; i++) {
                        Summoned remove = summonList.stream().findAny().orElse(null);
                        if (remove != null) {
                           ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                           info.initShadowBat(
                              remove.getTruePosition(),
                              remove.getSkill() == 14120019
                                 ? 3
                                 : (remove.getSkill() == 14141015 ? 3 : (batAtomSkillID == 14120020 ? 4 : (batAtomSkillID == 14141016 ? 4 : 2)))
                           );
                           ForceAtom forceAtom = new ForceAtom(
                              info,
                              remove.getSkill() == 14120019 ? 14120020 : (remove.getSkill() == 14141015 ? 14141016 : batAtomSkillID),
                              this.getPlayer().getId(),
                              false,
                              true,
                              this.getPlayer().getId(),
                              ForceAtom.AtomType.SHADOW_BAT,
                              Collections.singletonList(highest.getObjectId()),
                              1
                           );
                           this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                           this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(remove, true));
                           this.getPlayer().getMap().removeMapObject(remove);
                           this.getPlayer().removeVisibleMapObject(remove);
                           this.getPlayer().removeSummon(remove);
                           summonList.remove(remove);
                        }
                     }
                  }
               } else {
                  List<MapleMonster> mobs_ = this.getPlayer().getMap().getMobsInRect(this.getPlayer().getPosition(), -600, -600, 600, 600);
                  int highestStack = 0;

                  for (AttackPair pairx : attacks.allDamage) {
                     MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(pairx.objectid);
                     if (mob != null && mob.isAlive()) {
                        MobTemporaryStatEffect mse = null;
                        if ((mse = mob.getBuff(MobTemporaryStatFlag.ELEMENT_DARKNESS)) != null && highestStack < mse.getX()) {
                           highestStack = mse.getX();
                        }
                     }
                  }

                  var25 = Math.max(1, Math.min(var25, highestStack));

                  for (int ix = 0; ix < var25; ix++) {
                     Summoned remove = summonList.stream().findAny().orElse(null);
                     if (remove != null) {
                        Collections.shuffle(mobs_);
                        MapleMonster m = mobs_.stream().findAny().orElse(null);
                        ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                        info.initShadowBat(
                           remove.getTruePosition(),
                           remove.getSkill() == 14110033 ? 3 : (batAtomSkillID == 14120020 ? 4 : (batAtomSkillID == 14141016 ? 4 : 2))
                        );
                        ForceAtom forceAtom = new ForceAtom(
                           info,
                           remove.getSkill() == 14110033 ? 14110035 : batAtomSkillID,
                           this.getPlayer().getId(),
                           false,
                           true,
                           this.getPlayer().getId(),
                           ForceAtom.AtomType.SHADOW_BAT,
                           Collections.singletonList(m.getObjectId()),
                           1
                        );
                        this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                        this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(remove, true));
                        this.getPlayer().getMap().removeMapObject(remove);
                        this.getPlayer().removeVisibleMapObject(remove);
                        this.getPlayer().removeSummon(remove);
                        summonList.remove(remove);
                     }
                  }
               }
            }

            if (var25 < batLimit) {
               this.getPlayer().setNightWalkerAttackCount(this.getPlayer().getNightWalkerAttackCount() + 1);
               if (this.getPlayer().getNightWalkerAttackCount() % 3 == 0) {
                  this.getPlayer().summonShadowBat(batSummon, this.getPlayer().getPosition());
                  this.getPlayer().setNightWalkerAttackCount(0);
               }
            }
         }
      }
   }

   public void tryApplySiphonVitality() {
      int skillID = 14120009;
      SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getTotalSkillLevel(skillID));
      if (effect != null) {
         int max = effect.getX();
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.SiphonVitality);
         if (value == null) {
            value = 0;
         }

         int x = Math.min(max, value + 1);
         if (x < max) {
            this.getPlayer().temporaryStatSet(skillID, effect.getDuration(), SecondaryStatFlag.SiphonVitality, x);
         } else {
            this.getPlayer().temporaryStatSet(skillID, effect.getDuration(), SecondaryStatFlag.SiphonVitality, 0);
         }

         if (x >= max) {
            SecondaryStatEffect eff = SkillFactory.getSkill(14120049).getEffect(this.getPlayer().getTotalSkillLevel(14120049));
            int y = effect.getY();
            if (eff != null) {
               y += eff.getX();
            }

            int shield = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01) * y;
            this.getPlayer().temporaryStatSet(14120011, effect.getSubTime(), SecondaryStatFlag.SiphonVitalityShield, shield);
         } else if (this.getPlayer().getBuffedValue(SecondaryStatFlag.SiphonVitalityShield) != null) {
            this.getPlayer()
               .temporaryStatSet(
                  14120011,
                  effect.getSubTime(),
                  SecondaryStatFlag.SiphonVitalityShield,
                  this.getPlayer().getBuffedValue(SecondaryStatFlag.SiphonVitalityShield)
               );
         }
      }
   }

   public void tryShadowSpear(int skillID, AttackInfo info) {
      if (GameConstants.isNightWalker(this.getPlayer().getJob())) {
         if (skillID != 14121003 && skillID != 400041008 && skillID != 400041019 && skillID != 400041060 && skillID != 400041059) {
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ShadowSpear) != null) {
               SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(400040008);
               SecondaryStatEffect effect2 = SkillFactory.getSkill(400041008).getEffect(this.getPlayer().getTotalSkillLevel(400041008));
               if (effect != null && effect2 != null && !info.affectedSpawnPos.isEmpty()) {
                  if (skillID == 400040008) {
                     for (Point pt : info.affectedSpawnPos) {
                        Rect rect = new Rect(pt, effect2.getLt(), effect2.getRb(), (info.display & 32768) != 0);
                        this.getPlayer().getMap().spawnMist(new AffectedArea(rect, this.getPlayer(), effect2, pt, System.currentTimeMillis() + 50L));
                     }

                     int size = this.getPlayer().getMap().getAffectedAreaSize(400040008, this.getPlayer().getId());
                     if (size >= 5 && this.getPlayer().getCooldownLimit(400041019) == 0L) {
                        PacketEncoder packet = new PacketEncoder();
                        packet.writeShort(SendPacketOpcode.SHADOW_SPEAR_BIG_ACTIVE.getValue());
                        Point pt = info.affectedSpawnPos.get(0);
                        packet.writeInt(pt.x);
                        packet.writeInt(pt.y);
                        this.getPlayer().getMap().broadcastMessage(packet.getPacket());
                        this.getPlayer().addCooldown(400041019, System.currentTimeMillis(), (int)(effect2.getT() * 1000.0));
                     }

                     return;
                  }

                  Skill skill = SkillFactory.getSkill(skillID);
                  if (skill != null && skill.getElement() == Element.Dark && effect.makeChanceResult()) {
                     Point pt = info.affectedSpawnPos.get(0);
                     if (pt != null) {
                        if (info.allDamage.get(0) != null && info.allDamage.get(0).refImgId == 8880153) {
                           pt.x = 730;
                           pt.y = -488;
                           Rect rect = new Rect(pt, effect.getLt(), effect.getRb(), (info.display & 32768) != 0);
                           this.getPlayer()
                              .getMap()
                              .spawnMist(new AffectedArea(rect, this.getPlayer(), effect, pt, System.currentTimeMillis() + effect2.getSubTime() / 1000));
                        } else if (info.allDamage.get(0) != null && (info.allDamage.get(0).refImgId == 8644650 || info.allDamage.get(0).refImgId == 8644655)) {
                           pt.x = 46;
                           pt.y = -450;
                           Rect rect = new Rect(pt, effect.getLt(), effect.getRb(), (info.display & 32768) != 0);
                           this.getPlayer()
                              .getMap()
                              .spawnMist(new AffectedArea(rect, this.getPlayer(), effect, pt, System.currentTimeMillis() + effect2.getSubTime() / 1000));
                        } else {
                           Rect rect = new Rect(pt, effect.getLt(), effect.getRb(), (info.display & 32768) != 0);
                           this.getPlayer()
                              .getMap()
                              .spawnMist(new AffectedArea(rect, this.getPlayer(), effect, pt, System.currentTimeMillis() + effect2.getSubTime() / 1000));
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
