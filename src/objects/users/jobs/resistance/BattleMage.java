package objects.users.jobs.resistance;

import java.awt.Point;
import java.util.ArrayList;
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
import objects.effect.child.SpecialSkillEffect;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.jobs.Magician;
import objects.users.skills.CrystalGate;
import objects.users.skills.DarkLigntningEntry;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_PointWithDirection;
import objects.users.skills.TeleportAttackData_Quad;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class BattleMage extends Magician {
   private long lastDrainAuraTime = 0L;
   private long lastRecoveryDrainAuraTime = 0L;
   private long lastBMageDeathTime = 0L;

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
      if (attack.skillID != 32110020 && attack.skillID != 400021113 && this.getPlayer().getJob() >= 3211 && this.getPlayer().getJob() <= 3212) {
         if (attack.skillID != 32111016 && attack.skillID != 400021088 && attack.skillID != 400021047) {
            DarkLigntningEntry darkEntry = this.getPlayer().getDarkLightning(monster.getObjectId());
            if (darkEntry != null) {
               if (darkEntry.getRemainCount() <= 0) {
                  this.getPlayer().removeDarkLightning(monster.getObjectId());
               } else {
                  this.getPlayer().send(CField.userBonusAttackRequest(32110020, false, Collections.singletonList(new Pair<>(monster.getObjectId(), 1)), 0));
                  if (this.getPlayer().getBuffedValue(SecondaryStatFlag.AbyssalLightning) != null) {
                     this.attackDarkLightningThunder(monster);
                  }

                  darkEntry.setRemainCount(darkEntry.getRemainCount() - 1);
               }
            }
         } else {
            int count = 15;
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.AbyssalLightning) != null) {
               count = 30;
               this.attackDarkLightningThunder(monster);
            }

            this.getPlayer().addDarkLightning(monster.getObjectId(), count);
         }
      }

      if (totalDamage > 0L
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.DrainAura) != null
         && this.getPlayer().hasBuffBySkillID(32101009)
         && this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.DrainAura) == this.getPlayer().getId()
         && !monster.isAlive()) {
         SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DrainAura);
         if (eff != null) {
            int recoveryR = eff.getKillRecoveryR();
            int delta = (int)(recoveryR * (this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01));
            this.getPlayer().addHP(delta);
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400021007) {
         int q = effect.getQ();
         this.getPlayer().addMP(-q);
      }

      if (totalDamage > 0L) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(32100008);
         if (eff != null && Randomizer.nextInt(100) < 20) {
            int recoveryMp;
            if (boss) {
               recoveryMp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) / 100L * 3L);
            } else {
               recoveryMp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) / 100L * 5L);
            }

            this.getPlayer().addMP(recoveryMp);
         }
      }

      if (attack.targets > 0 && boss) {
         Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
         SecondaryStatEffect bossKiller = SkillFactory.getSkill(32120060).getEffect(this.getPlayer().getSkillLevel(32120060));
         statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(bossKiller.getIndieBdR()));
         this.getPlayer().temporaryStatSet(32120060, bossKiller.getLevel(), 500, statups);
         this.applyBMDeath();
      }

      if (attack.targets > 0) {
         SecondaryStatEffect death_eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.BMageDeath);
         if (death_eff != null
            && attack.skillID != 32001014
            && attack.skillID != 32141000
            && attack.skillID != 32100010
            && attack.skillID != 32110017
            && attack.skillID != 32120019) {
            if (this.getPlayer().hasBuffBySkillID(32121056)) {
               this.lastBMageDeathTime -= 500L;
            }

            for (int i = 0; i < multiKill; i++) {
               this.applyBMDeath();
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DrainAura) != null) {
            SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DrainAura);
            if (eff != null
               && eff.getSourceId() == 32101009
               && (this.lastDrainAuraTime == 0L || System.currentTimeMillis() - this.lastDrainAuraTime >= eff.getSubTime())) {
               int x = eff.getX();
               int delta = (int)(x * (totalDamage * 0.01));
               int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * 15.0);
               int d = Math.min(hp, delta);
               this.getPlayer().addHP(d);
               if (this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.DrainAura) == this.getPlayer().getId()
                  && this.getPlayer().getParty() != null
                  && this.getPlayer().getParty().getPartyMemberList().size() > 1) {
                  for (MapleCharacter player : this.getPlayer()
                     .getMap()
                     .getPlayerInRect(this.getPlayer().getPosition(), eff.getLt().x, eff.getLt().y, eff.getRb().x, eff.getRb().y)) {
                     if (player.getId() != this.getPlayer().getId()
                        && player.getParty() != null
                        && player.getParty().getId() == this.getPlayer().getParty().getId()) {
                        hp = (int)(player.getStat().getCurrentMaxHp(player) * 0.01 * 15.0);
                        d = Math.min(hp, delta);
                        player.addHP(d);
                     }
                  }
               }

               this.lastDrainAuraTime = System.currentTimeMillis();
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 400021047:
            int x = packet.readShort();
            int y = packet.readShort();
            packet.skip(4);
            byte isLeft = packet.readByte();
            packet.skip(2);
            boolean double_ = packet.readByte() == 1;
            int count = 1;
            Summoned s = new Summoned(
               this.getPlayer(),
               400021047,
               this.getActiveSkillLevel(),
               new Point(x, y),
               SummonMoveAbility.STATIONARY,
               isLeft,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(s, effect.getDuration());
            this.getPlayer().addSummon(s);
            if (double_) {
               count = 2;
               Point pos = new Point(x, y);
               if (isLeft == 1) {
                  pos.x -= 500;
               } else {
                  pos.x += 500;
               }

               pos = this.getPlayer().getMap().calcDropPos(pos, pos);
               Summoned s2 = new Summoned(
                  this.getPlayer(),
                  400021047,
                  this.getActiveSkillLevel(),
                  pos,
                  SummonMoveAbility.STATIONARY,
                  isLeft,
                  System.currentTimeMillis() + effect.getDuration()
               );
               this.getPlayer().getMap().spawnSummon(s2, effect.getDuration());
               this.getPlayer().addSummon(s2);
            }

            int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.AutoChargeStack, -1) - count;
            this.getPlayer().temporaryStatSet(400021047, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack, value);
            this.getPlayer().setAutoChargeStack(value);
            effect.applyTo(this.getPlayer());
            break;
         case 400021087:
            this.getPlayer().temporaryStatSet(400021087, 2000, SecondaryStatFlag.indiePartialNotDamaged, 1);
            this.getPlayer().temporaryStatSet(400021087, effect.getDuration(), SecondaryStatFlag.AbyssalLightning, 1);
            effect.applyTo(this.getPlayer());
            break;
         case 400021088:
            List<TeleportAttackElement> elements = this.teleportAttackAction.actions;
            List<Integer> removes = new ArrayList<>();

            for (TeleportAttackElement e : elements) {
               if (e.type == 9) {
                  TeleportAttackData_Quad quad = (TeleportAttackData_Quad)e.data;
                  if (quad != null) {
                     int objectID = quad.getObjectID();
                     removes.add(objectID);
                     this.getPlayer().removeCrystalGate(objectID);
                  }
               }
            }

            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.REMOVE_CRYSTAL_GATE.getValue());
            p.writeInt(this.getPlayer().getId());
            p.writeInt(removes.size());

            for (int r : removes) {
               p.writeInt(r);
            }

            this.getPlayer().send(p.getPacket());
            if (elements.size() >= 2) {
               p = new PacketEncoder();
               TeleportAttackData_PointWithDirection data = (TeleportAttackData_PointWithDirection)elements.get(0).data;
               p.writeInt(data.x);
               p.writeInt(data.y);
               TeleportAttackData_Quad data2 = (TeleportAttackData_Quad)elements.get(1).data;
               p.writeInt(data2.x);
               p.writeInt(data2.y);
               SpecialSkillEffect eff = new SpecialSkillEffect(this.getPlayer().getId(), this.getActiveSkillID(), p);
               this.getPlayer().send(eff.encodeForLocal());
               this.getPlayer().getMap().broadcastMessage(this.getPlayer(), eff.encodeForRemote(), false);
            }
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   public void skillUpdatePerTick(int skillID, PacketDecoder slea) {
      int size = 0;
      if (slea.available() >= 4L) {
         size = slea.readInt();
      }

      if (skillID == 400021089) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400021087).getEffect(this.getPlayer().getTotalSkillLevel(400021087));
         if (effect == null) {
            return;
         }

         for (CrystalGate gate : new ArrayList<>(this.getPlayer().getCrystalGates())) {
            if (System.currentTimeMillis() - gate.getCreateTime() >= gate.getRemainDuration()) {
               this.getPlayer().removeCrystalGate(gate.getTotalCount());
            }
         }

         if (this.getPlayer().getCrystalGates().size() >= effect.getU()) {
            return;
         }

         List<CrystalGate> gates = new ArrayList<>();

         for (int i = 0; i < size; i++) {
            int crystalGateNum = this.getPlayer().getCrystalGates().size();
            if (crystalGateNum >= effect.getU()) {
               break;
            }

            CrystalGate gatex = new CrystalGate(
               this.getPlayer().getId(),
               crystalGateNum + 1,
               skillID,
               this.getPlayer().getMap().getId(),
               slea.readInt(),
               slea.readInt(),
               effect.getV() * 1000,
               CrystalGate.GateType.AbyssalLightning
            );
            gates.add(gatex);
            this.getPlayer().addCrystalGate(gatex);
         }

         this.getPlayer().send(CField.createCrystalGate(this.getPlayer().getId(), gates));
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getJob() >= 3210 && this.getPlayer().getJob() <= 3212) {
         int slv = 0;
         if ((slv = this.getPlayer().getTotalSkillLevel(32101009)) > 0) {
            SecondaryStatEffect eff = SkillFactory.getSkill(32101009).getEffect(slv);
            if (eff != null
               && this.getPlayer().isAlive()
               && (this.lastRecoveryDrainAuraTime == 0L || System.currentTimeMillis() - this.lastRecoveryDrainAuraTime >= 4000L)) {
               int hp = eff.getHp();
               this.getPlayer().addHP(hp);
               this.lastRecoveryDrainAuraTime = System.currentTimeMillis();
            }
         }

         if (this.getPlayer().hasBuffBySkillID(32101009)
            && this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.DrainAura) == this.getPlayer().getId()) {
            SecondaryStatEffect eff = SkillFactory.getSkill(32101009).getEffect(slv);
            if (eff != null) {
               this.getPlayer().addMP(-eff.getMPCon());
            }
         }

         if (this.getPlayer().hasBuffBySkillID(32001016)
            && this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.YellowAura) == this.getPlayer().getId()) {
            SecondaryStatEffect eff = SkillFactory.getSkill(32001016).getEffect(slv);
            if (eff != null) {
               this.getPlayer().addMP(-eff.getMPCon());
            }
         }

         if (this.getPlayer().hasBuffBySkillID(32111012)
            && this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.BlueAura) == this.getPlayer().getId()) {
            SecondaryStatEffect eff = SkillFactory.getSkill(32111012).getEffect(slv);
            if (eff != null) {
               this.getPlayer().addMP(-eff.getMPCon());
               SecondaryStatEffect e = this.getPlayer().getSkillLevelData(32120062);
               if (e != null && this.getPlayer().checkInterval(this.getPlayer().lastBlueAuraDispelTime, e.getDuration()) && this.getPlayer().getParty() != null
                  )
                {
                  for (MapleCharacter pUser : this.getPlayer().getPartyMembers()) {
                     SecondaryStat secondaryStat = pUser.getSecondaryStat();
                     if (pUser.hasBuffBySkillID(32111012) && secondaryStat.getVarriableInt("BlueAuraDispelCount") == 0) {
                        SecondaryStatManager statManager = new SecondaryStatManager(pUser.getClient(), secondaryStat);
                        statManager.changeStatValue(SecondaryStatFlag.BlueAura, 32111012, e.getLevel());
                        secondaryStat.setVarriableInt("BlueAuraFromID", this.getPlayer().getId());
                        secondaryStat.setVarriableInt("BlueAuraTargetID", pUser.getId());
                        secondaryStat.setVarriableInt("BlueAuraDispelCount", 1);
                        statManager.temporaryStatSet();
                     }
                  }
               }
            }
         }

         if (this.getPlayer().hasBuffBySkillID(32121018)
            && this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.DebuffAura) == this.getPlayer().getId()) {
            SecondaryStatEffect eff = SkillFactory.getSkill(32121018).getEffect(slv);
            if (eff != null) {
               this.getPlayer().addMP(-eff.getMPCon());
            }
         }

         if (this.getPlayer().hasBuffBySkillID(32121017)
            && this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.DarkAura) == this.getPlayer().getId()) {
            SecondaryStatEffect eff = SkillFactory.getSkill(32121017).getEffect(slv);
            if (eff != null) {
               this.getPlayer().addMP(-eff.getMPCon());
            }
         }

         if (this.getPlayer().hasBuffBySkillID(32121018) || this.getPlayer().hasBuffBySkillID(400021006)) {
            int skillID = 32121018;
            SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getSkillLevel(skillID));
            if (effect != null && this.getPlayer().getCooldownLimit(skillID) == 0L) {
               if (this.getPlayer().hasBuffBySkillID(400021006)
                  && this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.UnionAuraBlow) == this.getPlayer().getId()) {
                  this.getPlayer().addMP(-effect.getMPCon());
               }

               List<MapleMonster> monsters = this.getPlayer()
                  .getMap()
                  .getMobsInRect(this.getPlayer().getPosition(), effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y);
               monsters.forEach(
                  mob -> Timer.BuffTimer.getInstance()
                     .schedule(
                        () -> {
                           if (mob.getStatusSourceID(MobTemporaryStatFlag.PDR) != skillID) {
                              mob.applyStatus(
                                 this.getPlayer(),
                                 new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, -effect.getX(), skillID, null, false),
                                 false,
                                 effect.getDuration(),
                                 false,
                                 effect
                              );
                              if (this.getPlayer().getSkillLevel(32120061) > 0) {
                                 MobTemporaryStatEffect mobEffect = new MobTemporaryStatEffect(MobTemporaryStatFlag.TRUE_SIGHT, 1, skillID, null, false);
                                 if (this.getPlayer().getParty() != null) {
                                    mobEffect.setW(1);
                                    mobEffect.setP(this.getPlayer().getParty().getId());
                                 } else {
                                    mobEffect.setW(0);
                                    mobEffect.setP(0);
                                 }

                                 mobEffect.setC(this.getPlayer().getId());
                                 mob.applyStatus(this.getPlayer(), mobEffect, false, effect.getDuration(), false, effect);
                                 mob.applyStatus(
                                    this.getPlayer(),
                                    new MobTemporaryStatEffect(MobTemporaryStatFlag.MULTI_PMDR, -effect.getS(), skillID, null, false),
                                    false,
                                    effect.getDuration(),
                                    false,
                                    effect
                                 );
                              }
                           }
                        },
                        effect.getY() * 1000L
                     )
               );
               if (!monsters.isEmpty()) {
                  this.getPlayer().giveCoolDowns(skillID, System.currentTimeMillis(), effect.getCooldown(this.getPlayer()));
               }
            }
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.UnionAuraBlow) != null) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.UnionAuraBlow);
         if (e != null) {
            int q = e.getMPCon();
            this.getPlayer().addMP(-q);
         }
      }
   }

   public void applyBMDeath() {
      SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.BMageDeath);
      if (effect != null) {
         int x = effect.getX();
         if (this.getPlayer().hasBuffBySkillID(32121056)) {
            x = 1;
         }

         if (this.lastBMageDeathTime == 0L || System.currentTimeMillis() - this.lastBMageDeathTime >= effect.getTime() * 1000) {
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.BMageDeath) >= x) {
               int oid = 0;

               try {
                  for (Summoned summon : this.getPlayer().getSummonsReadLock()) {
                     if (summon.getSkill() == effect.getSourceId()) {
                        oid = summon.getObjectId();
                        break;
                     }
                  }
               } finally {
                  this.getPlayer().unlockSummonsReadLock();
               }

               this.getPlayer().getMap().broadcastMessage(CField.summonAssistAttackRequest(this.getPlayer().getId(), oid, 0));
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.BMageDeath, effect.getSourceId(), x);
               statManager.temporaryStatSet();
               this.lastBMageDeathTime = System.currentTimeMillis();
            } else {
               this.addBMDeathCount();
            }
         }
      }
   }

   public void addBMDeathCount() {
      SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
      statManager.changeStatValue(SecondaryStatFlag.BMageDeath, 0, this.getPlayer().getBuffedValue(SecondaryStatFlag.BMageDeath) + 1);
      statManager.temporaryStatSet();
   }

   public void attackDarkLightningThunder(MapleMonster monster) {
      if (!this.getPlayer().skillisCooling(400021113)) {
         SecondaryStatEffect thunder = SkillFactory.getSkill(400021113).getEffect(this.getPlayer().getSkillLevel(400021087));
         this.getPlayer().addCooldown(400021113, System.currentTimeMillis(), thunder.getSubTime() / 1000);
         List<MapleMonster> bonusMob = this.getPlayer()
            .getMap()
            .getMobsInRect(monster.getPosition(), thunder.getLt().x, thunder.getLt().y, thunder.getRb().x, thunder.getRb().y);
         List<Pair<Integer, Integer>> newarray = new ArrayList<>();

         for (MapleMonster mob : bonusMob) {
            if (!mob.getStats().isFriendly()) {
               newarray.add(new Pair<>(mob.getObjectId(), 0));
               if (thunder.getMobCount() <= newarray.size()) {
                  break;
               }
            }
         }

         this.getPlayer().send(CField.userBonusAttackRequest(400021113, false, newarray, 0));
      }
   }
}
