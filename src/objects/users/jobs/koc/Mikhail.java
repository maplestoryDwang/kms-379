package objects.users.jobs.koc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.jobs.Warrior;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Mikhail extends Warrior {
   private int royalGuardState = 0;
   private int royalGuardStateX = 0;
   private long lastAttackSwordOfLightTime = System.currentTimeMillis();

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
      if (this.isShiningLightSkill(attack.skillID)) {
         int prop = effect.getProb();
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.MichaelSoulLink) != null) {
            SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.MichaelSoulLink);
            if (e != null) {
               prop = e.getU();
            }
         }

         if (Randomizer.isSuccess(prop)) {
            int time = attack.skillID == 51121052 ? effect.getSubTime() : effect.getDuration();
            MobTemporaryStatEffect monsterStatusEffect = new MobTemporaryStatEffect(
               MobTemporaryStatFlag.BLIND, effect.getX(), effect.getSourceId(), null, false
            );
            monster.applyStatus(this.getPlayer(), monsterStatusEffect, false, time, false, effect);
         }
      }

      if (attack.skillID == 51111015) {
         monster.setDivineJudgement(0);
         List<Pair<Integer, Integer>> list = new ArrayList<>();
         list.add(new Pair<>(monster.getObjectId(), monster.getDivineJudgement()));
         this.getPlayer().send(CField.showMonsterStackedDebuffMark(51111015, 51111015, list, 20000, 1));
      }

      if (attack.skillID == 51120057) {
         int duration = effect.getDuration();
         if (monster.getStats().isBoss()) {
            duration /= 2;
         }

         monster.applyStatus(
            this.getPlayer(),
            new MobTemporaryStatEffect(MobTemporaryStatFlag.BLIND, effect.getX(), attack.skillID, null, false),
            false,
            duration,
            false,
            effect
         );
      }

      if (attack.skillID == 400011032) {
         this.lastAttackSwordOfLightTime = System.currentTimeMillis();
      } else if (this.getPlayer().getTotalSkillLevel(400011032) > 0
         && this.getPlayer().getCooldownLimit(400011032) > 0L
         && System.currentTimeMillis() - this.lastAttackSwordOfLightTime >= this.getPlayer().getSkillLevelDataOne(400011032, SecondaryStatEffect::getS) * 1000L
         )
       {
         this.lastAttackSwordOfLightTime = System.currentTimeMillis();
         this.getPlayer().send(CField.userBonusAttackRequest(400011067, true, List.of()));
      }

      if (totalDamage > 0L) {
         if (attack.skillID == 51121052) {
            if (this.getPlayer().getParty() != null) {
               monster.setAddDamPartyPartyID(this.getPlayer().getParty().getId());
            }

            monster.setAddDamPartyC(999999);
            monster.setAddDamPartyFrom(this.getPlayer().getId());
            monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.ADD_DAM_PARTY, effect.getX(), attack.skillID, null, false),
               false,
               effect.getDuration(),
               false,
               effect
            );
         }

         if (attack.skillID == 51121007) {
            int propx = effect.getProb();
            int slv = this.getPlayer().getTotalSkillLevel(51120050);
            if (slv > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(51120050).getEffect(slv);
               if (eff != null) {
                  propx += eff.getProb();
               }
            }

            if (Randomizer.nextInt(100) < propx) {
               monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.BLIND, effect.getX(), attack.skillID, null, false),
                  false,
                  effect.getDuration(),
                  false,
                  effect
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
      if (this.getPlayer().getTotalSkillLevel(51110014) > 0
         && (attack.skillID >= 51001005 && attack.skillID <= 51001013 || attack.skillID == 51111011 || attack.skillID == 51111012)) {
         List<MapleMonster> mobList = new ArrayList<>();
         attack.allDamage.stream().forEach(a -> {
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(a.objectid);
            if (mob != null) {
               mobList.add(mob);
            }
         });
         this.onOffensiveDefense(mobList);
      }

      if (attack.targets > 0) {
         List<MapleMonster> mobList = new ArrayList<>();
         attack.allDamage.forEach(m -> {
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(m.objectid);
            mobList.add(mob);
         });
         this.onOffensiveDefenseActiveAttack(attack.skillID, mobList);
      }

      if (this.getPlayer().hasBuffBySkillID(51121006) && (attack.skillID == 51121007 || attack.skillID == 400011084)) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(51120057);
         if (e != null) {
            AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(51120057, this.getPlayer().getId());
            if (area != null) {
               this.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 51120057, false));
               this.getPlayer().getMap().removeMapObject(area);
            }

            Rect rect = new Rect(attack.attackPosition, e.getLt(), e.getRb(), (attack.display & 32768) != 0);
            AffectedArea aa = new AffectedArea(rect, this.getPlayer(), e, attack.attackPosition, System.currentTimeMillis() + e.getY() * 1000L);
            aa.setRlType((attack.display & 32768) != 0 ? 1 : 0);
            this.getPlayer().getMap().spawnMist(aa);
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   public void onOffensiveDefense(List<MapleMonster> mobList) {
      mobList.forEach(m -> {
         if (m != null) {
            m.setDivineJudgement(1);
         }
      });
      List<Pair<Integer, Integer>> list = new ArrayList<>();
      mobList.stream().forEach(mob -> {
         if (mob != null) {
            list.add(new Pair<>(mob.getObjectId(), mob.getDivineJudgement()));
         }
      });
      this.getPlayer().send(CField.showMonsterStackedDebuffMark(51111015, 51111015, list, 20000, 1));
   }

   public void onOffensiveDefenseActiveAttack(int skillID, List<MapleMonster> mobList) {
      int level = this.getPlayer().getTotalSkillLevel(51110014);
      if (level > 0 && this.isShiningLightSkill(skillID) && this.getPlayer().getCooldownLimit(51110014) <= 0L) {
         SecondaryStatEffect e = SkillFactory.getSkill(51110014).getEffect(level);
         if (e != null) {
            boolean active = false;

            for (MapleMonster mob : mobList) {
               if (mob != null && mob.getDivineJudgement() > 0) {
                  mob.setDivineJudgement(0);
                  boolean advanced = false;
                  if (this.getPlayer().hasBuffBySkillID(51121059)) {
                     advanced = true;
                  }

                  if (!active) {
                     PacketEncoder packet = new PacketEncoder();
                     packet.writeShort(SendPacketOpcode.USER_BONUS_ATTACK_REQUEST.getValue());
                     packet.writeInt(51111015);
                     packet.writeInt(1);
                     packet.write(1);
                     packet.writeInt(0);
                     packet.writeInt(0);
                     packet.writeInt(0);
                     packet.writeInt(mob.getObjectId());
                     packet.writeInt(advanced ? 473 : 405);
                     packet.write(advanced ? 1 : 0);
                     this.getPlayer().send(packet.getPacket());
                     active = true;
                  }

                  List<Pair<Integer, Integer>> list = new ArrayList<>();
                  list.add(new Pair<>(mob.getObjectId(), mob.getDivineJudgement()));
                  this.getPlayer().send(CField.showMonsterStackedDebuffMark(51111015, 51111015, list, 20000, 1));
               }
            }

            if (active) {
               int cooltime = e.getCooldown(this.getPlayer());
               SecondaryStatEffect advancedx = this.getPlayer().getSkillLevelData(51120018);
               if (advancedx != null) {
                  cooltime = advancedx.getCooldown(this.getPlayer());
               }

               if (this.getPlayer().hasBuffBySkillID(51121059)) {
                  SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(51121059);
                  if (eff != null) {
                     cooltime = (int)(eff.getT() * 1000.0);
                  }
               }

               this.getPlayer().send(CField.skillCooldown(51110014, cooltime));
               this.getPlayer().addCooldown(51110014, System.currentTimeMillis(), cooltime);
            }
         }
      }
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 51120057:
            Point pos = new Point(packet.readShort(), packet.readShort());
            packet.skip(4);
            byte flip = packet.readByte();
            AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(51120057, this.getPlayer().getId());
            if (area != null) {
               this.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 51120057, false));
               this.getPlayer().getMap().removeMapObject(area);
            }

            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), flip == 1);
            AffectedArea aa = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getY() * 1000L);
            aa.setRlType(flip);
            this.getPlayer().getMap().spawnMist(aa);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 51121016:
            if (this.getPlayer().hasBuffBySkillID(51121016)) {
               this.getPlayer().temporaryStatResetBySkillID(51121016);
            }

            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            Summoned s = new Summoned(
               this.getPlayer(),
               51121016,
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
         case 51121059:
            this.getPlayer()
               .temporaryStatSet(
                  this.getActiveSkillID(), effect.getDuration(effect.getDuration(), this.getPlayer()), SecondaryStatFlag.indieDamR, effect.getIndieDamR()
               );
            break;
         case 400011127:
         case 500061010:
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indieDamR, effect.getZ());
            statups.put(SecondaryStatFlag.LightOfCourage, 1);
            this.getPlayer()
               .temporaryStatSet(this.getActiveSkillID() + 1, this.getPlayer().getTotalSkillLevel(this.getActiveSkillID()), effect.getY() * 1000, statups);
            effect.applyTo(this.getPlayer());
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      int skillId = 400011127;
      if (this.getPlayer().getTotalSkillLevel(500061010) > 0) {
         skillId = 500061010;
      }

      if (this.getPlayer().hasBuffBySkillID(skillId)) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(skillId);
         if (e != null) {
            int x = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01) * e.getX();
            int w = (int)(x * 0.01) * e.getW();
            Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.indieBarrier);
            if (value != null) {
               value = value - w;
               if (value <= 0) {
                  this.getPlayer().temporaryStatResetBySkillID(skillId);
               } else {
                  SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.indieBarrier, skillId, value);
                  statManager.temporaryStatSet();
               }
            }
         }
      }
   }

   public boolean onCheckRoyalGuard() {
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.RoyalGuardPrepare) != null) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.RoyalGuardPrepare);
         SecondaryStatEffect eff = SkillFactory.getSkill(51001006).getEffect(this.getPlayer().getTotalSkillLevel(51001005));
         if (eff != null) {
            this.getPlayer().temporaryStatSet(51001006, eff.getS() * 1000, SecondaryStatFlag.indiePartialNotDamaged, 1);
         }

         eff = SkillFactory.getSkill(51120003).getEffect(this.getPlayer().getTotalSkillLevel(51120003));
         if (eff != null) {
            this.getPlayer().temporaryStatSet(51120003, eff.getDuration(), SecondaryStatFlag.ReduceFixDamR, eff.getY());
         }

         this.onSetRoyalGuard(true);
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ROYAL_GUARD_ATTACK.getValue());
         packet.write(0);
         this.getPlayer().send(packet.getPacket());
         return true;
      } else {
         return false;
      }
   }

   public void onSetRoyalGuard(boolean inc) {
      SecondaryStatEffect eff = SkillFactory.getSkill(51001005).getEffect(this.getPlayer().getTotalSkillLevel(51001005));
      if (eff != null) {
         int max = 3;
         SecondaryStatEffect e = SkillFactory.getSkill(51110009).getEffect(this.getPlayer().getTotalSkillLevel(51110009));
         if (e != null) {
            max = 5;
         }

         int delta = 1;
         if (!inc) {
            delta = -1;
         }

         int value = Math.max(0, Math.min(max, this.royalGuardStateX + delta));
         this.royalGuardStateX = value;
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.MichaelSoulLink) != null && this.getPlayer().getParty() != null) {
            SecondaryStatEffect soulLink = this.getPlayer().getBuffedEffect(SecondaryStatFlag.MichaelSoulLink);
            if (soulLink != null) {
               for (MapleCharacter player : this.getPlayerInArea(soulLink)) {
                  if (player.getId() != this.getPlayer().getId()) {
                     int duration = (int)(soulLink.getT() * 1000.0);
                     int barrier = (int)(player.getStat().getCurrentMaxHp(player) * 0.01 * soulLink.getU());
                     player.temporaryStatSet(SecondaryStatFlag.indieBarrier, 51111016, duration, soulLink.getLevel(), barrier);
                  }
               }
            }
         }

         if (value > 0) {
            double term = eff.getDuration(eff.getX(), this.getPlayer());
            int state;
            switch (value) {
               case 1:
                  state = eff.getDOTTime();
                  break;
               case 2:
                  state = eff.getDOTInterval();
                  break;
               case 3:
                  state = eff.getRange();
                  break;
               case 4:
                  state = e.getW2();
                  break;
               case 5:
                  state = e.getU();
                  break;
               default:
                  state = 0;
            }

            this.royalGuardState = state;
            this.getPlayer().temporaryStatSet(51001005, (int)(term * 1000.0), SecondaryStatFlag.RoyalGuardState, value);
            this.getPlayer().temporaryStatSet(51001011, (int)(term * 1000.0), SecondaryStatFlag.indiePAD, state);
         } else {
            this.royalGuardState = 0;
            this.royalGuardStateX = 0;
         }
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case RoyalGuardState:
            packet.writeInt(this.royalGuardState);
            packet.writeInt(this.royalGuardStateX);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public boolean isShiningLightSkill(int skillID) {
      switch (skillID) {
         case 51001004:
         case 51101005:
         case 51111006:
         case 51121009:
         case 51121017:
         case 51121052:
         case 51141000:
         case 51141001:
         case 400011084:
         case 400011087:
            return true;
         default:
            return false;
      }
   }
}
