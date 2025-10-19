package objects.users.jobs.resistance;

import constants.GameConstants;
import java.awt.Point;
import java.util.EnumMap;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleStat;
import objects.users.PlayerStats;
import objects.users.jobs.Warrior;
import objects.users.skills.ExceedInfo;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;

public class DemonAvenger extends Warrior {
   private long lastDiabolicRecoveryTime = 0L;
   private long lastRevenantTime = 0L;
   private int lifeTidalM = 0;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (GameConstants.isDemonAvenger(this.getPlayer().getJob())) {
         this.tryApplyExceed(attack.skillID);
      }

      if (attack.skillID == 400011038) {
         this.getPlayer().temporaryStatSet(attack.skillID, effect.getW2(), SecondaryStatFlag.indieDamReduceR, -effect.getW());
         PlayerStats stat = this.getPlayer().getStat();
         Map<MapleStat, Long> hpmpupdate = new EnumMap<>(MapleStat.class);
         int hpchange = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * (effect.getHpR() * 0.01));
         stat.setHp(stat.getHp() + hpchange, this.getPlayer());
         hpmpupdate.put(MapleStat.HP, stat.getHp());
         this.getPlayer().getClient().getSession().writeAndFlush(CWvsContext.updatePlayerStats(hpmpupdate, false, this.getPlayer()));
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
      if (attack.skillID == 31221002) {
         monster.applyStatus(
            this.getPlayer(),
            new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, effect.getY(), attack.skillID, null, false),
            false,
            effect.getDuration(),
            false,
            effect
         );
      }

      if (totalDamage > 0L && attack.skillID == 31221003) {
         if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(attack.skillID);
            if (e != null) {
               monster.applyStatus(
                  this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false), false, e.getDuration(), false, e
               );
               monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, null, 0);
               this.getPlayer().giveCoolDowns(31221003, System.currentTimeMillis(), e.getCooldown(this.getPlayer()));
               this.getPlayer().send(CField.skillCooldown(31221003, e.getCooldown(this.getPlayer())));
            }
         } else {
            this.getPlayer()
               .send(
                  MobPacket.monsterResist(
                     monster,
                     this.getPlayer(),
                     (int)((monster.getResistSkill(MobTemporaryStatFlag.FREEZE) - System.currentTimeMillis()) / 1000L),
                     attack.skillID,
                     2
                  )
               );
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.targets > 0 && attack.skillID >= 400011062 && attack.skillID <= 400011064) {
         SecondaryStatEffect e = SkillFactory.getSkill(attack.skillID).getEffect(attack.skillLevel);
         if (e != null) {
            int x = e.getX();
            int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * x);
            this.getPlayer().healHP(hp);
         }
      }

      if (attack.skillID == 31221052) {
         Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.OverloadCount);
         int overloadCount = 0;
         if (v != null) {
            overloadCount = v;
         }

         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(30010230);
         int max = e.getX() - this.getPlayer().getSkillLevelDataOne(31220044, SecondaryStatEffect::getX);
         overloadCount = Math.min(max, overloadCount + effect.getX());
         this.getPlayer().temporaryStatSet(30010230, Integer.MAX_VALUE, SecondaryStatFlag.OverloadCount, overloadCount);
         this.getPlayer().addMP(effect.getX(), true);
      }

      if (attack.targets > 0 && totalDamage > 0L) {
         int slv = 0;
         if ((slv = this.getPlayer().getTotalSkillLevel(31010002)) > 0) {
            SecondaryStatEffect eff = SkillFactory.getSkill(31010002).getEffect(slv);
            if (eff != null && eff.makeChanceResult()) {
               SecondaryStatEffect exceed = SkillFactory.getSkill(30010230).getEffect(this.getPlayer().getTotalSkillLevel(30010230));
               if (exceed != null) {
                  SecondaryStatEffect frenzyEffect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DemonFrenzy);
                  int x = eff.getX();
                  int exceedOverload = this.getPlayer().getSecondaryStat().ExceedOverloadValue;
                  SecondaryStatEffect painDampener = null;
                  SecondaryStatEffect advAbsorbLife = null;
                  if ((slv = this.getPlayer().getTotalSkillLevel(31210005)) > 0) {
                     painDampener = SkillFactory.getSkill(31210005).getEffect(slv);
                  }

                  if ((slv = this.getPlayer().getTotalSkillLevel(31210006)) > 0) {
                     advAbsorbLife = SkillFactory.getSkill(31210006).getEffect(slv);
                  }

                  if (advAbsorbLife != null) {
                     x = advAbsorbLife.getX();
                  }

                  int dec = 0;
                  if (painDampener != null) {
                     dec = painDampener.getX();
                  }

                  int delta = exceedOverload / exceed.getZ() * -exceed.getY() + dec;
                  delta = Math.max(0, Math.min(x, x + delta));
                  if ((delta <= 0 || frenzyEffect != null) && (frenzyEffect == null || this.getPlayer().getStat().getHPPercent() > frenzyEffect.getQ2())) {
                     int frenzyLevel = this.getPlayer().getTotalSkillLevel(400011010);
                     if (frenzyLevel == 30) {
                        this.getPlayer().addHP((int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.02));
                     } else {
                        this.getPlayer().addHP((int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01));
                     }
                  } else {
                     this.getPlayer().addHP((int)(delta * this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01));
                  }
               }
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 400011010 && this.getPlayer().hasBuffBySkillID(400011010)) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.DemonFrenzy);
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.indiePMDR);
         this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
      } else {
         super.beforeActiveSkill(packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      switch (this.getActiveSkillID()) {
         case 31011000:
         case 31201000:
         case 31211000:
         case 31221000:
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.Exceed);
         default:
            super.activeSkillCancel();
      }
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 31241501:
            Point pos = packet.readPos();
            byte flip = packet.readByte();
            Summoned summon = new Summoned(
               this.getPlayer(),
               31241501,
               this.getActiveSkillLevel(),
               pos,
               SummonMoveAbility.STATIONARY,
               flip,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(summon, effect.getDuration(), true, false);
            this.getPlayer().addSummon(summon);
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getJob() >= 3121 && this.getPlayer().getJob() <= 3122 && this.getPlayer().getStat().getHp() > 0L) {
         this.updateDiabolicRecovery();
      }

      if (this.getPlayer().hasBuffBySkillID(400011010)) {
         int lostHpPercent = this.getPlayer().getTotalSkillLevel(400011010) < 25
            ? (100 - this.getPlayer().getStat().getHPPercent()) / 3
            : (100 - this.getPlayer().getStat().getHPPercent()) / 2;
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.indiePMDR, 400011010);
         if (lostHpPercent > 20) {
            lostHpPercent = 20;
         }

         this.getPlayer().temporaryStatSet(400011010, Integer.MAX_VALUE, SecondaryStatFlag.indiePMDR, lostHpPercent);
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Revenant) != null
         && this.getPlayer().getRevenantRage() > 0
         && this.getPlayer().checkInterval(this.lastRevenantTime, 2000)) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.Revenant);
         if (e != null) {
            int q2 = e.getQ2();
            int v = (int)(this.getPlayer().getRevenantRage() * 0.01 * q2);
            this.getPlayer().setRevenantRage(Math.max(0, this.getPlayer().getRevenantRage() - v));
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            int skillID = 400011112;
            if (this.getPlayer().getTotalSkillLevel(500061054) > 0) {
               skillID = 500061054;
            }

            statManager.changeStatValue(SecondaryStatFlag.Revenant, skillID, 1);
            statManager.temporaryStatSet();
            this.lastRevenantTime = System.currentTimeMillis();
         }
      }
   }

   private void updateDiabolicRecovery() {
      SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DiabolicRecovery);
      if (eff != null
         && (this.lastDiabolicRecoveryTime == 0L || System.currentTimeMillis() - this.lastDiabolicRecoveryTime >= eff.getW() * 1000)
         && this.getPlayer().isAlive()) {
         int regenHP = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * (eff.getX() / 100.0));
         this.getPlayer().addHP(regenHP);
         this.lastDiabolicRecoveryTime = System.currentTimeMillis();
      }
   }

   public void initDemonAvenger() {
      if (GameConstants.isDemonAvenger(this.getPlayer().getJob())) {
         this.lifeTidalM = (int)this.getPlayer().getStat().getHp();
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.LifeTidal, 0, Integer.MAX_VALUE, 1, 3, false, true, false);
      }
   }

   public void tryApplyExceed(int skillID) {
      if (GameConstants.isDemonAvenger(this.getPlayer().getJob())) {
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(30010230);
         if (effect != null) {
            Skill skill = SkillFactory.getSkill(skillID);
            if (skill != null) {
               int exceedSkillParent = 0;
               if ((exceedSkillParent = skill.getExceedSkillParent()) != 0) {
                  skillID = exceedSkillParent;
                  skill = SkillFactory.getSkill(exceedSkillParent);
               }

               ExceedInfo exceedInfo = null;
               if (skill != null) {
                  exceedInfo = skill.getExceedInfo();
               }

               if (exceedInfo != null) {
                  SecondaryStat ss = this.getPlayer().getSecondaryStat();
                  if (ss != null) {
                     Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.Exceed);
                     int exceed = 0;
                     if (v != null) {
                        exceed = v;
                     }

                     if (exceed >= 0 && exceed <= exceedInfo.getMax()) {
                        int time = 15000;
                        boolean exceedChanged = exceed > 0 && skillID != ss.ExceedReason;
                        exceed = Math.min(exceedInfo.getMax(), exceed + 1);
                        this.getPlayer().temporaryStatSet(skill.getId(), time, SecondaryStatFlag.Exceed, exceed);
                        v = this.getPlayer().getBuffedValue(SecondaryStatFlag.OverloadCount);
                        int overloadCount = 0;
                        if (v != null) {
                           overloadCount = v;
                        }

                        int max = effect.getX() - this.getPlayer().getSkillLevelDataOne(31220044, SecondaryStatEffect::getX);
                        overloadCount = Math.min(max, overloadCount + 1 + (exceedChanged ? effect.getW() : 0));
                        this.getPlayer().temporaryStatSet(30010230, Integer.MAX_VALUE, SecondaryStatFlag.OverloadCount, overloadCount);
                     }
                  }
               }
            }
         }
      }
   }

   public void summonedChangeSkillRequest(int skillID) {
      if (skillID == 400011090) {
         int newsid = 400011102;
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(newsid);
         if (effect != null) {
            Summoned summoned = this.getPlayer().getSummonBySkillID(skillID);
            if (summoned == null) {
               return;
            }

            SecondaryStatEffect e = SkillFactory.getSkill(skillID).getEffect(summoned.getSkillLevel());
            if (e != null) {
               long span = summoned.getSummonRemoveTime() - System.currentTimeMillis();
               int ms = (int)(span / e.getQ());
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.indieSummon);
               this.getPlayer().temporaryStatSet(newsid, ms, SecondaryStatFlag.DevilishPower, 1);
            }
         }
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case LifeTidal:
            packet.writeInt(this.lifeTidalM);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
