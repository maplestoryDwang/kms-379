package objects.users.jobs.resistance;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.summoned.EnhancedSupportUnit;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.summoned.SummonedAura;
import objects.users.jobs.Pirate;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.CollectionUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.Timer;

public class Mechanic extends Pirate {
   private long lastMecaCarrierTime = 0L;
   private int mecaCarrierNum = 0;
   private ScheduledFuture<?> multipleOptionTask = null;
   private long lastMultipleOptionTime = 0L;
   private long lastGroundZeroTime = 0L;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 35121052) {
         boolean find = false;

         for (AffectedArea mist : this.getPlayer().getMap().getAllMistsThreadsafe()) {
            if (mist.getOwnerId() == this.getPlayer().getId() && mist.getSourceSkillID() == 35121052) {
               find = true;
            }
         }

         if (!find) {
            SecondaryStatEffect eff = SkillFactory.getSkill(35121052).getEffect(this.getPlayer().getSkillLevel(35121052));
            if (eff != null) {
               Point point = attack.forcedPos;
               if ((attack.display & 32768) == 0) {
                  point.x += 600;
               }

               Rectangle rect = eff.calculateBoundingBox(this.getPlayer().getPosition(), (attack.display & 32768) != 0);
               AffectedArea df = new AffectedArea(rect, this.getPlayer(), eff, point, System.currentTimeMillis() + eff.getDuration());
               df.setSkillDelay(6);
               this.getPlayer().getMap().spawnMist(df);
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
      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 35121016) {
         AttackPair lastAP = attack.allDamage.get(attack.allDamage.size() - 1);
         this.getPlayer().send(CField.userBonusAttackRequest(35121019, true, Collections.singletonList(new Pair<>(lastAP.point.x, lastAP.point.y))));
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder slea) {
      if (this.getActiveSkillPrepareID() == 400051041) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400051041);
         if (eff != null) {
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.KeyDownStart, 1);
            statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            this.getPlayer().temporaryStatSet(400051094, this.getPlayer().getTotalSkillLevel(400051041), eff.getW() * 1000, statups);
            eff.applyTo(this.getPlayer());
            this.getPlayer().giveCoolDowns(400051041, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
            this.getPlayer().send(CField.skillCooldown(400051041, eff.getCooldown(this.getPlayer())));
         }
      }
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 30001080:
            this.getPlayer().temporaryStatSet(30001080, 5000, SecondaryStatFlag.NewFlying, 1);
            break;
         case 35111002:
            List<Summoned> summonedList = new ArrayList<>();

            try {
               for (Summoned s : this.getPlayer().getSummonsReadLock()) {
                  if (s.getSkill() == 35111002) {
                     summonedList.add(s);
                  }
               }
            } finally {
               this.getPlayer().unlockSummonsReadLock();
            }

            if (summonedList.size() == 3) {
               for (Summoned sx : summonedList) {
                  this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(sx, true));
                  this.getPlayer().getMap().removeMapObject(sx);
                  this.getPlayer().removeSummon(sx);
               }
            }

            if (1 == packet.readInt()) {
               Point point1 = packet.readPos();
               Point point2 = packet.readPos();
               packet.readByte();
               Point point3 = packet.readPos();
               byte facingLeft = packet.readByte();
               effect.applyTo(this.getPlayer(), point1, facingLeft, this.exclusive);
               effect.applyTo(this.getPlayer(), point2, facingLeft, this.exclusive);
               effect.applyTo(this.getPlayer(), point3, facingLeft, this.exclusive);
            } else {
               packet.readByte();
               Point point1 = packet.readPos();
               byte facingLeft = packet.readByte();
               effect.applyTo(this.getPlayer(), point1, facingLeft, this.exclusive);
            }
            break;
         case 35111008:
         case 35120002:
            Point pos = packet.readPos();
            byte facingLeft = packet.readByte();

            for (Summoned summon : this.getPlayer().getSummons()) {
               if (summon.getSkill() == this.getActiveSkillID()) {
                  this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                  this.getPlayer().getMap().removeMapObject(summon);
                  this.getPlayer().removeVisibleMapObject(summon);
                  this.getPlayer().removeSummon(summon);
               }
            }

            Summoned summonx = new Summoned(
               this.getPlayer(),
               this.getActiveSkillID(),
               this.getActiveSkillLevel(),
               this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition()),
               SummonMoveAbility.STATIONARY,
               facingLeft,
               System.currentTimeMillis() + effect.getDuration(effect.getDuration(), this.getPlayer())
            );
            Rect rect = new Rect(effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y);
            summonx.setArea(rect);
            if (this.getActiveSkillID() == 35120002) {
               SummonedAura aura = new EnhancedSupportUnit();
               int slv = 0;
               if ((slv = this.getPlayer().getTotalSkillLevel(35120047)) > 0) {
                  SecondaryStatEffect eff = SkillFactory.getSkill(35120047).getEffect(slv);
                  if (eff != null) {
                     aura.z = eff.getX();
                  }
               }

               summonx.addAura(aura);
            }

            this.getPlayer().getMap().spawnSummon(summonx, effect.getDuration());
            this.getPlayer().addSummon(summonx);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 35141501:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.EpicDropRIncrease, 35141501, effect.getDuration(), 1);
            break;
         case 400051009:
            this.startMultipleOptionTask();
            effect.applyTo(this.getPlayer(), true);
            break;
         case 400051017:
            pos = packet.readPos();
            var flip = packet.readByte();
            List<MapleMonster> mobs = this.getPlayer()
               .getMap()
               .getMobsInRect(pos, effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y, flip == 1);
            List<Integer> mobList = new LinkedList<>();
            if (mobs.size() > 0) {
               for (int i = 0; i < effect.getMobCount(); i++) {
                  CollectionUtil.sortMonsterByBossHP(mobs);
                  int div = mobs.size();
                  MapleMonster m = mobs.get(i % div);
                  mobList.add(m.getObjectId());
               }

               if (mobList.size() > 0) {
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initMicroMissileContainer();
                  ForceAtom forceAtom = new ForceAtom(
                     info,
                     400051017,
                     this.getPlayer().getId(),
                     false,
                     true,
                     this.getPlayer().getId(),
                     ForceAtom.AtomType.MICRO_MISSILE_CONTAINER,
                     mobList,
                     effect.getBulletCount()
                  );
                  this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               }
            }

            effect.applyTo(this.getPlayer(), true);
            return;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400051041) {
         SecondaryStatEffect eff = SkillFactory.getSkill(400051094).getEffect(this.getPlayer().getTotalSkillLevel(this.getActiveSkillID()));
         if (eff != null) {
            this.getPlayer().giveCoolDowns(35101002, System.currentTimeMillis(), eff.getDuration());
            this.getPlayer().send(CField.skillCooldown(35101002, eff.getDuration()));
         }

         this.getPlayer().temporaryStatResetBySkillID(400051094);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieBlockSkill, 400051051, 2000, 1);
      }

      if (this.getActiveSkillID() == 30001080 && this.getPlayer().hasBuffBySkillID(30001080)) {
         this.getPlayer().temporaryStatResetBySkillID(30001080);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().hasBuffBySkillID(400051068)) {
         Summoned summon = this.getPlayer().getSummonBySkillID(400051068);
         if (summon == null) {
            return;
         }

         List<SecondAtom.Atom> atoms = new ArrayList<>();
         Skill skill = SkillFactory.getSkill(400051069);
         if (this.getPlayer().getSecondAtomCount(SecondAtom.SecondAtomType.MecaCarrier) <= 0) {
            SecondaryStatEffect e = SkillFactory.getSkill(400051069).getEffect(this.getPlayer().getTotalSkillLevel(400051068));
            if (e != null) {
               if (this.lastMecaCarrierTime == 0L) {
                  this.lastMecaCarrierTime = System.currentTimeMillis();
               }

               if (this.getPlayer().checkInterval(this.lastMecaCarrierTime, 3000)) {
                  List<MapleMonster> mobs = this.getPlayer().getMap().getMobsInRect(summon.getTruePosition(), -600, -600, 600, 600);
                  int q = e.getQ();
                  if (this.mecaCarrierNum == 0) {
                     this.mecaCarrierNum = e.getX();
                  } else {
                     this.mecaCarrierNum = Math.min(q, this.mecaCarrierNum + 1);
                  }

                  for (int i = 0; i < this.mecaCarrierNum; i++) {
                     Collections.shuffle(mobs);
                     MapleMonster m = mobs.stream().filter(m_ -> !m_.getStats().isOnlyHittedByCommonAttack()).findAny().orElse(null);
                     if (m != null) {
                        SecondAtom.Atom a = new SecondAtom.Atom(
                           this.getPlayer().getMap(),
                           this.getPlayer().getId(),
                           400051069,
                           SecondAtom.SN.getAndAdd(1),
                           SecondAtom.SecondAtomType.MecaCarrier,
                           0,
                           null,
                           summon.getTruePosition()
                        );
                        SecondAtomData data = skill.getSecondAtomData();
                        a.setPlayerID(this.getPlayer().getId());
                        a.setTargetObjectID(m.getObjectId());
                        a.setExpire(data.getExpire());
                        a.setAttackableCount(data.getAttackableCount());
                        a.setEnableDelay(data.getEnableDelay());
                        a.setRange(1);
                        a.setAngle(Randomizer.rand(data.getFirstAngleStart(), data.getFirstAngleStart() + data.getFirstAngleRange()));
                        a.setSkillID(400051069);
                        this.getPlayer().addSecondAtom(a);
                        atoms.add(a);
                     }
                  }

                  if (atoms.size() > 0) {
                     SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400051069, atoms);
                     secondAtom.setShowEffect(2);
                     this.getPlayer().getMap().createSecondAtom(secondAtom);
                  }

                  this.lastMecaCarrierTime = System.currentTimeMillis();
               }
            }
         }
      }

      if (this.getPlayer().hasBuffBySkillID(35111003)) {
         SecondaryStatEffect effect = SkillFactory.getSkill(35111003).getEffect(this.getPlayer().getSkillLevel(35111003));
         if (effect != null
            && (
               this.getPlayer().getLastTankConsumeMPTime() == 0L
                  || System.currentTimeMillis() - this.getPlayer().getLastTankConsumeMPTime() >= effect.getV() * 1000
            )) {
            this.getPlayer().addMP(-effect.getU());
            this.getPlayer().setLastTankConsumeMPTime(System.currentTimeMillis());
         }
      }

      if (this.getPlayer().hasBuffBySkillID(35141501) && System.currentTimeMillis() - this.lastGroundZeroTime > 0L) {
         this.lastGroundZeroTime = System.currentTimeMillis();
         this.getPlayer().send(CField.userBonusAttackRequest(35141501, true, Collections.emptyList()));
      }
   }

   public void removeSummon(Summoned s) {
      if (s.getSkill() == 400051068) {
         this.mecaCarrierNum = 0;
      }
   }

   public void startMultipleOptionTask() {
      if (this.multipleOptionTask == null) {
         this.multipleOptionTask = Timer.BuffTimer.getInstance().register(() -> {
            if (this.lastMultipleOptionTime != 0L && System.currentTimeMillis() - this.lastMultipleOptionTime >= 1500L) {
               try {
                  for (Summoned summon : this.getPlayer().getSummonsReadLock()) {
                     if (summon.getSkill() == 400051009) {
                        this.getPlayer().send(CField.summonAssistAttackRequest(this.getPlayer().getId(), summon.getObjectId(), 0));
                        this.lastMultipleOptionTime = System.currentTimeMillis();
                        break;
                     }
                  }
               } finally {
                  this.getPlayer().unlockSummonsReadLock();
               }
            }

            if (this.lastMultipleOptionTime == 0L) {
               this.lastMultipleOptionTime = System.currentTimeMillis();
            }
         }, 500L);
      }
   }

   public void cancelMultipleOptionTask() {
      if (this.multipleOptionTask != null) {
         this.multipleOptionTask.cancel(true);
         this.multipleOptionTask = null;
      }
   }
}
