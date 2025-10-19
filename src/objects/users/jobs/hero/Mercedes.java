package objects.users.jobs.hero;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.SecondAtom;
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
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class Mercedes extends Archer {
   private long lastActiveRoyalKnightsTime = 0L;
   private long lastActiveElemGhostTime = 0L;
   Map<Integer, Long> ishtarsRingTime = new HashMap<>();
   Map<Integer, Integer> ishtarsRingStack = new HashMap<>();

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.bAddAttackProc) {
         SecondaryStatEffect ignisRoarEff = this.getPlayer().getSkillLevelData(23110004);
         if (ignisRoarEff != null) {
            Integer val = this.getPlayer().getBuffedValue(SecondaryStatFlag.PMDR);
            if (val == null) {
               val = 0;
            }

            val = Math.min(ignisRoarEff.getY(), val + 1);
            int duration = ignisRoarEff.getSubTime();
            Map<SecondaryStatFlag, Integer> localstatups = new HashMap<>(ignisRoarEff.getStatups());
            localstatups.put(SecondaryStatFlag.PMDR, val);
            localstatups.put(SecondaryStatFlag.Booster, -val);
            this.getPlayer().temporaryStatSet(23110004, ignisRoarEff.getLevel(), duration, localstatups);
         }

         if (this.getPlayer().getCooldownLimit(23111002) != 0L) {
            this.getPlayer().changeCooldown(23111002, -1000L);
         }

         if (this.getPlayer().getCooldownLimit(23121002) != 0L) {
            this.getPlayer().changeCooldown(23121002, -1000L);
         }

         if (this.getPlayer().getCooldownLimit(23121052) != 0L) {
            this.getPlayer().changeCooldown(23121052, -1000L);
         }

         if (this.getPlayer().getCooldownLimit(400031007) != 0L) {
            this.getPlayer().changeCooldown(400031007, -1000L);
         }

         if (this.getPlayer().getCooldownLimit(500061046) != 0L) {
            this.getPlayer().changeCooldown(500061046, -1000L);
         }
      }

      this.getPlayer().setLastUseSkill(attack.skillID);
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
      if (attack.skillID == 23111002 && effect.makeChanceResult()) {
         monster.setAddDamPartyC(999999);
         monster.setAddDamPartyFrom(this.getPlayer().getId());
         monster.applyStatus(
            this.getPlayer(),
            new MobTemporaryStatEffect(MobTemporaryStatFlag.ADD_DAM_PARTY, effect.getX(), 23111002, null, false),
            false,
            effect.getDuration(),
            false,
            effect
         );
      }

      if (totalDamage <= 0L) {
      }

      if (attack.skillID == 23121002) {
         int x = effect.getX();
         if (this.getPlayer().getTotalSkillLevel(23120050) > 0) {
            SecondaryStatEffect eff = SkillFactory.getSkill(23120050).getEffect(this.getPlayer().getTotalSkillLevel(23120050));
            if (eff != null) {
               x += eff.getX();
            }
         }

         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
         mse.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, x, attack.skillID, null, false));
         mse.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, x, attack.skillID, null, false));
         monster.applyMonsterBuff(mse, attack.skillID, effect.getDuration(), null, Collections.EMPTY_LIST);
      }

      if (attack.skillID == 23121052 && monster.getStats().isBoss()) {
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
         mse.put(MobTemporaryStatFlag.DODGE_BODY_ATTACK, new MobTemporaryStatEffect(MobTemporaryStatFlag.DODGE_BODY_ATTACK, 1, attack.skillID, null, false));
         monster.applyMonsterBuff(mse, attack.skillID, 60000L, null, Collections.EMPTY_LIST);
      }

      if (attack.skillID == 23141000 && this.getPlayer().hasBuffBySkillID(23110004)) {
         SecondaryStatEffect ignisRoarEff = this.getPlayer().getSkillLevelData(23110004);
         Map<SecondaryStatFlag, Integer> localstatups = new HashMap<>(ignisRoarEff.getStatups());
         localstatups.put(SecondaryStatFlag.PMDR, this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.PMDR, 0));
         localstatups.put(SecondaryStatFlag.Booster, this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.Booster, 0));
         int duration = (int)(this.getPlayer().getSecondaryStat().PMDRTill - System.currentTimeMillis());
         this.getPlayer().temporaryStatSet(23110004, ignisRoarEff.getLevel(), duration + 100, localstatups);
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   private void spawnElementalKnight() {
      SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.Elemental_Knights);
      int finalSkillID = 23111008;
      finalSkillID += Randomizer.nextInt(3) + 1;
      if (this.getPlayer().getMap().getSummonCount(this.getPlayer(), finalSkillID) == 0 && finalSkillID != 23111010) {
         SecondaryStatEffect summonEffect = SkillFactory.getSkill(finalSkillID).getEffect(e.getLevel());
         SummonMoveAbility summonMovementType = summonEffect.getSummonMovementType();
         long duration = summonEffect.getDuration(summonEffect.getDuration(), this.getPlayer());
         long summonRemoveTime = System.currentTimeMillis() + duration;
         Point point = new Point(this.getPlayer().getTruePosition());
         Summoned tosummon = new Summoned(
            this.getPlayer(),
            finalSkillID,
            e.getLevel(),
            this.getPlayer().getMap().calcDropPos(point, this.getPlayer().getTruePosition()),
            summonMovementType,
            (byte)0,
            summonRemoveTime
         );
         this.getPlayer().getMap().spawnSummon(tosummon, (int)duration);
         this.getPlayer().addSummon(tosummon);
         tosummon.addHP(summonEffect.getX());
      }
   }

   private Skill spawnRoyalKnights(Skill skill) {
      SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.RoyalKnights);
      if (e != null && this.getPlayer().checkInterval(this.lastActiveRoyalKnightsTime, (int)(e.getT() * 1000.0))) {
         int s = e.getS();
         int count = 0;
         SecondaryStatEffect e2 = SkillFactory.getSkill(400031045).getEffect(e.getLevel());
         if (e2 != null) {
            List<SecondAtom.Atom> atoms = new ArrayList<>();

            for (MapleMonster mob : this.getPlayer()
               .getMap()
               .getMobsInRect(this.getPlayer().getTruePosition(), e2.getLt().x, e2.getLt().y, e2.getRb().x, e2.getRb().y)) {
               SecondAtom.Atom a = new SecondAtom.Atom(
                  this.getPlayer().getMap(),
                  this.getPlayer().getId(),
                  400031045,
                  SecondAtom.SN.getAndAdd(1),
                  SecondAtom.SecondAtomType.RoyalKnights,
                  0,
                  null,
                  mob.getTruePosition()
               );
               skill = SkillFactory.getSkill(400031045);
               if (skill != null) {
                  SecondAtomData data = skill.getSecondAtomData();
                  a.setPlayerID(this.getPlayer().getId());
                  a.setTargetObjectID(mob.getObjectId());
                  a.setExpire(data.getExpire());
                  a.setSkillID(400031045);
                  a.setAttackableCount(data.getAttackableCount());
                  a.setAngle(Randomizer.rand(data.getFirstAngleStart(), data.getFirstAngleRange()));
                  this.getPlayer().addSecondAtom(a);
                  atoms.add(a);
               }

               if (++count >= s) {
                  break;
               }
            }

            SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400031045, atoms);
            this.getPlayer().getMap().createSecondAtom(secondAtom);
            this.lastActiveRoyalKnightsTime = System.currentTimeMillis();
         }
      }

      return skill;
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.targets > 0 && this.getPlayer().getBuffedEffect(SecondaryStatFlag.Elemental_Knights) != null) {
         int count = 0;

         for (int i = 23111009; i <= 23111011; i++) {
            count += this.getPlayer().getMap().getSummonCount(this.getPlayer(), i);
         }

         if (count < 2) {
            this.spawnElementalKnight();
         }
      }

      if (attack.targets > 0 && this.getPlayer().getJob() == 2312 && this.getPlayer().getBuffedValue(SecondaryStatFlag.RoyalKnights) != null) {
         skill = this.spawnRoyalKnights(skill);
      }

      if ((attack.skillID == 23121000 || attack.skillID == 400031024) && attack.targets > 0) {
         Integer val = this.getPlayer().getBuffedValue(SecondaryStatFlag.PMDR);
         if (val != null) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(23110004);
            if (e != null) {
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.PMDR, 23110004, val);
               statManager.changeStatValue(SecondaryStatFlag.Booster, 23110004, -val);
               statManager.changeTill(SecondaryStatFlag.PMDR, 23110004, attack.skillID == 400031024 ? 300 : 100);
               statManager.changeTill(SecondaryStatFlag.Booster, 23110004, attack.skillID == 400031024 ? 300 : 100);
               statManager.temporaryStatSet();
            }
         }
      }

      if (this.getPlayer().hasBuffBySkillID(400031007) || this.getPlayer().hasBuffBySkillID(500061046)) {
         int skillID = 400031007;
         if (this.getPlayer().hasBuffBySkillID(500061046)) {
            skillID = 500061046;
         }

         SecondaryStatEffect eff = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getSkillLevel(skillID));
         if (this.lastActiveElemGhostTime == 0L || System.currentTimeMillis() - this.lastActiveElemGhostTime >= eff.getS2() * 1000) {
            this.getPlayer().send(CField.userBonusAttackRequest(skillID == 400031007 ? 400031011 : 500061049, true, Collections.EMPTY_LIST));
            this.lastActiveElemGhostTime = System.currentTimeMillis();
         }
      }

      if (this.getPlayer().getTotalSkillLevel(23141000) > 0) {
         List<Integer> list = SkillFactory.getSkill(23141000).getSkillList();
         List<Integer> list2 = SkillFactory.getSkill(23141000).getSkillList2();
         if (list.contains(attack.skillID)) {
            int incStack = 2;
            if (list2.contains(attack.skillID)) {
               incStack = 1;
            }

            int skillIDx = 23141000;
            int attackSkillID = 23141001;
            int attackSkillID2 = 23141002;

            for (AttackPair dmg : attack.allDamage) {
               int objId = dmg.objectid;
               this.ishtarsRingTime.put(objId, System.currentTimeMillis());
            }

            List<Pair<Integer, Integer>> targets = new ArrayList<>();

            for (Entry<Integer, Long> entry : this.ishtarsRingTime.entrySet()) {
               int objId = entry.getKey();
               long time = entry.getValue();
               if (this.getPlayer().getMap().getMonsterByOid(objId) != null && System.currentTimeMillis() <= time + 90000L) {
                  int stack = this.ishtarsRingStack.getOrDefault(objId, 0);
                  if (stack >= 60) {
                     this.getPlayer().send(CField.attackMonsterStackedDebuffMark(attackSkillID2, 0, objId, 600));
                     this.ishtarsRingStack.put(objId, 0);
                  } else {
                     int beforeCheck = stack / 12;
                     int afterCheck = (stack + incStack) / 12;
                     if (afterCheck > beforeCheck) {
                        this.getPlayer().send(CField.attackMonsterStackedDebuffMark(attackSkillID, 0, objId, 0));
                     }

                     this.ishtarsRingStack.put(objId, Math.min(60, stack + incStack));
                     targets.add(new Pair<>(objId, stack / 12));
                  }
               }
            }

            this.getPlayer().send(CField.showMonsterStackedDebuffMark(skillIDx, attackSkillID, attackSkillID2, targets, 90000, 5));
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 23141501:
            Point pos = packet.readPos();
            byte flip = packet.readByte();
            Summoned summon = this.getPlayer().getSummonBySkillID(23141501);
            if (summon == null) {
               summon = new Summoned(
                  this.getPlayer(),
                  23141501,
                  this.getActiveSkillLevel(),
                  pos,
                  SummonMoveAbility.STATIONARY,
                  flip,
                  System.currentTimeMillis() + effect.getDuration()
               );
            } else {
               this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
               this.getPlayer().getMap().removeMapObject(summon);
               this.getPlayer().removeVisibleMapObject(summon);
               this.getPlayer().removeSummon(summon);
               summon.setPosition(pos);
               summon.setSummonRLType(flip);
            }

            this.getPlayer().getMap().spawnSummon(summon, effect.getDuration(), true, false);
            this.getPlayer().addSummon(summon);
            break;
         case 400031007:
         case 500061046:
            pos = packet.readPos();
            flip = packet.readByte();

            for (int i = 0; i < 3; i++) {
               summon = new Summoned(
                  this.getPlayer(),
                  this.getActiveSkillID() + i,
                  this.getActiveSkillLevel(),
                  pos,
                  SummonMoveAbility.SHADOW_SERVANT,
                  flip,
                  System.currentTimeMillis() + effect.getDuration()
               );
               this.getPlayer().getMap().spawnSummon(summon, effect.getDuration(), i == 0, false);
               this.getPlayer().addSummon(summon);
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         case 400031044:
            this.getPlayer().temporaryStatSet(400031044, 2000, SecondaryStatFlag.indiePartialNotDamaged, 1);
            effect.applyTo(this.getPlayer(), true);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400031044) {
         this.getPlayer().temporaryStatSet(400031044, 1000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      super.activeSkillCancel();
   }

   public void cleanUpHashMap() {
      this.ishtarsRingTime.clear();
      this.ishtarsRingStack.clear();
   }
}
