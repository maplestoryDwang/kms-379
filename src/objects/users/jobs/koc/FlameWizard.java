package objects.users.jobs.koc;

import java.awt.Point;
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
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Magician;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Timer;

public class FlameWizard extends Magician {
   public int infinityFlameCircleStack;
   private int orbitalExplosionStack = 0;
   private int flameDischargeStack = 0;
   private int flameDischargeBossStack = 0;
   private int flameDischargeRegenCount = 0;
   private int addFlameDischargeRegenCount = 0;
   private int cycleFlameCount = 0;
   private ScheduledFuture<?> cycleFlameTask = null;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 12121052) {
         this.getPlayer().temporaryStatSet(12121052, 4000, SecondaryStatFlag.NotDamaged, 1);
      } else if (attack.skillID == 400021043 && this.getPlayer().getBuffedValue(SecondaryStatFlag.Ember) != null) {
         this.flameDischargeStack = 0;
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.Ember);
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
         RecvPacketOpcode opcode) {
      if (opcode == RecvPacketOpcode.NON_TARGET_FORCE_ATOM_ATTACK || opcode == RecvPacketOpcode.MAGIC_ATTACK) {
         if (this.getPlayer().getTotalSkillLevel(12101022) > 0) {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(12101022);
            if (level != null && level.makeChanceResult()) {
               int y = boss ? level.getU() : level.getY();
               int mp = (int) (this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * y);
               this.getPlayer().addMP(mp, true);
            }
         }

         if (skill.getId() == 12120019 || skill.getId() == 12120020 || skill.getId() == 12141004
               || skill.getId() == 12141005) {
            SecondaryStatEffect eff = SkillFactory.getSkill(12120024).getEffect(31);
            int mischief = monster.getFoxMischiefStack();
            if (mischief >= 100) {
               monster.setFoxMischiefStack(100);
            } else {
               monster.setFoxMischiefStack(mischief + 1);
            }

            if (eff != null) {
               monster.setFoxFlameDebuffer(this.getPlayer().getId());
               monster.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.ELEMENT_RESET_BY_SUMMON, -10, 12120024, null,
                           false),
                     false,
                     eff.getDuration(),
                     false,
                     eff);
               if (mischief >= 10) {
                  int stack = Math.min(mischief / 10, 5);
                  List<Pair<Integer, Integer>> list = new ArrayList<>();
                  list.add(new Pair<>(monster.getObjectId(), stack));
                  this.getPlayer().send(CField.showMonsterStackedDebuffMark(12120021, 12120022, list, 20000, 5));
               }
            }
         }

         if (skill.getId() == 12120023) {
            int attackCount = monster.getFoxMischiefStack();
            int stack = attackCount / 10;
            if (stack >= 5) {
               this.getPlayer().send(CField.attackMonsterStackedDebuffMark(12120022, 5, monster.getObjectId(), 0));
               monster.setFoxMischiefStack(0);
               List<Pair<Integer, Integer>> list = new ArrayList<>();
               list.add(new Pair<>(monster.getObjectId(), 0));
               this.getPlayer().send(CField.showMonsterStackedDebuffMark(12120021, 12120022, list, 20000, 5));
            }
         }
      }

      if (this.getPlayer().getJob() == 1212) {
         if (this.getPlayer().hasBuffBySkillID(400021092)
               && this.getPlayer().getSecondAtomCount(SecondAtom.SecondAtomType.SalamanderMischief) <= 0) {
            Summoned summon = this.getPlayer().getSummonBySkillID(400021092);
            if (summon != null) {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.SUMMON_SKILL.getValue());
               packet.writeInt(this.getPlayer().getId());
               packet.writeInt(summon.getObjectId());
               packet.write(0);
               packet.writeInt(0);
               if (summon.getSkill() == 400021092) {
                  packet.writeInt(1);
               }

               this.getPlayer().getMap().broadcastMessage(packet.getPacket());
               List<SecondAtom.Atom> atoms = new ArrayList<>();
               Skill skill_ = SkillFactory.getSkill(400021092);
               SecondAtom.Atom a = new SecondAtom.Atom(
                     this.getPlayer().getMap(),
                     this.getPlayer().getId(),
                     400021092,
                     ForceAtom.SN.getAndAdd(1),
                     SecondAtom.SecondAtomType.SalamanderMischief,
                     0,
                     null,
                     monster.getTruePosition());
               SecondAtomData data = skill_.getSecondAtomData();
               a.setPlayerID(this.getPlayer().getId());
               a.setTargetObjectID(monster.getObjectId());
               a.setExpire(data.getExpire());
               a.setAttackableCount(data.getAttackableCount());
               a.setEnableDelay(data.getEnableDelay());
               a.setRange(30);
               a.setSkillID(400021092);
               this.getPlayer().addSecondAtom(a);
               atoms.add(a);
               SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400021092, atoms);
               this.getPlayer().getMap().createSecondAtom(secondAtom);
            }
         }

         if (attack.skillID == 400021092) {
            Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.SalamanderMischief);
            int v = 0;
            if (value != null) {
               v = value;
            }

            v = Math.min(effect.getZ(), v + 1);
            this.getPlayer().temporaryStatSet(400021092, effect.getDuration(), SecondaryStatFlag.SalamanderMischief, v);
            this.getPlayer().addMP(-effect.getV());
         }
      }

      if (attack.skillID == 12111022 && monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
         monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false), false, 10000L,
               false, effect);
         monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(),
               attack.skillID);
      }

      if (totalDamage > 0L) {
         if (this.getPlayer().getTotalSkillLevel(400021042) > 0) {
            Skill skill_ = SkillFactory.getSkill(400021042);
            SecondaryStatEffect level = skill_.getEffect(this.getPlayer().getTotalSkillLevel(400021042));
            if (level != null && (skill_.getSkillList().contains(attack.skillID)
                  || skill_.getSkillList2().contains(attack.skillID))) {
               if (this.flameDischargeStack >= 6) {
                  if (this.flameDischargeStack > 0) {
                     if (this.flameDischargeStack >= 6) {
                        this.flameDischargeStack = 6;
                     }

                     this.getPlayer().temporaryStatSet(SecondaryStatFlag.Ember, 400021042, Integer.MAX_VALUE,
                           this.flameDischargeStack);
                  }
               } else if (boss || monster != null && monster.getStats() != null && monster.getStats().isBoss()) {
                  if (this.flameDischargeBossStack < level.getZ()) {
                     this.flameDischargeBossStack++;
                     if (this.flameDischargeBossStack >= level.getZ()) {
                        this.flameDischargeStack++;
                        this.flameDischargeBossStack = 0;
                        this.getPlayer().temporaryStatSet(SecondaryStatFlag.Ember, 400021042, Integer.MAX_VALUE,
                              this.flameDischargeStack);
                     }
                  }

                  monster.setFoxFlameStack(this.addFlameDischargeRegenCount);
               } else {
                  this.flameDischargeStack++;
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.Ember, 400021042, Integer.MAX_VALUE,
                        this.flameDischargeStack);
               }
            }
         }

         if (attack.skillID != 12121002 && attack.skillID != 12121052 && attack.skillID == 12121054) {
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   public void increaseDischarge() {
      if (this.flameDischargeStack < 6) {
         this.flameDischargeStack++;
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.Ember, 400021042, Integer.MAX_VALUE,
               this.flameDischargeStack);
      }
   }

   @Override
   public void afterAttack(
         boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill,
         long totalExp, RecvPacketOpcode opcode) {
      if (attack.targets > 0 && this.getPlayer().getBuffedValue(SecondaryStatFlag.OrbitalExplosion) != null) {
         Skill s = SkillFactory.getSkill(12101030);
         if (s != null
               && (s.getSkillList().contains(attack.skillID)
                     || s.getSkillList2().contains(attack.skillID)
                     || s.getSkillList3().contains(attack.skillID)
                     || s.getSkillList4().contains(attack.skillID))
               && ++this.orbitalExplosionStack >= 10) {
            List<Pair<Integer, Integer>> list = new ArrayList<>();
            attack.allDamage.stream().forEach(l -> list.add(new Pair<>(l.objectid, 0)));
            this.getPlayer().send(CField.userBonusAttackRequest(12101030, false, list));
            this.orbitalExplosionStack = 0;
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
      super.beforeActiveSkill(packet);
   }

   @Override
   public void updatePerSecond() {
      if (this.cycleFlameTask != null && this.getPlayer().getBuffedEffect(SecondaryStatFlag.Eternity) == null) {
         this.cancelCycleFlameTask();
      }

      super.updatePerSecond();
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 12101024:
            if (this.getPlayer().hasBuffBySkillID(12101024)) {
               this.getPlayer().temporaryStatResetBySkillID(12101024);
               return;
            }

            this.orbitalExplosionStack = 0;
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.OrbitalExplosion, 12101024, Integer.MAX_VALUE, 1);
            break;
         case 12101025: {
            packet.skip(4);
            Point pos = packet.readPos();
            packet.skip(1);
            if (packet.readByte() == 1) {
               this.getPlayer()
                     .send(CField.getTeleport(0, 2, this.getPlayer().getId(), this.getPlayer().getFireBlinkPos()));
            } else {
               this.getPlayer().setFireBlinkPos(pos);
            }

            effect.applyTo(this.getPlayer(), true);
         }
            break;
         case 12111022: {
            Point pos = packet.readPos();
            byte faceLeft = packet.readByte();
            packet.skip(2);
            int mobObjectID = packet.readInt();
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(mobObjectID);
            if (mob == null || !mob.isAlive() || mob.getStats().isBoss()) {
               return;
            }

            Summoned summon = new Summoned(
                  this.getPlayer(),
                  this.getActiveSkillID(),
                  this.getActiveSkillLevel(),
                  this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition()),
                  SummonMoveAbility.STATIONARY,
                  faceLeft,
                  effect.getDuration());
            summon.setMobObjectID(mob.getId());
            this.getPlayer().getMap().spawnSummon(summon, effect.getDuration());
            this.getPlayer().addSummon(summon);
            effect.applyTo(this.getPlayer(), true);
         }
            break;
         case 12120013:
         case 12120014:
            if (this.getPlayer().hasBuffBySkillID(12120013)) {
               this.getPlayer().temporaryStatResetBySkillID(12120013, true);
            }

            if (this.getPlayer().hasBuffBySkillID(12120014)) {
               this.getPlayer().temporaryStatResetBySkillID(12120014, true);
            }

            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(12121004);
            if (level == null) {
               return;
            }

            Summoned summon = new Summoned(
                  this.getPlayer(),
                  this.getActiveSkillID(),
                  this.getActiveSkillLevel(),
                  new Point(x, y),
                  SummonMoveAbility.FLAME_SUMMON,
                  isLeft,
                  System.currentTimeMillis() + level.getDuration(level.getDuration(), this.getPlayer()));
            this.getPlayer().getMap().spawnSummon(summon, level.getDuration(level.getDuration(), this.getPlayer()));
            this.getPlayer().addSummon(summon);
            break;
         case 12121054:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.PhoenixDrive, this.getActiveSkillID(),
                  effect.getDuration(), 1);
            break;
         case 12141500:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.Eternity, this.getActiveSkillID(), effect.getDuration(),
                  1);
            this.startCycleFlameTask();
            break;
         case 400021044:
            byte targetNum = packet.readByte();
            List<Integer> targets = new LinkedList<>();

            for (int next = 0; next < targetNum; next++) {
               targets.add(packet.readInt());
            }

            ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
            ForceAtom atom = new ForceAtom(
                  atomInfo, 400021045, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                  ForceAtom.AtomType.ATOM_REGEN, targets, 8);
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
            this.addFlameDischargeRegenCount = this.flameDischargeStack >= 2 ? this.flameDischargeStack - 2 : 0;
            this.flameDischargeRegenCount = 0;
            this.flameDischargeStack = 0;
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.Ember);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400021072) {
         SecondaryStatEffect eff = SkillFactory.getSkill(this.getActiveSkillID())
               .getEffect(this.getPlayer().getTotalSkillLevel(this.getActiveSkillID()));
         if (eff != null) {
            this.getPlayer().giveCoolDowns(400021072, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
            this.getPlayer().send(CField.skillCooldown(400021072, eff.getCooldown(this.getPlayer())));
         }
      }

      super.activeSkillCancel();
   }

   public void setInfinityFlameCircle(int stack) {
      if (stack < this.infinityFlameCircleStack) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400021072)
               .getEffect(this.getPlayer().getSkillLevel(400021072));
         this.getPlayer().addMP(-effect.getMPCon());
      }

      this.infinityFlameCircleStack = stack;
      if (this.getPlayer().getSkillLevel(400021072) > 0) {
         this.getPlayer().temporaryStatSet(400021072, Integer.MAX_VALUE, SecondaryStatFlag.InfinityFlameCircle,
               this.infinityFlameCircleStack);
      }
   }

   public void atomRegen(PacketDecoder slea, int removeCount) {
      int atomCount = slea.readInt();
      SecondaryStatEffect effect = SkillFactory.getSkill(400021042)
            .getEffect(this.getPlayer().getSkillLevel(400021042));
      if (effect != null) {
         for (int next = 0; next < atomCount; next++) {
            int skillID2 = slea.readInt();
            int key = slea.readInt();
            int posX = slea.readInt();
            int toMobID = slea.readInt();
         }

         int next = 0;
         if (next < removeCount) {
            int key = slea.readInt();
            slea.skip(1);
            int byMobID = slea.readInt();
            int tickCount = slea.readInt();
            int toMobID = slea.readInt();
            int posX = slea.readInt();
            int posY = slea.readInt();
            slea.skip(1);
            slea.skip(4);
            if (this.flameDischargeRegenCount < (effect.getU2() + this.addFlameDischargeRegenCount) * 8) {
               ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
               atomInfo.initFlameDischargeRegen(posX, posY);
               ForceAtom atom = new ForceAtom(
                     atomInfo,
                     400021045,
                     this.getPlayer().getId(),
                     true,
                     true,
                     byMobID,
                     ForceAtom.AtomType.ATOM_REGEN,
                     Collections.singletonList(toMobID),
                     atomCount);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
               this.flameDischargeRegenCount++;
            } else {
               this.addFlameDischargeRegenCount = 0;
            }
         }
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case InfinityFlameCircle:
            packet.writeInt(this.infinityFlameCircleStack);
            break;
         case Ember:
            packet.writeInt(this.flameDischargeStack);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public void cancelCycleFlameTask() {
      try {
         if (this.cycleFlameTask == null) {
            return;
         }

         this.cycleFlameTask.cancel(true);
         this.cycleFlameTask = null;
      } catch (Exception var2) {
         System.out.println("F/W Err");
         var2.printStackTrace();
      }
   }

   public void startCycleFlameTask() {
      if (this.cycleFlameTask != null) {
         this.cancelCycleFlameTask();
      }

      this.cycleFlameCount = 0;
      this.cycleFlameTask = Timer.BuffTimer.getInstance().register(() -> {
         if (this.getPlayer() != null && this.getPlayer().getMap() != null) {
            if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.Eternity) != null && this.cycleFlameCount < 9) {
               this.getPlayer().send(CField.userBonusAttackRequest(12141501, true, Collections.EMPTY_LIST, 0, 0));
               this.cycleFlameCount++;
            }
         } else {
            this.cancelCycleFlameTask();
         }
      }, 3000L);
   }
}
