package objects.users.jobs.adventure.magician;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.Element;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.Summoned;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_RectPos;
import objects.users.skills.TeleportAttackData_TriArray;
import objects.users.skills.TeleportAttackData_TriArray_Elem;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class ArcMageFP extends DefaultMagician {
   private ScheduledFuture<?> fireAuraTask = null;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 2141503) {
         Skill skil = SkillFactory.getSkill(2141500);
         SecondaryStatEffect eff = skil.getEffect(effect.getLevel());
         this.handleSixJobNoDeathTime(2141503, eff);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.InfernalVenom, 2141503, eff.getDuration(), 5);
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
      if (this.getPlayer().getJob() == 212) {
         this.updateElementalDrain();
      }

      if (attack.skillID != 400021092 && this.getPlayer().getJob() >= 210 && this.getPlayer().getJob() <= 212) {
         Skill s = SkillFactory.getSkill(attack.skillID);
         if (s != null && s.getElement() == Element.Fire || attack.skillID == 400021001) {
            if (this.getPlayer().getSkillLevel(2110012) > 0) {
               SecondaryStatEffect manaburn = SkillFactory.getSkill(2110012).getEffect(this.getPlayer().getSkillLevel(2110012));
               int monsterMaxMP = monster.getMobMaxMp();
               if (monsterMaxMP > 0) {
                  int monsterMP = monster.getMp();
                  int monsterMpRemainingPercent = monsterMP * 100 / monsterMaxMP;
                  if (monsterMpRemainingPercent >= manaburn.getZ() && manaburn.makeChanceResult()) {
                     monster.setMp(Math.max(0, monsterMP - monsterMaxMP / 100));
                     int delta = Math.min(manaburn.getQ(), Math.max(manaburn.getY(), monsterMaxMP / 100));
                     this.getPlayer().addMP(delta);
                  }
               }
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.WizardIgnite) != null
               && attack.skillID != 2100010
               && (s != null && s.getElement() == Element.Fire || attack.skillID == 400021001)) {
               SecondaryStatEffect eff = SkillFactory.getSkill(2100010).getEffect(this.getPlayer().getSkillLevel(2101010));
               if (eff != null && eff.makeChanceResult()) {
                  Point pos = monster.getPosition();
                  if (monster.getId() == 8880153) {
                     pos.y -= 450;
                     pos.x += 150;
                  }

                  Rectangle rect = eff.calculateBoundingBox(pos, false);
                  pos = this.getPlayer().getMap().calcDropPos(new Point(rect.x, rect.y - 23), pos);
                  this.getPlayer().getMap().spawnMist(new AffectedArea(rect, this.getPlayer(), eff, pos, System.currentTimeMillis() + eff.getDuration()));
                  this.getPlayer().getMap().spawnMist(new AffectedArea(rect, this.getPlayer(), eff, pos, System.currentTimeMillis() + eff.getDuration()));
               }
            }
         }
      }

      if ((attack.skillID == 2121052 || attack.skillID == 2121054) && effect != null) {
         MobTemporaryStatEffect monsterStatusEffect = new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, attack.skillID, null, false);
         monster.applyStatus(this.getPlayer(), monsterStatusEffect, true, effect.getDOTTime() * 1000, true, effect);
      }

      if (attack.skillID == 2121011) {
         MobTemporaryStatEffect eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getX(), 90001002, null, false);
         eff.setDuration(10000);
         eff.setCancelTask(10000L);
         eff.setValue(1);
         monster.applyStatus(eff);
         MobTemporaryStatEffect eff2 = new MobTemporaryStatEffect(MobTemporaryStatFlag.DODGE_BODY_ATTACK, 1, 90001002, null, false);
         eff2.setDuration(10000);
         eff2.setCancelTask(10000L);
         eff2.setValue(1);
         monster.applyStatus(eff2);
      }

      if (totalDamage > 0L && attack.skillID == 2121003) {
         monster.setTempEffectiveness(Element.Ice, effect.getDuration());
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 2141000 && attack.allDamage.size() > 0) {
         int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.FlameSweep, 0) + 1;
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.FlameSweep, 2141000, Integer.MAX_VALUE, stack);
      }

      if (attack.skillID == 2141001 && attack.allDamage.size() > 0) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.FlameSweep);
      }

      if (attack.skillID == 2111003) {
         this.getPlayer()
            .getMap()
            .spawnMist(
               new AffectedArea(
                  effect.calculateBoundingBox(attack.position2, this.getPlayer().isFacingLeft()),
                  this.getPlayer(),
                  effect,
                  attack.position2,
                  System.currentTimeMillis() + effect.getDuration()
               )
            );
      }

      if (attack.skillID == 400021101) {
         PacketEncoder p = new PacketEncoder();
         p.writeShort(SendPacketOpcode.POISON_CHAIN_RESULT.getValue());
         p.write(true);
         p.writeInt(attack.allDamage.size());

         for (AttackPair a : attack.allDamage) {
            p.writeInt(a.objectid);
            p.writeInt(1);
            p.writeInt(0);
            p.writeInt(effect.getY() * 1000);
         }

         p.writeInt(effect.getU());
         this.getPlayer().send(p.getPacket());
      }

      List<AffectedArea> areaList = this.getPlayer().getMap().getAffectedAreasBySkillId(2111013, this.getPlayer().getId());
      if (!areaList.isEmpty()) {
         Skill summonSkill = SkillFactory.getSkill(2111013);
         if (summonSkill != null) {
            List<ExtraSkillInfo> extraSkills = new ArrayList<>();

            for (ExtraSkillInfo skills : summonSkill.getExtraSkillInfo()) {
               extraSkills.add(skills.clone());
            }

            extraSkills.forEach(extraSkill -> extraSkill.delay = 250);
            if (attack.skillID == 2111014) {
               List<TeleportAttackElement> elements = attack.teleportAttackAction.actions;
               boolean find = false;
               TeleportAttackElement t_data = elements.stream().filter(t_ -> t_.data instanceof TeleportAttackData_RectPos).findFirst().orElse(null);
               if (t_data != null) {
                  TeleportAttackData_RectPos t_data_ = (TeleportAttackData_RectPos)t_data.data;
                  Point attackPos = t_data_.getPos();
                  AffectedArea typeLeft = null;
                  AffectedArea typeRight = null;

                  for (AffectedArea area : areaList) {
                     if (Math.abs(attackPos.x - area.getTruePosition().x) == 200) {
                        if (attackPos.x - area.getTruePosition().x < 0) {
                           if (typeLeft != null) {
                              if (Math.abs(attackPos.x - area.getTruePosition().x) < Math.abs(attackPos.x - typeLeft.getTruePosition().x)) {
                                 typeLeft = area;
                              }
                           } else {
                              typeLeft = area;
                           }
                        } else if (typeRight != null) {
                           if (Math.abs(attackPos.x - area.getTruePosition().x) < Math.abs(attackPos.x - typeRight.getTruePosition().x)) {
                              typeRight = area;
                           }
                        } else {
                           typeRight = area;
                        }
                     }
                  }

                  if (typeLeft != null) {
                     if (this.getPlayer().isGM()) {
                        this.getPlayer().dropMessage(6, "typeLeft x: " + typeLeft.getTruePosition().x);
                     }

                     this.getPlayer()
                        .send(
                           CField.getRegisterExtraSkill(
                              2111013,
                              typeLeft.getTruePosition().x,
                              typeLeft.getTruePosition().y - 154,
                              (attack.display & 32768) != 0,
                              extraSkills,
                              1,
                              Collections.emptyList(),
                              Collections.emptyList(),
                              typeLeft.getObjectId()
                           )
                        );
                  }

                  if (typeRight != null) {
                     if (this.getPlayer().isGM()) {
                        this.getPlayer().dropMessage(6, "typeRight x: " + typeRight.getTruePosition().x);
                     }

                     this.getPlayer()
                        .send(
                           CField.getRegisterExtraSkill(
                              2111013,
                              typeRight.getTruePosition().x,
                              typeRight.getTruePosition().y - 154,
                              (attack.display & 32768) != 0,
                              extraSkills,
                              1,
                              Collections.emptyList(),
                              Collections.emptyList(),
                              typeRight.getObjectId()
                           )
                        );
                  }
               }
            } else if (summonSkill.getSkillList2().contains(attack.skillID)) {
               List<AffectedArea> clone = new ArrayList<>();
               areaList.stream().forEach(areax -> clone.add(areax));
               Collections.sort(clone, (o1, o2) -> Integer.compare(o1.getTruePosition().x, o2.getTruePosition().x));
               Point check = attack.attackPosition;
               int i = 1;

               for (AffectedArea areax : clone) {
                  if (effect.calculateBoundingBox(check, false).contains(areax.getTruePosition())) {
                     int delay = i++ * 100;
                     extraSkills.forEach(extraSkill -> extraSkill.delay = delay);
                     this.getPlayer()
                        .send(
                           CField.getRegisterExtraSkill(
                              2111013,
                              attack.skillID,
                              areax.getTruePosition().x,
                              areax.getTruePosition().y - 154,
                              false,
                              extraSkills,
                              1,
                              Collections.emptyList(),
                              Collections.emptyList(),
                              areax.getObjectId()
                           )
                        );
                     check = areax.getTruePosition();
                  }
               }

               Collections.reverse(clone);
               check = attack.attackPosition;
               i = 1;

               for (AffectedArea areaxx : clone) {
                  if (effect.calculateBoundingBox(check, true).contains(areaxx.getTruePosition())) {
                     int delay = i++ * 100;
                     extraSkills.forEach(extraSkill -> extraSkill.delay = delay);
                     this.getPlayer()
                        .send(
                           CField.getRegisterExtraSkill(
                              2111013,
                              attack.skillID,
                              areaxx.getTruePosition().x,
                              areaxx.getTruePosition().y - 154,
                              true,
                              extraSkills,
                              1,
                              Collections.emptyList(),
                              Collections.emptyList(),
                              areaxx.getObjectId()
                           )
                        );
                     check = areaxx.getTruePosition();
                  }
               }
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 2121054:
            this.startFireAuraTask();
            effect.applyTo(this.getPlayer(), true);
            break;
         case 400021001:
            try {
               List<Point> pos = new ArrayList<>();
               int left = this.getPlayer().getPosition().x - 500;
               int top = this.getPlayer().getPosition().x + 500;
               int right = this.getPlayer().getPosition().y - 450;
               int bottom = this.getPlayer().getPosition().y - 75;
               List<Integer> targets = new LinkedList<>();
               int x = effect.getX();
               int burnedSize = 0;

               for (MapleMonster m : new ArrayList<>(
                  this.getPlayer()
                     .getMap()
                     .getMobsInRect(this.getPlayer().getPosition(), effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y)
               )) {
                  if (m != null && m.isAlive()) {
                     burnedSize += m.getBurendSizeByPlayerID(this.getPlayer().getId());
                  }
               }

               if (burnedSize > 0) {
                  int y = burnedSize / effect.getY();
                  x += y;
               }

               if (x > effect.getZ()) {
                  x = effect.getZ();
               }

               for (int i = 0; i < x; i++) {
                  targets.add(0);
                  pos.add(new Point(Randomizer.rand(left, top), Randomizer.rand(right, bottom)));
               }

               ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
               atomInfo.initDotPunisher(pos);
               ForceAtom atom = new ForceAtom(
                  atomInfo, 400021001, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.DOT_PUNISHER, targets, pos.size()
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
               effect.applyTo(this.getPlayer(), true);
            } catch (Exception var14) {
               FileoutputUtil.outputFileError("Log_Packet_Except.rtf", var14);
            }
            break;
         case 400021102:
            List<SecondAtom.Atom> atoms = new ArrayList<>();

            for (TeleportAttackElement e : this.teleportAttackAction.actions) {
               if (e.data instanceof TeleportAttackData_TriArray) {
                  TeleportAttackData_TriArray data = (TeleportAttackData_TriArray)e.data;

                  for (TeleportAttackData_TriArray_Elem d : data.data) {
                     SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(),
                        this.getPlayer().getId(),
                        400021102,
                        ForceAtom.SN.getAndAdd(1),
                        SecondAtom.SecondAtomType.PoisonChain,
                        0,
                        null,
                        new Point(d.x, d.y)
                     );
                     a.setTargetObjectID(d.objectID);
                     a.setExpire(10000);
                     a.setAttackableCount(1);
                     a.setSkillID(400021102);
                     a.setCustoms(Collections.singletonList(new SecondAtom.Custom(1, 1)));
                     this.getPlayer().addSecondAtom(a);
                     atoms.add(a);
                  }

                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400021102, atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
               }
            }
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      this.updateElementalDrain();
   }

   public void removeSummon(Summoned s) {
      if (s.getSkill() == 2111013) {
         for (AffectedArea area : this.getPlayer().getMap().getAllMistsThreadsafe()) {
            if (area.getSourceSkillID() == 2111013) {
               this.getPlayer().getMap().removeMapObject(area);
               this.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 2111013, false));
            }
         }
      }
   }

   public void updateElementalDrain() {
      int bc = 0;

      for (MapleMonster mob : this.getPlayer().getMap().getAllMonster()) {
         int count = mob.getBurendSizeByPlayerID(this.getPlayer().getId());
         bc += count;
      }

      if (bc > 0) {
         int incMax = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.InfernalVenom, 0);
         bc = Math.min(5 + incMax, bc);
         this.getPlayer().temporaryStatSet(2100009, Integer.MAX_VALUE, SecondaryStatFlag.DotBasedBuff, bc);
      } else if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DotBasedBuff) != null) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.DotBasedBuff);
      }
   }

   public void cancelFireAuraTask() {
      if (this.fireAuraTask != null) {
         this.fireAuraTask.cancel(true);
         this.fireAuraTask = null;
      }
   }

   public void startFireAuraTask() {
      if (this.fireAuraTask != null) {
         this.cancelFireAuraTask();
      }

      this.fireAuraTask = Timer.BuffTimer.getInstance().register(() -> {
         if (this.getPlayer() == null) {
            this.cancelFireAuraTask();
         } else {
            SecondaryStatEffect effect = SkillFactory.getSkill(2121054).getEffect(this.getPlayer().getTotalSkillLevel(2121054));
            if (effect.getMPCon() > this.getPlayer().getStat().getMp()) {
               this.fireAuraTask.cancel(true);
               this.fireAuraTask = null;
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.FireAura);
            } else {
               this.getPlayer().addMP(-effect.getMPCon());
            }
         }
      }, 1000L);
   }
}
