package objects.users.jobs.hero;

import constants.GameConstants;
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
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.Warrior;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;

public class Aran extends Warrior {
   private long lastActiveInstallMaha = 0L;
   private short saveCombo = 0;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID != 400011016
         && attack.skillID != 500061039
         && attack.skillID != 400011020
         && attack.skillID != 500061040
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.InstallMaha) != null) {
         int skillID = 400011016;
         int bonusAttack = 400011020;
         if (this.getPlayer().getTotalSkillLevel(500061039) > 0) {
            skillID = 500061039;
            bonusAttack = 500061040;
         }

         SecondaryStatEffect e = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getSkillLevel(skillID));
         if (this.lastActiveInstallMaha == 0L || System.currentTimeMillis() - this.lastActiveInstallMaha >= e.getX() * 1000) {
            this.getPlayer().send(CField.userBonusAttackRequest(bonusAttack, true, Collections.EMPTY_LIST));
            this.lastActiveInstallMaha = System.currentTimeMillis();
         }
      }

      if (attack.skillID == 21121057) {
         this.getPlayer().temporaryStatSet(21121057, 2000, SecondaryStatFlag.indiePartialNotDamaged, 1);
         SkillFactory.getSkill(21121068).getEffect(this.getPlayer().getSkillLevel(21121068)).applyTo(this.getPlayer());
      }

      if (GameConstants.isAranSwingSkill(attack.skillID)) {
         int skill_ = 0;
         if (this.getPlayer().getTotalSkillLevel(21100015) > 0) {
            skill_ = 21100015;
         }

         if (this.getPlayer().getTotalSkillLevel(21120021) > 0) {
            skill_ = 21120021;
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
      if (attack.skillID == 400011121 || attack.skillID == 500004083) {
         if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
            monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false),
               false,
               effect.getDuration(),
               false,
               effect
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

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.BlizzardTempest) != null
         && monster.getBlizzardTempestStack() > 0
         && attack.skillID != 400011122
         && attack.skillID != 400011123
         && attack.skillID != 21120018
         && attack.skillID != 21120023
         && attack.skillID != 21120062) {
         this.getPlayer().send(CField.userBonusAttackRequest(400011122, true, Collections.EMPTY_LIST, 0, 0));
      }

      if (GameConstants.isAeroSwingSkill(attack.skillID) && this.getPlayer().getJob() >= 2111 && this.getPlayer().getJob() <= 2112) {
         this.getPlayer().send(CField.setSlowDown());
      }

      if (totalDamage > 0L) {
         if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.AranDrain) != null) {
            SecondaryStatEffect eff_ = this.getPlayer().getBuffedEffect(SecondaryStatFlag.AranDrain);
            int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * eff_.getX());
            this.getPlayer().healHP(hp);
         }

         if (attack.skillID == 21100002) {
            SecondaryStatEffect eff = SkillFactory.getSkill(21100002).getEffect(this.getPlayer().getTotalSkillLevel(21101011));
            if (eff.makeChanceResult()) {
               monster.applyStatus(
                  this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, 21100002, null, false), false, eff.getDuration(), false, eff
               );
            }
         }

         if (attack.skillID == 21100013) {
            SecondaryStatEffect eff = SkillFactory.getSkill(21100013).getEffect(this.getPlayer().getTotalSkillLevel(21101017));
            if (eff.makeChanceResult()) {
               monster.applyStatus(
                  this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, 21100013, null, false), false, eff.getDuration(), false, eff
               );
            }
         }

         if (this.getPlayer().hasBuffBySkillID(21101006)) {
            SecondaryStatEffect eff = SkillFactory.getSkill(21101006).getEffect(this.getPlayer().getTotalSkillLevel(21101006));
            if (eff != null && !monster.isBuffed(MobTemporaryStatFlag.SPEED)) {
               monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, eff.getX(), eff.getSourceId(), null, false),
                  false,
                  eff.getY() * 1000,
                  true,
                  eff
               );
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400010071) {
         this.getPlayer()
            .send(
               CField.getRegisterExtraSkill(attack.skillID, attack.forcedPos.x, attack.forcedPos.y, (attack.display & 32768) != 0, skill.getExtraSkillInfo(), 1)
            );
      }

      if (attack.skillID == 400011121) {
         SecondaryStatEffect e = SkillFactory.getSkill(400011123).getEffect(attack.skillLevel);
         if (e != null) {
            e.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID == 21121029 && this.getPlayer().hasBuffBySkillID(21121029)) {
         this.getPlayer().temporaryStatResetBySkillID(21121029);
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 21121029) {
         this.getPlayer().temporaryStatSet(21121029, 5000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      if (this.getActiveSkillPrepareID() == 21120019 || this.getActiveSkillPrepareID() == 21120026) {
         this.getPlayer().temporaryStatSet(21120019, 5000, SecondaryStatFlag.AranHuntersTargetingCharge, 1);
      }

      if (this.getActiveSkillPrepareID() == 21120027
         || this.getActiveSkillPrepareID() == 21120023
         || this.getActiveSkillPrepareID() == 21120019
         || this.getActiveSkillPrepareID() == 21120026
         || this.getActiveSkillPrepareID() == 21120018
         || this.getActiveSkillPrepareID() == 21121029) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.AdrenalinBoost);
         if (e != null) {
            if (this.getActiveSkillPrepareID() == 21120023 || this.getActiveSkillPrepareID() == 21120026) {
               this.getPlayer().setAdrenalinBoostCount(1);
            } else if (this.getPlayer().getAdrenalinBoostCount() > 0) {
               this.getPlayer().setAdrenalinBoostCount(0);
            }

            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.AdrenalinBoost, e.getSourceId(), e.getSourceId() == 21110016 ? e.getW() : 1);
            statManager.temporaryStatSet();
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 21120019) {
         this.getPlayer().temporaryStatResetBySkillID(21120019);
      }

      super.activeSkillCancel();
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 21141500) {
         int duration = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel()).getDuration();
         Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
         statups.put(SecondaryStatFlag.AdrenalinBoost, 150);
         statups.put(SecondaryStatFlag.AdrenalineSurge, 2);
         statups.put(SecondaryStatFlag.Stance, 100);
         this.getPlayer().temporaryStatSet(this.getActiveSkillID(), this.getActiveSkillLevel(), duration, statups, false);
         this.saveCombo = this.getPlayer().getCombo();
         this.getPlayer().setCombo((short)0);
         this.getPlayer().send(CField.aranCombo(0));
      }

      if (this.getActiveSkillID() == 21141506) {
         SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.indiePartialNotDamaged, this.getActiveSkillID());
         if (eff == null) {
            SecondaryStatEffect effect = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            this.handleSixJobNoDeathTime(this.getActiveSkillID(), effect);
         }

         this.getPlayer().temporaryStatReset(SecondaryStatFlag.AdrenalineSurge);
         this.getPlayer().setCombo(this.saveCombo);
         this.getPlayer().send(CField.aranCombo(this.saveCombo));
         this.saveCombo = 0;
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.BlizzardTempest) != null) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.BlizzardTempest);
         if (e != null) {
            SecondaryStatEffect e2 = this.getPlayer().getSkillLevelData(400011121);
            if (e2 != null) {
               List<MapleMonster> mobs = this.getPlayer()
                  .getMap()
                  .getMobsInRect(this.getPlayer().getPosition(), e.getLt().x, e.getLt().y, e.getRb().x, e.getRb().y, this.getPlayer().isFacingLeft());
               int i = 0;
               if (mobs.size() > 0) {
                  for (MapleMonster mob : mobs) {
                     if (++i > e2.getRange()) {
                        break;
                     }

                     int max = e2.getY();
                     int x = e2.getX();
                     int stack = mob.getBlizzardTempestStack();
                     stack = Math.min(max, stack + 1);
                     mob.setBlizzardTempestStack(stack);
                     if (mob.getBlizzardTempestEndTime() == 0L) {
                        mob.setBlizzardTempestEndTime(System.currentTimeMillis() + x * 1000);
                     }

                     int remain = (int)(mob.getBlizzardTempestEndTime() - System.currentTimeMillis());
                     PacketEncoder packet = new PacketEncoder();
                     packet.writeShort(SendPacketOpcode.BLIZZARD_TEMPEST_DEBUFF.getValue());
                     packet.writeInt(1);
                     packet.writeInt(mob.getObjectId());
                     packet.writeInt(stack);
                     packet.writeInt(remain);
                     this.getPlayer().send(packet.getPacket());
                  }
               }
            }
         }

         for (MapleMonster mob : this.getPlayer().getMap().getAllMonstersThreadsafe()) {
            if (mob.isAlive() && mob.getBlizzardTempestEndTime() != 0L && mob.getBlizzardTempestEndTime() <= System.currentTimeMillis()) {
               mob.setBlizzardTempestEndTime(0L);
               mob.setBlizzardTempestStack(0);
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.BLIZZARD_TEMPEST_DEBUFF.getValue());
               packet.writeInt(1);
               packet.writeInt(mob.getObjectId());
               packet.writeInt(0);
               packet.writeInt(0);
               this.getPlayer().send(packet.getPacket());
            }
         }
      }
   }
}
