package objects.users.jobs.nova;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import objects.effect.EffectHeader;
import objects.effect.NormalEffect;
import objects.fields.ForceAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.Summoned;
import objects.users.jobs.Pirate;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Randomizer;

public class AngelicBuster extends Pirate {
   public long lastUpdateFamilarTime = 0L;
   public long familiarDeleteTime = 0L;
   private int trinityM = 0;
   private List<Integer> offStateSkills = new ArrayList<>();
   private int soulRechargeFailed = 0;
   private int soulRechargeKillMob = 0;
   private boolean useMascotFamiliar = false;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 400051011) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.EnergyBurst);
         int v = 1;
         if (value != null) {
            v = value;
         }

         int energyBurstValue = v - 1;
         int time = effect.getX() + energyBurstValue * effect.getS();
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.EnergyBurst);
         this.getPlayer().temporaryStatSet(400051011, time * 1000, SecondaryStatFlag.indiePartialNotDamaged, 1);
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
         RecvPacketOpcode opcode) {
      if (attack.skillID == 65101100) {
         monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.EXPLOSION, 1, attack.skillID, null, false),
               false,
               effect.getDuration(),
               true,
               effect);
      }

      if (attack.skillID == 65121100 && effect.makeChanceResult()) {
         monster.applyStatus(
               this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, attack.skillID, null, false),
               false, effect.getDuration(), true, effect);
      }

      if (attack.skillID == 65121101) {
         if (this.trinityM < effect.getY()) {
            this.trinityM++;
         }

         int time = effect.getDuration(effect.getDuration(), this.getPlayer());
         this.getPlayer().temporaryStatSet(65121101, time, SecondaryStatFlag.Trinity, effect.getX() * this.trinityM);
      }

      if (attack.skillID == 65121002) {
         monster.setFatalityPartyValue(effect.getY());
         monster.setFatalityFrom(this.getPlayer().getId());
         int partyID = 0;
         if (this.getPlayer().getParty() != null) {
            partyID = this.getPlayer().getParty().getId();
         }

         monster.setFatalityPartyID(partyID);
         monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(
                     MobTemporaryStatFlag.FATALITY,
                     effect.getX() + this.getPlayer().getSkillLevelDataOne(65120047, eff -> eff.getX()), attack.skillID,
                     null, false),
               false,
               effect.getDuration(),
               false,
               effect);
      }

      if (totalDamage > 0L && attack.skillID != 60011216) {
         Summoned summoned = this.getPlayer().getSummonBySkillID(400051011);
         if (summoned != null) {
            Skill jodiacRay = SkillFactory.getSkill(400051011);
            if (jodiacRay != null) {
               for (int i = 0; i < Randomizer.rand(1, 3); i++) {
                  int inc = Randomizer.rand(1, 3);
                  if (inc > 0) {
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initEnergyBurst(monster.getPosition());
                     ForceAtom forceAtom = new ForceAtom(
                           info,
                           400051011,
                           this.getPlayer().getId(),
                           true,
                           false,
                           monster.getObjectId(),
                           ForceAtom.AtomType.ENERGY_BURST,
                           Collections.EMPTY_LIST,
                           inc);
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                     Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.EnergyBurst);
                     int v = 0;
                     if (value != null) {
                        v = value;
                     }

                     if (v < 3) {
                        this.getPlayer().setEnergyBurst(this.getPlayer().getEnergyBurst() + inc);
                        SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400051011);
                        if (e != null && this.getPlayer().getEnergyBurst() >= e.getY()) {
                           this.getPlayer().setEnergyBurst(0);
                           v++;
                           int unit = v - 1;
                           long till = this.getPlayer().getSecondaryStat().getTill(SecondaryStatFlag.EnergyBurst);
                           int remain = (int) (till - System.currentTimeMillis());
                           SecondaryStatManager statManager = new SecondaryStatManager(
                                 this.getPlayer().getClient(), this.getPlayer().getSecondaryStat(), 400051011,
                                 e.getLevel(), remain, 0L);
                           statManager.changeStatValue(SecondaryStatFlag.indiePMDR, 400051011, unit * e.getQ());
                           statManager.changeStatValue(SecondaryStatFlag.EnergyBurst, 400051011, v);
                           statManager.temporaryStatSet();
                        }
                     }
                  }
               }
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
         boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill,
         long totalExp, RecvPacketOpcode opcode) {
      if (attack.targets > 0
            && attack.skillID != 60011216
            && attack.skillID != 65111100
            && attack.skillID != 65111007
            && attack.skillID != 65120011
            && attack.skillID != 65141502) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.AngelicBursterSoulSeeker);
         if (e != null) {
            int prop = e.getProb();
            if (attack.skillID == 65121100) {
               prop += e.getZ();
            }

            if (attack.skillID == 65121101) {
               prop += e.getW();
            }

            SecondaryStatEffect e3 = this.getPlayer().getBuffedEffect(SecondaryStatFlag.SoulExalt);
            if (e3 != null) {
               prop += e3.getX();
            }

            if (Randomizer.nextInt(100) < prop) {
               SecondaryStatEffect e2 = this.getPlayer().getSkillLevelData(65111100);
               if (e2 != null) {
                  List<Integer> targets = new ArrayList<>();
                  int count = 0;

                  for (MapleMonster mob : this.getPlayer()
                        .getMap()
                        .getMobsInRect(this.getPlayer().getPosition(), e2.getLt().x, e2.getLt().y, e2.getRb().x,
                              e2.getRb().y)) {
                     targets.add(mob.getObjectId());
                     if (++count >= effect.getMobCount()) {
                        break;
                     }
                  }

                  ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
                  if (this.getPlayer().hasBuffBySkillID(65141501)) {
                     atomInfo.initCheerballoon();
                     ForceAtom atom = new ForceAtom(
                           atomInfo,
                           65141502,
                           this.getPlayer().getId(),
                           false,
                           true,
                           this.getPlayer().getId(),
                           ForceAtom.AtomType.GrandFinale_Cheerballoon,
                           targets,
                           e.getBulletCount());
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                  } else {
                     atomInfo.initSoulSeeker();
                     ForceAtom atom = new ForceAtom(
                           atomInfo,
                           65120011,
                           this.getPlayer().getId(),
                           false,
                           true,
                           this.getPlayer().getId(),
                           ForceAtom.AtomType.AUTO_SOUL_SEEKER,
                           targets,
                           e.getBulletCount());
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                  }
               }
            }
         }
      }

      int realSkill = GameConstants.getLinkedAranSkill(attack.skillID);
      if (GameConstants.isOnOffSkill(realSkill)) {
         this.onSetOffStateSkill(realSkill);
      }

      SecondaryStatEffect e = this.getPlayer().getSkillLevelData(realSkill);
      if (e != null && e.getOnActive() > 0) {
         this.trySoulRecharge(e.getOnActive(), 0);
      }

      e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.PowerTransferGauge);
      if (e != null && e.getSourceId() == 65101002) {
         int powerTransfer = (int) Math.min(99999L, totalDamage * e.getY() / 100L);
         Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.PowerTransferGauge);
         if (v != null) {
            int newValue = (int) Math.min((long) (powerTransfer + v),
                  this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()));
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(),
                  this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.PowerTransferGauge, 65101002, newValue);
            statManager.temporaryStatSet();
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 65121003) {
         SecondaryStatEffect eff = SkillFactory.getSkill(65121003)
               .getEffect(this.getPlayer().getTotalSkillLevel(65121003));
         if (eff != null) {
            this.getPlayer().temporaryStatSet(65121012, 2000, SecondaryStatFlag.indiePartialNotDamaged, 1);
            this.getPlayer().temporaryStatSet(65121003, 15000, SecondaryStatFlag.AngelicBursterSoulResonance,
                  eff.getY());
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 65111100:
            int xy1 = packet.readShort();
            int xy2 = packet.readShort();
            int targetNum = packet.readByte();
            List<Integer> targets = new LinkedList<>();

            for (int i = 0; i < targetNum; i++) {
               targets.add(packet.readInt());
            }

            if (this.getPlayer().hasBuffBySkillID(65141501)) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initCheerballoon();
               ForceAtom forceAtom = new ForceAtom(
                     info, 65141502, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.GrandFinale_Cheerballoon, targets, 2);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            } else {
               ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
               atomInfo.initSoulSeeker();
               ForceAtom atom = new ForceAtom(
                     atomInfo, 65111007, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.MEGIDDO_FLAME, targets, 2);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
            }

            this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
            break;
         case 65141500:
            this.getPlayer().temporaryStatSet(65141501, effect.getDuration(), SecondaryStatFlag.GrandFinale, 1);
            break;
         case 65141501:
            List<MapleMonster> mobs = this.getPlayer()
                  .getMap()
                  .getMobsInRect(this.getPlayer().getPosition(), effect.getLt().x, effect.getLt().y, effect.getRb().x,
                        effect.getRb().y);
            targets = new ArrayList<>();

            for (MapleMonster target : mobs) {
               if (targets.size() >= 25) {
                  break;
               }

               targets.add(target.getId());
            }

            if (targets.size() > 0) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initCheerballoon();
               ForceAtom forceAtom = new ForceAtom(
                     info, 65141502, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.GrandFinale_Cheerballoon, targets, 15);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
            break;
         case 400051046:
            this.lastUpdateFamilarTime = System.currentTimeMillis();
            effect.applyTo(this.getPlayer());
            break;
         case 400051072:
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indieBarrier, -effect.getW());
            statups.put(SecondaryStatFlag.indieFlyAcc, 1);
            this.getPlayer().temporaryStatSet(400051072, this.getPlayer().getTotalSkillLevel(400051072), 2000, statups);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 65121003) {
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.indiePartialNotDamaged, 65121012);
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.AngelicBursterSoulResonance);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      Summoned summon;
      if ((summon = this.getPlayer().getSummonBySkillID(400051046)) != null
            && !this.getPlayer().hasBuffBySkillID(400051046)
            && (this.familiarDeleteTime == 0L || this.familiarDeleteTime < System.currentTimeMillis())) {
         this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
         this.getPlayer().getMap().removeMapObject(summon);
         this.getPlayer().removeVisibleMapObject(summon);
         this.getPlayer().removeSummon(summon);
      }

      super.updatePerSecond();
   }

   public void removeSummon(Summoned s) {
      if (s.getSkill() == 400051046) {
         this.useMascotFamiliar = false;
      }
   }

   public void skillUpdatePerTick(int skillID, PacketDecoder p) {
      Summoned summoned = this.getPlayer().getSummonBySkillID(400051046);
      if (summoned != null && !this.useMascotFamiliar
            && this.lastUpdateFamilarTime + 3000L <= System.currentTimeMillis()) {
         this.lastUpdateFamilarTime = System.currentTimeMillis();
         this.addSummonEnergy();
      }
   }

   public void addSummonEnergy() {
      int summonSkillID = 400051046;
      Summoned summon;
      if ((summon = this.getPlayer().getSummonBySkillID(400051046)) != null) {
         SecondaryStatEffect summonEffect = SkillFactory.getSkill(summonSkillID).getEffect(summon.getSkillLevel());
         int max = summonEffect.getS();
         summon.addEnergyCharge(1, max);
         this.getPlayer().getMap().broadcastMessage(CField.summonSetEnergy(this.getPlayer(), summon, 2));
         this.getPlayer().getMap().broadcastMessage(CField.summonEnergyUpdate(this.getPlayer().getId(), summon, 2));
      }
   }

   public void onSetOffStateSkill(int skillID) {
      if (!this.offStateSkills.contains(skillID)) {
         this.offStateSkills.add(skillID);
         this.getPlayer().send(CField.setOffStateForOnOffSkill(skillID));
      }
   }

   public void trySoulRecharge(int baseProp, int tried) {
      if (!this.offStateSkills.isEmpty()) {
         int prop = baseProp + this.getPlayer().getSkillLevelDataOne(65000003, SecondaryStatEffect::getX);
         SecondaryStatEffect affinity3 = this.getPlayer().getSkillLevelData(65110006);
         SecondaryStatEffect affinity4 = this.getPlayer().getSkillLevelData(65120006);
         if (affinity3 != null && this.soulRechargeFailed >= affinity3.getS()) {
            prop += affinity3.getX();
         }

         if (this.soulRechargeKillMob >= 10) {
            prop = 100;
            this.soulRechargeKillMob = 0;
         }

         boolean r = Randomizer.isSuccess(prop);
         if (!r) {
            if (affinity3 != null) {
               this.soulRechargeFailed++;
            }

            if (affinity4 != null && tried == 0) {
               this.trySoulRecharge(baseProp, 1);
            }
         } else {
            this.soulRechargeFailed = 1;
            this.offStateSkills.clear();
            Summoned summoned = this.getPlayer().getSummonBySkillID(400051046);
            if (summoned != null) {
               this.getPlayer()
                     .getMap()
                     .broadcastMessage(CField.summonAssistAttackRequest(this.getPlayer().getId(),
                           summoned.getObjectId(), Randomizer.rand(8, 9)));
               this.addSummonEnergy();
            }

            this.getPlayer().send(CField.resetOnStateForOnOfSkill());
            NormalEffect e = new NormalEffect(this.getPlayer().getId(), EffectHeader.SoulRecharge);
            this.getPlayer().send(e.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
            if (affinity4 != null && Randomizer.isSuccess(50)) {
               this.getPlayer().temporaryStatSet(65120006, 5000, SecondaryStatFlag.AffinitySlug, affinity4.getY());
            }
         }
      }
   }

   public void summonedChangeSkillRequest(int skillID) {
      if (skillID == 400051046 && !this.useMascotFamiliar) {
         Summoned summoned = this.getPlayer().getSummonBySkillID(skillID);
         if (summoned != null) {
            SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(summoned.getSkillLevel());
            this.getPlayer().send(CField.summonForcedAction(this.getPlayer(), summoned.getObjectId(), 10));
            this.useMascotFamiliar = true;
            this.familiarDeleteTime = (long) (System.currentTimeMillis() + effect.getU2() * 1000L
                  + effect.getT() * summoned.getEnergyCharge() * 1000.0);
         }
      }
   }

   public void addSoulRechargeKillMob() {
      this.soulRechargeKillMob++;
   }

   public void onSetOffTrinity() {
      this.onSetOffStateSkill(65121101);
      this.trySoulRecharge(this.getPlayer().getSkillLevelDataOne(65121101, SecondaryStatEffect::getOnActive), 0);
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case Trinity:
            packet.write(this.trinityM);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
