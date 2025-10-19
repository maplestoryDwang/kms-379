package objects.users.jobs.adventure.warrior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.SecondAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.summoned.Summoned;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Randomizer;

public class DarkKnight extends DefaultWarrior {
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
      if (boss) {
         this.handleReincarnation(1, boss);
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400011047) {
         Skill s = SkillFactory.getSkill(400011047);
         SecondaryStatEffect e = s.getEffect(this.getPlayer().getTotalSkillLevel(400011047));
         List<SecondAtom.Atom> atoms = new ArrayList<>();
         if (e != null) {
            int max = e.getQ();
            int count = 0;

            for (AttackPair pair : attack.allDamage) {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(pair.objectid);
               if (mob != null) {
                  SecondAtom.Atom a = new SecondAtom.Atom(
                     this.getPlayer().getMap(),
                     this.getPlayer().getId(),
                     400011047,
                     SecondAtom.SN.getAndAdd(1),
                     SecondAtom.SecondAtomType.DarknessAuraDrain,
                     0,
                     null,
                     mob.getTruePosition()
                  );
                  SecondAtomData data = skill.getSecondAtomData();
                  a.setUnk1(1);
                  a.setUnk2(1);
                  a.setPlayerID(this.getPlayer().getId());
                  a.setTargetObjectID(this.getPlayer().getId());
                  a.setExpire(data.getExpire());
                  a.setAngle(Randomizer.rand(data.getFirstAngleStart(), data.getFirstAngleRange()));
                  this.getPlayer().addSecondAtom(a);
                  atoms.add(a);
                  if (++count >= max) {
                     break;
                  }
               }
            }

            SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400011047, atoms);
            this.getPlayer().getMap().createSecondAtom(secondAtom);
            if (this.getPlayer().getDarknessAuraStack() < effect.getS()) {
               this.getPlayer().setDarknessAuraStack(this.getPlayer().getDarknessAuraStack() + 1);
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.DarknessAura, 400011047, 1);
               statManager.temporaryStatSet();
            }
         }
      }

      if (attack.targets > 0) {
         int skillLevel = this.getPlayer().getTotalSkillLevel(1310009);
         if (skillLevel > 0) {
            SecondaryStatEffect effect_ = SkillFactory.getSkill(1310009).getEffect(skillLevel);
            if (effect != null && effect_.makeChanceResult()) {
               int x = effect_.getX();
               int delta = (int)(this.getPlayer().getStat().getMaxHp() * (x * 0.01));
               this.getPlayer().healHP(delta);
            }
         }

         if (this.getPlayer().hasBuffBySkillID(1321054)) {
            SecondaryStatEffect effect_ = SkillFactory.getSkill(1321054).getEffect(1);
            if (effect_ != null) {
               int x = effect_.getX();
               int delta = (int)(this.getPlayer().getStat().getMaxHp() * (x * 0.01));
               this.getPlayer().healHP(delta);
            }
         }

         if (this.getPlayer().skillisCooling(1321015)) {
            Skill skill_ = SkillFactory.getSkill(1321015);
            SecondaryStatEffect effect_ = skill_.getEffect(this.getPlayer().getTotalSkillLevel(1321015));
            if (skill_.getSkillList().contains(attack.skillID)) {
               this.getPlayer().changeCooldown(1321015, (long)(effect_.getT() * -1000.0));
            }
         }

         if (multiKill > 0) {
            this.handleReincarnation(multiKill, false);
         }

         Summoned summon;
         if ((summon = this.getPlayer().getSummonBySkillID(1301013)) != null
            && this.getPlayer().getTotalSkillLevel(1320011) > 0
            && !this.getPlayer().skillisCooling(1320011)) {
            Skill skill_ = SkillFactory.getSkill(1320011);
            SecondaryStatEffect effect_ = skill_.getEffect(this.getPlayer().getTotalSkillLevel(1320011));
            if (skill_.getSkillList().contains(attack.skillID)) {
               List<Integer> attackMobs = new ArrayList<>();
               attack.allDamage.forEach(at -> {
                  if (attackMobs.size() < effect_.getMobCount()) {
                     attackMobs.add(at.objectid);
                  }
               });
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.SUMMON_BEHOLDERS_REVENGE.getValue());
               packet.writeInt(summon.getOwnerId());
               packet.writeInt(summon.getObjectId());
               packet.writeInt(attackMobs.size());
               attackMobs.forEach(packet::writeInt);
               this.getPlayer().send(packet.getPacket());
               this.getPlayer().send(CField.skillCooldown(1320011, effect_.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(1320011, System.currentTimeMillis(), effect_.getCooldown(this.getPlayer()));
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 1311014:
         case 400011054:
            boolean found = false;

            try {
               for (Summoned summon : this.getPlayer().getSummonsReadLock()) {
                  if (summon.getSkill() == 1301013) {
                     this.getPlayer().send(CField.summonBeholderShock(this.getPlayer().getId(), summon.getObjectId(), this.getActiveSkillID()));
                     found = true;
                     break;
                  }
               }
            } finally {
               this.getPlayer().unlockSummonsReadLock();
            }

            if (!found) {
               this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
            }
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400011068) {
         this.getPlayer().temporaryStatResetBySkillID(400011068);
      } else if (this.getActiveSkillID() == 400011047) {
         this.getPlayer().send(CField.userBonusAttackRequest(400011085, true, Collections.EMPTY_LIST, 0, 0));
         SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DarknessAura);
         if (eff != null) {
            int v2 = this.getPlayer().getDarknessAuraStack() / 3;

            for (int i = 0; i < v2; i++) {
               this.getPlayer().send(CField.userBonusAttackRequest(400011085, true, Collections.EMPTY_LIST, 0, 0));
            }
         }
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DarknessAura) != null
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.MaxHP) != null
         && this.getPlayer().checkInterval(this.getPlayer().lastDarknessAuraBarrierTime, 10000)) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DarknessAura);
         if (e != null) {
            Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.indieBarrier);
            int maxHP = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * e.getY());
            if (v != null) {
               maxHP = Math.min(500000, v + maxHP);
            }

            this.getPlayer().temporaryStatSet(400011047, e.getDuration(), SecondaryStatFlag.indieBarrier, maxHP);
            this.getPlayer().lastDarknessAuraBarrierTime = System.currentTimeMillis();
         }
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case Beholder:
            int skillID = 1301013;
            if (this.getPlayer().getSkillLevel(1310013) > 0) {
               skillID = 1310013;
            }

            packet.writeInt(skillID);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public void handleReincarnation(int attackCount, boolean boss) {
      if (this.getPlayer().getReincarnationCount() > 0) {
         SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ReincarnationActivate);
         if (effect != null) {
            if (boss) {
               attackCount = this.getPlayer().getReincarnationCount();
            }

            this.getPlayer().addReincarnationCount(-attackCount);
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.ReincarnationActivate, effect.getSourceId(), this.getPlayer().getReincarnationCount());
            statManager.temporaryStatSet();
            if (this.getPlayer().getReincarnationCount() <= 0) {
               this.getPlayer().changeCooldown(1320019, effect.getY() * -1000L);
            }
         }
      }
   }
}
