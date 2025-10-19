package objects.users.jobs.adventure.warrior;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;

public class Hero extends DefaultWarrior {
   public int valhallaCount = 0;
   private long lastSwordOfBurningSoulTime = 0L;
   private long lastComboInstinctTime = 0L;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (opcode != RecvPacketOpcode.SHOOT_ATTACK) {
         int numFinisherOrbs = 0;
         Integer comboBuff = this.getPlayer().getBuffedValue(SecondaryStatFlag.Combo);
         if (attack.skillID == 1101012 || attack.skillID == 1111014) {
            SecondaryStatEffect eff = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);
            if (eff.makeChanceResult()) {
               for (AttackPair ap : attack.allDamage) {
                  MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
                  if (mob != null && !mob.getStats().isBoss()) {
                     mob.applyStatus(
                        this.getPlayer(),
                        new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, attack.skillID, null, false),
                        false,
                        eff.getDuration(),
                        true,
                        eff
                     );
                  }
               }

               if (comboBuff != null && attack.skillID == 1101012 && attack.targets > 0) {
                  this.handleOrbgain(attack.skillID);
               }
            }
         }

         if (this.getPlayer().hasBuffBySkillID(1111003)) {
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(1111003);
            if (eff.makeChanceResult()) {
               for (AttackPair apx : attack.allDamage) {
                  MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(apx.objectid);
                  Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
                  if (mob != null) {
                     int duration = eff.getV() * 1000;
                     mse.put(MobTemporaryStatFlag.PAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.PAD, eff.getW(), 1111003, null, false));
                     mse.put(MobTemporaryStatFlag.BLIND, new MobTemporaryStatEffect(MobTemporaryStatFlag.BLIND, eff.getX(), 1111003, null, false));
                     mob.applyMonsterBuff(mse, 1111003, duration, null, Collections.EMPTY_LIST);
                  }
               }
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
      if (attack.skillID == 1121015) {
         monster.setIncizingFrom(this.getPlayer().getId());
         if (this.getPlayer().getParty() != null) {
            monster.setIncizingPartyID(this.getPlayer().getParty().getId());
            monster.setIncizingPartyValue(effect.getU());
         }

         monster.applyStatus(
            this.getPlayer(),
            new MobTemporaryStatEffect(MobTemporaryStatFlag.INCIZING, effect.getX(), attack.skillID, null, false),
            false,
            effect.getDuration(),
            false,
            effect
         );
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().hasBuffBySkillID(400011001) && attack.skillID != 0 && System.currentTimeMillis() - this.lastSwordOfBurningSoulTime >= 1000L) {
         int oid = 0;

         try {
            for (Summoned summon : this.getPlayer().getSummonsReadLock()) {
               if (summon.getSkill() == 400011001) {
                  oid = summon.getObjectId();
                  break;
               }
            }
         } finally {
            this.getPlayer().unlockSummonsReadLock();
         }

         this.getPlayer().getMap().broadcastMessage(CField.summonAssistAttackRequest(this.getPlayer().getId(), oid, 0));
         this.lastSwordOfBurningSoulTime = System.currentTimeMillis();
      }

      if (this.getPlayer().hasBuffBySkillID(400011073)
         && (attack.skillID == 1120017 || attack.skillID == 1121008 || attack.skillID == 1141001)
         && (this.lastComboInstinctTime == 0L || System.currentTimeMillis() - this.lastComboInstinctTime >= 500L)) {
         this.getPlayer().send(CField.userBonusAttackRequest(400011074, true, Collections.EMPTY_LIST));
         this.getPlayer().send(CField.userBonusAttackRequest(400011075, true, Collections.EMPTY_LIST));
         this.getPlayer().send(CField.userBonusAttackRequest(400011076, true, Collections.EMPTY_LIST));
         this.lastComboInstinctTime = System.currentTimeMillis();
      }

      if (attack.skillID == 400011027) {
         this.getPlayer()
            .temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 400011027, 1680, this.getPlayer().getSkillLevel(attack.skillID), 1, false, true);
      }

      if (attack.skillID != 1100002 && attack.skillID != 1120013 && this.getPlayer().getBuffedValue(SecondaryStatFlag.Combo) != null) {
         this.handleOrbgain(attack.skillID);
      }

      if (this.getPlayer().hasBuffBySkillID(1121054)) {
         Skill sk = SkillFactory.getSkill(1121054);
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(1121054);
         if (sk.getSkillList().contains(attack.skillID) && this.valhallaCount < e.getU2()) {
            this.valhallaCount++;

            for (int i = 0; i < e.getW(); i++) {
               Summoned summonx = new Summoned(
                  this.getPlayer(), 1121055, 1, attack.attackPosition, SummonMoveAbility.STATIONARY, (byte)((attack.display & 32768) > 0 ? 1 : 0), 2147483647L
               );
               this.getPlayer().getMap().spawnSummon(summonx, 10000);
               this.getPlayer().addSummon(summonx);
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public final void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      if (this.getActiveSkillID() == 1121054) {
         this.valhallaCount = 0;
      }

      super.onActiveSkill(skill, effect, packet);
   }

   public final void handleOrbgain(int skillID) {
      if (skillID != 1111008 && skillID != 1121010) {
         if (skillID < 400011074 || skillID > 400011076) {
            int orbcount = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.Combo, 0);
            Skill combo = SkillFactory.getSkill(1101013);
            Skill advcombo = SkillFactory.getSkill(1120003);
            Skill combosynergy = SkillFactory.getSkill(1110013);
            SecondaryStatEffect ceffect = null;
            int advComboSkillLevel = this.getPlayer().getSkillLevel(advcombo);
            int ComboSynergySkillLevel = this.getPlayer().getSkillLevel(combosynergy);
            if (ComboSynergySkillLevel > 0) {
               ceffect = combosynergy.getEffect(ComboSynergySkillLevel);
            } else {
               ceffect = combo.getEffect(this.getPlayer().getSkillLevel(combo));
            }

            int addComboProp = ceffect.getProb();
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ComboInstinct) != null) {
               addComboProp -= 50;
            }

            int maxCombo = ceffect.getX();
            if (advComboSkillLevel > 0) {
               maxCombo = advcombo.getEffect(advComboSkillLevel).getX();
            }

            if (skillID == 1121054 || skillID == 400011001) {
               this.getPlayer().temporaryStatSet(combo.getId(), Integer.MAX_VALUE, SecondaryStatFlag.Combo, 11);
            } else if ((skillID == 400011125 || skillID == 400011126) && this.getPlayer().getBuffedValue(SecondaryStatFlag.Combo) != null) {
               SecondaryStatEffect effect = SkillFactory.getSkill(400011124).getEffect(this.getActiveSkillLevel());
               if (effect != null) {
                  this.getPlayer()
                     .temporaryStatSet(
                        effect.getSourceId(), effect.getDuration(), SecondaryStatFlag.indiePMDR, effect.getU() * effect.getX() - effect.getY() * (orbcount - 1)
                     );
               }
            }

            if (skillID != 1121054 && skillID != 400011001 && ThreadLocalRandom.current().nextInt(101) < addComboProp && orbcount < maxCombo + 1) {
               int neworbcount = orbcount + 1;
               int doubleComboChance = 0;
               if (advComboSkillLevel > 0) {
                  doubleComboChance += advcombo.getEffect(advComboSkillLevel).getProb();
                  if (this.getPlayer().getSkillLevel(1120044) > 0) {
                     doubleComboChance += 20;
                  }

                  if (Randomizer.nextInt(100) < doubleComboChance && neworbcount < advcombo.getEffect(advComboSkillLevel).getX() + 1) {
                     neworbcount++;
                  }
               }

               this.getPlayer().temporaryStatSet(combo.getId(), Integer.MAX_VALUE, SecondaryStatFlag.Combo, neworbcount);
            }
         }
      }
   }
}
