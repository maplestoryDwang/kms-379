package objects.users.jobs.adventure.archer;

import constants.GameConstants;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Archer;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class PathFinder extends Archer {
   private long lastActiveRelicUnboundTime = 0L;
   private int lastCardinalForceAttack = 0;

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
      if (attack.skillID == 3301008) {
         int count = 2;
         List<MapleMapObject> objs = this.getPlayer()
            .getMap()
            .getMapObjectsInRange(this.getPlayer().getTruePosition(), 640000.0, Arrays.asList(MapleMapObjectType.MONSTER));
         List<Integer> monsters = new ArrayList<>();
         List<Point> positions = new ArrayList<>();
         if (objs.size() > 0) {
            for (int i = 0; i < count; i++) {
               int rand = Randomizer.rand(0, objs.size() - 1);
               if (objs.size() < count) {
                  if (i < objs.size()) {
                     MapleMonster mob = (MapleMonster)objs.get(i);
                     monsters.add(mob.getObjectId());
                     positions.add(mob.getPosition());
                  }
               } else {
                  MapleMonster mob = (MapleMonster)objs.get(rand);
                  monsters.add(mob.getObjectId());
                  positions.add(mob.getPosition());
                  objs.remove(rand);
               }
            }

            for (Integer mon : monsters) {
               if (this.getPlayer().getMap().getMonsterByOid(mon).getStats().isFriendly()) {
                  monsters.remove(mon);
               }
            }

            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            info.initSplitMistelle(positions.get(0));
            ForceAtom forceAtom = new ForceAtom(
               info,
               3301009,
               this.getPlayer().getId(),
               false,
               true,
               this.getPlayer().getId(),
               ForceAtom.AtomType.SPLIT_MISTELLE,
               monsters,
               count,
               monster.getPosition()
            );
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
         }
      }

      if (totalDamage > 0L) {
         if (attack.skillID == 3321006 || attack.skillID == 3321007) {
            MobTemporaryStatEffect eff = monster.getBuff(MobTemporaryStatFlag.ANCIENT_CURSE);
            int stack = 0;
            if (eff != null) {
               stack = eff.getX();
            }

            stack = Math.min(5, stack + 1);
            SecondaryStatEffect effect_ = SkillFactory.getSkill(3320001).getEffect(this.getPlayer().getTotalSkillLevel(3320001));
            if (effect_ != null) {
               monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.ANCIENT_CURSE, stack, 3320001, null, false),
                  false,
                  effect_.getDuration(),
                  false,
                  effect_
               );
            }
         } else if (attack.skillID >= 3321016 && attack.skillID <= 3321020) {
            MobTemporaryStatEffect effx = monster.getBuff(MobTemporaryStatFlag.ANCIENT_CURSE);
            int stackx = 0;
            if (effx != null) {
               stackx = effx.getX();
            }

            SecondaryStatEffect real = this.getPlayer().getSkillLevelData(attack.skillID);
            if (real != null) {
               int x = real.getX();
               stackx = Math.min(5, stackx + x);
               SecondaryStatEffect effect_ = SkillFactory.getSkill(3320001).getEffect(this.getPlayer().getTotalSkillLevel(3320001));
               if (effect_ != null) {
                  monster.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.ANCIENT_CURSE, stackx, 3320001, null, false),
                     false,
                     effect_.getDuration(),
                     false,
                     effect_
                  );
               }
            }
         }

         if (attack.skillID == 3321036) {
            SecondaryStatEffect effxx = SkillFactory.getSkill(3321036).getEffect(attack.skillLevel);
            if (effxx != null && Randomizer.rand(0, 100) <= effxx.getZ()) {
               int count = effxx.getW();
               if (count > 0) {
                  List<MapleMapObject> objs = this.getPlayer()
                     .getMap()
                     .getMapObjectsInRange(this.getPlayer().getTruePosition(), 360000.0, Arrays.asList(MapleMapObjectType.MONSTER));
                  List<Integer> monsters = new ArrayList<>();
                  List<Point> positions = new ArrayList<>();

                  for (int ix = 0; ix < count; ix++) {
                     if (objs.size() > 0) {
                        int rand = Randomizer.rand(0, objs.size() - 1);
                        if (objs.size() < count) {
                           if (ix < objs.size()) {
                              MapleMonster mob = (MapleMonster)objs.get(ix);
                              monsters.add(mob.getObjectId());
                              positions.add(mob.getPosition());
                           }
                        } else {
                           MapleMonster mob = (MapleMonster)objs.get(rand);
                           monsters.add(mob.getObjectId());
                           positions.add(mob.getPosition());
                           objs.remove(rand);
                        }
                     }
                  }

                  if (monsters.size() > 0) {
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initAdditionalDischarge(positions.get(0), 3);
                     ForceAtom forceAtom = new ForceAtom(
                        info,
                        3301009,
                        this.getPlayer().getId(),
                        false,
                        true,
                        this.getPlayer().getId(),
                        ForceAtom.AtomType.EDITIONAL_DISCHARGE,
                        monsters,
                        count,
                        positions.get(0)
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                  }
               }
            }
         }

         if ((GameConstants.isDischargeSkill(attack.skillID) || GameConstants.isBlastSkill(attack.skillID) || GameConstants.isEnchantSkill(attack.skillID))
            && this.getPlayer().getEndAdditionalTransitionTime() > System.currentTimeMillis()) {
            SecondaryStatEffect effxx = SkillFactory.getSkill(3320008).getEffect(this.getPlayer().getTotalSkillLevel(3320008));
            if (effxx != null && effxx.makeChanceResult()) {
               MobTemporaryStatEffect eff2 = monster.getBuff(MobTemporaryStatFlag.ANCIENT_CURSE);
               int stackxx = 0;
               if (eff2 != null) {
                  stackxx = eff2.getX();
               }

               stackxx = Math.min(5, stackxx + 1);
               SecondaryStatEffect effect_ = SkillFactory.getSkill(3320001).getEffect(this.getPlayer().getTotalSkillLevel(3320001));
               if (effect_ != null) {
                  monster.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.ANCIENT_CURSE, stackxx, 3320001, null, false),
                     false,
                     effect_.getDuration(),
                     false,
                     effect_
                  );
               }
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().hasBuffBySkillID(400031051) && this.getPlayer().checkInterval(this.lastActiveRelicUnboundTime, 5000)) {
         for (Summoned summon : new ArrayList<>(this.getPlayer().getSummons())) {
            if (summon.getSkill() == 400031051) {
               this.getPlayer().send(CField.summonBeholderShock(this.getPlayer().getId(), summon.getObjectId(), 0, false));
            }
         }

         this.lastActiveRelicUnboundTime = System.currentTimeMillis();
      }

      if (this.getPlayer().hasBuffBySkillID(400031049) && this.getPlayer().checkInterval(this.lastActiveRelicUnboundTime, 1000)) {
         int level = this.getPlayer().getTotalSkillLevel(400031049) == 0 ? 1 : this.getPlayer().getTotalSkillLevel(400031049);
         SecondaryStatEffect eff = SkillFactory.getSkill(400031049).getEffect(level);
         if (eff != null && this.getPlayer().getActiveRelicUnboundCount() < eff.getX()) {
            for (Summoned summonx : new ArrayList<>(this.getPlayer().getSummons())) {
               if (summonx.getSkill() == 400031049) {
                  this.getPlayer().send(CField.summonBeholderShock(this.getPlayer().getId(), summonx.getObjectId(), 400031049, false));
               }
            }

            this.lastActiveRelicUnboundTime = System.currentTimeMillis();
            this.getPlayer().setActiveRelicUnboundCount(this.getPlayer().getActiveRelicUnboundCount() + 1);
         }
      }

      if (attack.skillID != 400031047 && attack.skillID != 400031048 && this.getPlayer().hasBuffBySkillID(400031047)) {
         System.out.println("adsadads?");
         int level = this.getPlayer().getTotalSkillLevel(400031047) == 0 ? 1 : this.getPlayer().getTotalSkillLevel(400031047);
         SecondaryStatEffect eff = SkillFactory.getSkill(400031047).getEffect(level);
         if (eff != null && this.getPlayer().getBuffedValue(SecondaryStatFlag.RelicUnboundDischarge) == null) {
            this.getPlayer().temporaryStatSet(400031048, eff.getSubTime() / 1000, SecondaryStatFlag.RelicUnboundDischarge, 1);
         }
      }

      switch (attack.skillID) {
         case 3321016:
         case 3321018:
         case 3321020:
            this.getPlayer().send(CField.userBonusAttackRequest(attack.skillID + 1, true, Collections.EMPTY_LIST));
         case 3321017:
         case 3321019:
         default:
            if (GameConstants.isCardinalForceSkill(attack.skillID)) {
               this.getPlayer().handleRelicChargeCon(attack.skillID, -1, attack.targets);
            }

            if (attack.skillID == 3311002 || attack.skillID == 3311003 || attack.skillID == 3321006 || attack.skillID == 3321007) {
               this.getPlayer().setAutoChargeStack(this.getPlayer().getAutoChargeStack() - 1);
               this.getPlayer().temporaryStatSet(3311002, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack, this.getPlayer().getAutoChargeStack());
            }

            if (attack.skillID == 3311011) {
               SecondaryStatEffect eff = SkillFactory.getSkill(3311010).getEffect(attack.skillLevel);
               if (eff != null) {
                  this.getPlayer().handleRelicChargeCon(3311011, eff.getForceCon(), 0);
               }
            }

            if (attack.skillID == 400031035) {
               this.getPlayer().handleRelicChargeCon(attack.skillID, this.getPlayer().getRelicCharge(), 0);
               SecondaryStatEffect eff = SkillFactory.getSkill(400031034).getEffect(attack.skillLevel);
               this.getPlayer().send(CField.skillCooldown(400031034, eff.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(400031034, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
            }

            if (attack.skillID == 400031036) {
               this.getPlayer().handleRelicChargeCon(attack.skillID, -effect.getV(), 0);
            }

            if (this.getPlayer().hasBuffBySkillID(3341501)) {
               if (GameConstants.isCardinalForceSkill(attack.skillID) && this.lastCardinalForceAttack != attack.skillID) {
                  List<MapleMonster> objs = this.getPlayer().getMap().getMobsInRange(attack.forcedPos, 640000.0, 1, true);
                  List<Integer> monsters = new ArrayList<>();

                  for (MapleMonster monster : objs) {
                     monsters.add(monster.getObjectId());
                  }

                  if (!objs.isEmpty()) {
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initForsakenRelic(objs.get(0).getPosition());
                     ForceAtom forceAtom = new ForceAtom(
                        info,
                        3341502,
                        this.getPlayer().getId(),
                        false,
                        true,
                        this.getPlayer().getId(),
                        ForceAtom.AtomType.Forsaken_Relic,
                        monsters,
                        1,
                        attack.forcedPos
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                  }

                  this.lastCardinalForceAttack = attack.skillID;
               }

               List<Integer> list = SkillFactory.getSkill(3341503).getSkillList();
               if (list.contains(attack.skillID) && this.getPlayer().getRemainCooltime(3341503) <= 0L) {
                  this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 3341503);
                  this.getPlayer().send(CField.skillCooldown(3341503, 10000));
                  this.getPlayer().addCooldown(3341503, System.currentTimeMillis(), 10000L);
               }
            }

            if (attack.targets > 0 && attack.skillID == 3341002 && this.getPlayer().getRemainCooltime(3341003) <= 0L) {
               MapleMonster targetMob = null;

               for (AttackPair dmg : attack.allDamage) {
                  targetMob = this.getPlayer().getMap().getMonsterByOid(dmg.objectid);
                  if (targetMob != null) {
                     break;
                  }
               }

               if (targetMob != null) {
                  List<SecondAtom.Atom> atoms = new ArrayList<>();

                  for (int i = 0; i < 6; i++) {
                     int xpos = targetMob.getPosition().x + (ThreadLocalRandom.current().nextInt(330) - 165);
                     int ypos = targetMob.getPosition().y + (ThreadLocalRandom.current().nextInt(330) - 165);
                     Point pos = new Point(xpos, ypos);
                     SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(),
                        this.getPlayer().getId(),
                        3341003,
                        SecondAtom.SN.getAndAdd(1),
                        SecondAtom.SecondAtomType.AdditionalBlastCurseArrow,
                        i,
                        null,
                        pos
                     );
                     SecondAtomData dd = SkillFactory.getSkill(3341003).getSecondAtomData();
                     a.setPlayerID(this.getPlayer().getId());
                     a.setExpire(2000);
                     a.setAttackableCount(1);
                     a.setEnableDelay(dd.getEnableDelay());
                     a.setCreateDelay(dd.getCreateDelay());
                     a.setSkillID(3341003);
                     int atomangle = -130 + ThreadLocalRandom.current().nextInt(260);
                     a.setAngle(atomangle);
                     this.getPlayer().addSecondAtom(a);
                     atoms.add(a);
                  }

                  if (atoms.size() > 0) {
                     SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 3341003, atoms);
                     this.getPlayer().getMap().createSecondAtom(secondAtom);
                  }

                  this.getPlayer().send(CField.skillCooldown(3341003, 20000));
                  this.getPlayer().addCooldown(3341003, System.currentTimeMillis(), 20000L);
               }
            }

            super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
      }
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      switch (this.getActiveSkillPrepareID()) {
         case 3321038:
         case 3321040:
            SecondaryStatEffect eff = SkillFactory.getSkill(this.getActiveSkillPrepareID()).getEffect(this.getActiveSkillPrepareSLV());
            this.getPlayer().temporaryStatSet(this.getActiveSkillPrepareID(), Integer.MAX_VALUE, SecondaryStatFlag.indieDamReduceR, -eff.getIndieDamReduceR());
            break;
         case 400031034:
            Map<SecondaryStatFlag, Integer> flags = new HashMap<>();
            flags.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            flags.put(SecondaryStatFlag.indieBlockSkill, 1);
            this.getPlayer().temporaryStatSet(this.getActiveSkillPrepareID(), this.getActiveSkillPrepareSLV(), Integer.MAX_VALUE, flags);
            this.getPlayer().handleRelicChargeCon(this.getActiveSkillPrepareID(), this.getPlayer().getRelicCharge(), 0);
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (GameConstants.isPathFinder(this.getPlayer().getJob())) {
         this.getPlayer().checkPathfinderPattern(this.getActiveSkillID());
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 3321034:
            this.getPlayer().temporaryStatSet(3321034, effect.getDuration(), SecondaryStatFlag.indieSummon, 1);
            SecondaryStatEffect eff = SkillFactory.getSkill(3300000).getEffect(1);
            if (eff != null) {
               int max = eff.getU();
               this.getPlayer().setRelicCharge(max);
               this.getPlayer().onAncientGuardians(max);
               eff.applyTo(this.getPlayer(), true);
            }

            effect.applyTo(this.getPlayer(), true);
            SecondaryStatEffect eff2 = SkillFactory.getSkill(3311002).getEffect(this.getPlayer().getSkillLevel(3311002));
            SecondaryStatEffect eff3 = SkillFactory.getSkill(3321034).getEffect(this.getPlayer().getSkillLevel(3321034));
            this.getPlayer().setAutoChargeSkillID(3311002);
            this.getPlayer().setAutoChargeMaxStack(eff2.getY());
            this.getPlayer().setAutoChargeCycle(eff2.getS2() * 1000 - eff3.getU() * 1000);
            break;
         case 3341501:
            this.getPlayer().temporaryStatSet(3341501, 30000, SecondaryStatFlag.ForsakenRelic, 1);
            break;
         case 400031037:
         case 400031039:
         case 400031040:
         case 500061018:
         case 500061020:
         case 500061021: {
            Point pos = packet.readPos();
            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), false);
            AffectedArea area = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration());
            area.setStartTime(System.currentTimeMillis());
            this.getPlayer().getMap().spawnMist(area);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 400031047: {
            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            Summoned s = new Summoned(
               this.getPlayer(),
               400031047,
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
         case 400031049: {
            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            Summoned s = new Summoned(
               this.getPlayer(),
               400031049,
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
         case 400031051: {
            int max = effect.getU();
            int x = effect.getX();
            int y = effect.getY();
            new Rect(effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y);
            List<Point> pos = new ArrayList<>();
            pos.add(this.getPlayer().getMap().calcDropPos(this.getPlayer().getTruePosition(), this.getPlayer().getPosition()));
            Point pos1 = pos.get(0);
            if (pos1 == null) {
               return;
            }

            pos1 = new Point(pos.get(0));
            pos1.x -= x;
            pos1.y -= y;
            pos1 = this.getPlayer().getMap().calcDropPos(pos1, pos1);
            if (this.getPlayer().getMap().getLeft() <= pos1.x
               && this.getPlayer().getMap().getRight() >= pos1.x
               && this.getPlayer().getMap().getTop() <= pos1.y
               && this.getPlayer().getMap().getBottom() >= pos1.y) {
               pos.add(pos1);
            }

            Point pos2 = new Point(pos.get(0));
            pos2.x += x;
            pos2.y -= y;
            pos2 = this.getPlayer().getMap().calcDropPos(pos2, pos2);
            if (this.getPlayer().getMap().getLeft() <= pos2.x
               && this.getPlayer().getMap().getRight() >= pos2.x
               && this.getPlayer().getMap().getTop() <= pos2.y
               && this.getPlayer().getMap().getBottom() >= pos2.y) {
               pos.add(pos2);
            }

            for (Point p : pos) {
               Summoned sx = new Summoned(
                  this.getPlayer(),
                  400031051,
                  this.getActiveSkillLevel(),
                  p,
                  SummonMoveAbility.STATIONARY,
                  (byte)0,
                  System.currentTimeMillis() + effect.getDuration()
               );
               this.getPlayer().getMap().spawnSummon(sx, effect.getDuration());
               this.getPlayer().addSummon(sx);
            }

            effect.applyTo(this.getPlayer());
            break;
         }
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() >= 3321035 && this.getActiveSkillID() <= 3321040) {
         this.getPlayer().cancelAncientAstraTask();
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.indieDamReduceR, this.getActiveSkillID());
      }

      super.activeSkillCancel();
   }
}
