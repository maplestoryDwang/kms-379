package objects.users.jobs.adventure.warrior;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.MapleCharacter;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Paladin extends DefaultWarrior {
   private long lastGrandCrossTime = 0L;
   private int holyChargeStack = 0;
   private int lastHolyUnityCharId = 0;
   Map<Integer, Long> devineJudgementTime = new HashMap<>();
   Map<Integer, Integer> devineJudgementStack = new HashMap<>();

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      this.handleHolyCharge(attack.skillID);
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
      if (attack.skillID == 1221019) {
         this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 1221019);
      }

      if (totalDamage > 0L && this.getPlayer().getTotalSkillLevel(1241502) > 0) {
         List<Integer> list = SkillFactory.getSkill(1241502).getSkillList();
         if (list.contains(attack.skillID)) {
            AffectedArea mist = this.getPlayer().getMap().getAffectedAreaBySkillId(1241501, this.getPlayer().getId());
            if (mist != null && this.getPlayer().getRemainCooltime(1241502) <= 0L) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 1241502);
               this.getPlayer().giveCoolDowns(1241502, System.currentTimeMillis(), 500L);
               this.getPlayer().send(CField.skillCooldown(1241502, 500));
            }
         }
      }

      if (attack.targets > 0 && (attack.skillID == 1221009 || attack.skillID == 1241000)) {
         int skillID = 0;
         int attackSkillID = 0;
         if (this.getPlayer().getTotalSkillLevel(1220022) > 0) {
            skillID = 1220022;
            attackSkillID = 1221023;
         }

         if (this.getPlayer().getTotalSkillLevel(1241000) > 0) {
            skillID = 1240001;
            attackSkillID = 1241002;
         }

         for (AttackPair dmg : attack.allDamage) {
            int objId = dmg.objectid;
            this.devineJudgementTime.put(objId, System.currentTimeMillis());
         }

         List<Pair<Integer, Integer>> targets = new ArrayList<>();

         for (Entry<Integer, Long> entry : this.devineJudgementTime.entrySet()) {
            int objId = entry.getKey();
            long time = entry.getValue();
            if (this.getPlayer().getMap().getMonsterByOid(objId) != null && System.currentTimeMillis() <= time + 30000L) {
               int stack = this.devineJudgementStack.getOrDefault(objId, 0) + 1;
               if (stack >= 5) {
                  this.getPlayer().send(CField.attackMonsterStackedDebuffMark(attackSkillID, 5, objId, 202));
                  this.devineJudgementStack.put(objId, 0);
               } else {
                  this.devineJudgementStack.put(objId, Math.min(5, stack));
                  targets.add(new Pair<>(objId, stack));
               }
            }
         }

         this.getPlayer().send(CField.showMonsterStackedDebuffMark(skillID, attackSkillID, targets, 30000, 5));
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 1211013:
            packet.skip(4);
            int mobCount = packet.readByte();

            for (int i = 0; i < mobCount; i++) {
               int mobObjectID = packet.readInt();
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(mobObjectID);
               if (mob != null) {
                  Map<MobTemporaryStatFlag, MobTemporaryStatEffect> statups = new HashMap<>();
                  int w = effect.getW();
                  int z = effect.getZ();
                  int time = effect.getDuration(effect.getDuration(), this.getPlayer());
                  int prop = effect.getProb();
                  w += this.getPlayer().getSkillLevelDataOne(1220045, SecondaryStatEffect::getX);
                  z += this.getPlayer().getSkillLevelDataOne(1220045, SecondaryStatEffect::getY);
                  if (Randomizer.nextInt(100) < prop) {
                     statups.put(MobTemporaryStatFlag.PAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.PAD, w, this.getActiveSkillID(), null, false));
                     statups.put(MobTemporaryStatFlag.MAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.MAD, w, this.getActiveSkillID(), null, false));
                     statups.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, w, this.getActiveSkillID(), null, false));
                     statups.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, w, this.getActiveSkillID(), null, false));
                     mob.applyMonsterBuff(statups, this.getActiveSkillID(), time, null, Collections.EMPTY_LIST);
                     mob.applyStatus(
                        this.getPlayer(),
                        new MobTemporaryStatEffect(MobTemporaryStatFlag.ACC, z, this.getActiveSkillID(), null, false),
                        false,
                        effect.getSubTime(),
                        false,
                        effect
                     );
                  }
               }
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         case 1221054:
            if (this.getPlayer().hasBuffBySkillID(400011003) || this.getPlayer().hasBuffBySkillID(500061000)) {
               MapleCharacter target = this.getPlayer().getMap().getCharacterById(this.lastHolyUnityCharId);
               if (target != null) {
                  long duration = this.getPlayer().getSecondaryStat().getTill(SecondaryStatFlag.HolyUnity) - System.currentTimeMillis();
                  target.temporaryStatSet(1221054, (int)duration, SecondaryStatFlag.indiePartialNotDamaged, 1);
               }
            }
         default:
            super.onActiveSkill(skill, effect, packet);
            break;
         case 1241501:
            Point pos = new Point(packet.readShort(), packet.readShort());
            boolean isLeft = packet.readByte() != 0;
            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), isLeft);
            AffectedArea aa = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration());
            this.getPlayer().getMap().spawnMist(aa);
            break;
         case 400011053:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.BlessedHammerBig, this.getActiveSkillID(), effect.getDuration(), this.holyChargeStack);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 400011131:
            int flag = this.getActiveSkillFlag();
            byte targetCount = packet.readByte();
            List<Integer> targets = new ArrayList<>();

            for (int i = 0; i < targetCount; i++) {
               targets.add(packet.readInt());
            }

            short startDelay = packet.readShort();
            packet.skip(1);
            int lastAttackMaxTaget = packet.readByte();
            if (targets.isEmpty()) {
               return;
            }

            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(targets.get(0));
            if (mob == null) {
               return;
            }

            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            info.initMightyMjolnir(this.getPlayer().getTruePosition(), startDelay, 32768, lastAttackMaxTaget);
            ForceAtom forceAtom = new ForceAtom(
               info, this.getActiveSkillID(), this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.MIGHTY_MJOLNIR, targets, 1
            );
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            this.getPlayer().setAutoChargeStack(this.getPlayer().getAutoChargeStack() - 1);
            this.getPlayer().temporaryStatSet(400011131, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack, this.getPlayer().getAutoChargeStack());
            effect.applyTo(this.getPlayer());
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 1221054) {
         int cooldownReduce = (int)(this.getPlayer().getCooldownLimit(1221054) / 1000L);
         cooldownReduce = (30 - cooldownReduce) / 5 * 45;
         cooldownReduce *= 1000;
         this.getPlayer().changeCooldown(1221054, -cooldownReduce);
         if (this.lastHolyUnityCharId != 0) {
            MapleCharacter target = this.getPlayer().getMap().getCharacterById(this.lastHolyUnityCharId);
            if (target != null) {
               target.temporaryStatResetBySkillID(1221054);
            }
         }
      } else if (this.getActiveSkillID() == 400011003 || this.getActiveSkillID() == 500061000) {
         Integer targetID = this.getPlayer().getBuffedValue(SecondaryStatFlag.HolyUnity);
         if (targetID != null && targetID != 0) {
            MapleCharacter target = this.getPlayer().getMap().getCharacterById(targetID);
            if (target != null) {
               target.temporaryStatResetBySkillID(400011021);
               if (target.hasBuffBySkillID(1221054)) {
                  target.temporaryStatResetBySkillID(1221054);
               }

               this.lastHolyUnityCharId = targetID;
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.HolyUnity) != null) {
            this.getPlayer().temporaryStatResetBySkillID(400011021);
         }
      } else if (this.getActiveSkillID() == 400011072) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.GrandCrossSize);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      this.updateGrandCross();
   }

   public void cleanUpHashMap() {
      this.devineJudgementTime.clear();
      this.devineJudgementStack.clear();
   }

   public void handleHolyCharge(int skillID) {
      if (this.holyChargeStack < 5) {
         this.holyChargeStack++;
      }

      Skill holyCharge = SkillFactory.getSkill(1200014);
      if (holyCharge != null && this.getPlayer().getTotalSkillLevel(1200014) > 0) {
         if (holyCharge.getSkillList().contains(skillID)) {
            SecondaryStatEffect advHolyCharge = this.getPlayer().getSkillLevelData(1220010);
            this.getPlayer()
               .temporaryStatSet(
                  advHolyCharge != null ? 1220010 : 1200014,
                  Integer.MAX_VALUE,
                  SecondaryStatFlag.HolyCharge,
                  this.holyChargeStack * this.getPlayer().getSkillLevelDataOne(advHolyCharge != null ? 1220010 : 1200014, SecondaryStatEffect::getX)
               );
            if (this.getPlayer().getSkillLevel(400011052) > 0) {
               this.getPlayer().temporaryStatSet(400011052, Integer.MAX_VALUE, SecondaryStatFlag.BlessedHammer, this.holyChargeStack);
            }
         }
      }
   }

   private void updateGrandCross() {
      if (this.isActiveGrandCross() && (this.lastGrandCrossTime == 0L || System.currentTimeMillis() - this.lastGrandCrossTime >= 3000L)) {
         long maxHP = this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer());
         int delta = (int)(maxHP * 0.01 * 1.5);
         if (this.getPlayer().getStat().getHp() > delta) {
            this.getPlayer().addHP(-delta);
         }

         this.lastGrandCrossTime = System.currentTimeMillis();
      }
   }

   private boolean isActiveGrandCross() {
      return this.getPlayer().getBuffedValue(SecondaryStatFlag.GrandCrossSize) != null;
   }

   public void blessedHammerReset() {
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.BlessedHammer) != null) {
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.BlessedHammer, 400011052, Integer.MAX_VALUE, 0);
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.BlessedHammer);
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.BlessedHammerBig) != null) {
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.BlessedHammerBig, 400011053, Integer.MAX_VALUE, 0);
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.BlessedHammerBig);
      }

      this.holyChargeStack = 0;
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case HolyCharge:
            SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(flag);
            packet.write(this.holyChargeStack);
            packet.writeShort(effect != null ? effect.getY() * this.holyChargeStack : 0);
            packet.write(effect != null ? effect.getU() * this.holyChargeStack : 0);
            packet.write(effect != null ? effect.getX() * this.holyChargeStack : 0);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
