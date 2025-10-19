package objects.users.jobs.koc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.jobs.Pirate;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Randomizer;

public class Striker extends Pirate {
   private int lightningStack = 0;
   private int correctionStack = 0;
   private double decrementSeaWaveCooltime = 0.0;
   private double decrementSharkTorpedoCooltime = 0.0;
   private double decrementLightningGodSpearStrikeCooltime = 0.0;
   private Map<Integer, Long> annihilate = new HashMap<>();

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 15121052) {
         this.getPlayer().temporaryStatSet(15121052, 3000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      if (attack.skillID >= 400051058 && attack.skillID <= 400051067) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.SpearLightningChain);
         if (value == null) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400051058);
            if (e != null) {
               int x = e.getX() - 1;
               this.getPlayer().temporaryStatSet(400051058, e.getDuration(e.getDuration(), this.getPlayer()), SecondaryStatFlag.SpearLightningChain, x);
            }
         } else {
            value = value - 1;
            if (value <= 0) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.SpearLightningChain);
            } else {
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.SpearLightningChain, 400051058, value);
               statManager.temporaryStatSet();
            }
         }
      }

      if (attack.bAddAttackProc) {
         System.out.println("asdadadadas");
         this.getPlayer().tryApplyLightningSpear(this.getPlayer().getPosition(), (attack.display & 32768) > 0);
         SecondaryStatEffect eff = SkillFactory.getSkill(15121004).getEffect(this.getPlayer().getTotalSkillLevel(15121004));
         if (eff != null) {
            this.getPlayer().changeCooldown(15121004, -eff.getY() * 1000);
         }
      }

      if (attack.skillID == 400051096) {
         SecondaryStatEffect level = this.getPlayer().getSkillLevelData(400051044);
         if (level != null) {
            this.getPlayer().giveCoolDowns(400051044, System.currentTimeMillis(), level.getCooldown(this.getPlayer()));
            this.getPlayer().send(CField.skillCooldown(400051044, level.getCooldown(this.getPlayer())));
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.LightningSpear);
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
      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.LightningCascade) != null) {
         int skillID = 400051007;
         if (this.getPlayer().getTotalSkillLevel(500061036) > 0) {
            skillID = 500061036;
         }

         SecondaryStatEffect eff = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getSkillLevel(skillID));
         if (eff != null
            && (
               this.getPlayer().getLastActiveLightningCascade() == 0L
                  || System.currentTimeMillis() - this.getPlayer().getLastActiveLightningCascade() >= eff.getY() * 1000
            )) {
            this.getPlayer().send(CField.getActiveAttackLightningCascade(attack.skillID, skillID, this.getPlayer().getSkillLevel(skillID)));
            this.getPlayer().setLastActiveLightningCascade(System.currentTimeMillis());
         }
      }

      if (this.getPlayer().hasBuffBySkillID(15001022) && attack.targets > 0) {
         if (attack.skillID == 15120003) {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(15120003);
            if (level != null) {
               int y = this.lightningStack * level.getY();
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieDamR, 15120003, level.getDuration(level.getDuration(), this.getPlayer()), y);
            }
         }

         if (attack.skillID == 400051016) {
            if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.StrikerHyperElectric) == null) {
               this.applyStrikerStack(15001022, attack.skillID, -2);
            }
         } else if (attack.skillID == 15111022 || attack.skillID == 15120003) {
            if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.StrikerHyperElectric) == null) {
               this.applyStrikerStack(15001022, attack.skillID, 0);
            }
         } else if (attack.skillID != 15121001) {
            int delta = 1;
            if (attack.bAddAttackProc && this.getPlayer().getBuffedEffect(SecondaryStatFlag.CygnusElementSkill) != null) {
               SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.CygnusElementSkill);
               if (e != null) {
                  delta = e.getW();
               }
            }

            this.applyStrikerStack(15001022, attack.skillID, delta);
         }
      }

      if (attack.bAddAttackProc) {
         int level = 0;
         if (attack.targets > 0 && this.getPlayer().getTotalSkillLevel(15110025) > 0) {
            SecondaryStatEffect proc = this.getPlayer().getSkillLevelData(15110025);
            if (proc != null) {
               int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.PriorPreparation, 0);
               int max = proc.getU();
               value = Math.min(max, value + 1);
               this.getPlayer().temporaryStatSet(15110025, proc.getDuration(proc.getDuration(), this.getPlayer()), SecondaryStatFlag.PriorPreparation, value);
            }
         }

         int var20 = 0;
         if (!this.getPlayer().getOneInfoQuest(1544, "15100027").equals("1")
            && (var20 = this.getPlayer().getTotalSkillLevel(15100027)) > 0
            && this.getPlayer().getCooldownLimit(15101028) <= 0L) {
            SecondaryStatEffect seaWave = SkillFactory.getSkill(15100027).getEffect(var20);
            if (seaWave != null) {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.STRIKER_SEA_WAVE_RESULT.getValue());
               packet.writeInt(this.getPlayer().getId());
               packet.writeShort(this.getPlayer().getTransferFieldCount());
               this.getPlayer().getMap().broadcastMessage(packet.getPacket());
               System.out.println("dhdld??" + this.getPlayer().getTransferFieldCount());
               this.getPlayer().giveCoolDowns(15101028, System.currentTimeMillis(), seaWave.getCooldown(this.getPlayer()));
               this.getPlayer().send(CField.skillCooldown(15101028, seaWave.getCooldown(this.getPlayer())));
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.StrikerHyperElectric) != null) {
            int max = 9;
            double delta = 1.5;
            if (max > this.decrementSeaWaveCooltime) {
               this.decrementSeaWaveCooltime += delta;
               this.getPlayer().changeCooldown(15101028, (int)(-(delta * 1000.0)));
            }

            int var29 = 2;
            delta = 0.5;
            if (var29 > this.decrementSharkTorpedoCooltime) {
               this.decrementSharkTorpedoCooltime += delta;
               this.getPlayer().changeCooldown(400051016, (int)(-(delta * 1000.0)));
            }

            var29 = 3;
            if (var29 > this.decrementLightningGodSpearStrikeCooltime) {
               this.decrementLightningGodSpearStrikeCooltime += delta;
               this.getPlayer().changeCooldown(400051044, (int)(-(delta * 1000.0)));
            }
         }
      }

      if (attack.skillID == 15141000) {
         List<Integer> mobList = new ArrayList<>();

         for (AttackPair dmg : attack.allDamage) {
            int objId = dmg.objectid;
            if (this.getPlayer().getMap().getMonsterByOid(objId) != null) {
               this.annihilate.put(objId, System.currentTimeMillis());
            }
         }

         for (Entry<Integer, Long> entry : this.annihilate.entrySet()) {
            int objId = entry.getKey();
            if (this.getPlayer().getMap().getMonsterByOid(entry.getKey()) != null) {
               long time = entry.getValue();
               if (System.currentTimeMillis() <= time + 90000L) {
                  mobList.add(objId);
               }
            }
         }

         this.getPlayer().send(CField.showMonsterDebuffMark(15141000, mobList, 90000));
      }

      if (this.getPlayer().getTotalSkillLevel(15141000) > 0) {
         List<Integer> list = SkillFactory.getSkill(15141001).getSkillList();
         if (list != null && list.contains(attack.skillID)) {
            for (AttackPair dmgx : attack.allDamage) {
               int objId = dmgx.objectid;
               if (this.getPlayer().getMap().getMonsterByOid(objId) != null) {
                  Long timeCheck = this.annihilate.get(objId);
                  if (timeCheck != null && System.currentTimeMillis() <= timeCheck + 90000L) {
                     this.getPlayer().send(CField.attackMonsterDebuffMark(15141001, 15121002, objId, 290));
                  }
               }
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 15121052) {
         this.getPlayer().temporaryStatSet(15121052, 3000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (skill.getId()) {
         case 15121004:
            if (this.getPlayer().hasBuffBySkillID(15121004)) {
               this.getPlayer().temporaryStatResetBySkillID(15121004);
               return;
            }

            this.getPlayer().temporaryStatSet(effect.getSourceId(), Integer.MAX_VALUE, SecondaryStatFlag.ShadowPartner, effect.getX());
            break;
         case 15121054:
            this.decrementSeaWaveCooltime = 0.0;
            this.decrementSharkTorpedoCooltime = 0.0;
            this.decrementLightningGodSpearStrikeCooltime = 0.0;
      }

      super.onActiveSkill(skill, effect, packet);
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case IgnoreTargetDEF:
            packet.writeInt(this.lightningStack);
            break;
         case StrikerCorrectionBuff:
            packet.writeInt(this.correctionStack);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public final void applyStrikerStack(int skillID, int attackID, int delta) {
      SecondaryStatEffect level = this.getPlayer().getSkillLevelData(skillID);
      if (level != null) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(15001022);
         if (e != null) {
            int prop = e.getProb();
            int maxCount = e.getV();
            int indiePMDR = 0;
            int ignoreTargetDef = level.getX();
            if (this.getPlayer().getSkillLevel(15100025) > 0) {
               SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(15100025);
               if (effect != null) {
                  prop += effect.getProb();
                  maxCount++;
                  indiePMDR += effect.getIndiePMdR();
               }
            }

            if (this.getPlayer().getSkillLevel(15110026) > 0) {
               SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(15110026);
               if (effect != null) {
                  prop += effect.getProb();
                  maxCount++;
                  indiePMDR += effect.getIndiePMdR();
               }
            }

            if (this.getPlayer().getSkillLevel(15120008) > 0) {
               SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(15120008);
               if (effect != null) {
                  prop += effect.getProb();
                  ignoreTargetDef = effect.getX();
                  maxCount++;
               }
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.StrikerHyperElectric) != null) {
               SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.StrikerHyperElectric);
               if (effect != null) {
                  ignoreTargetDef = effect.getX();
               }
            }

            boolean change = false;
            if (delta > 0) {
               if (Randomizer.nextInt(100) < prop) {
                  this.lightningStack += delta;
                  change = true;
               }
            } else {
               if (delta == 0) {
                  this.correctionStack = this.lightningStack;
                  this.lightningStack = 0;
                  SecondaryStatEffect attackEffect = SkillFactory.getSkill(attackID).getEffect(this.getPlayer().getSkillLevel(attackID));
                  this.getPlayer()
                     .temporaryStatSet(attackID, attackEffect.getDuration(), SecondaryStatFlag.indieDamR, this.lightningStack * attackEffect.getY());
               } else {
                  this.lightningStack += delta;
               }

               change = true;
            }

            if (change) {
               this.lightningStack = Math.max(0, Math.min(this.lightningStack, maxCount));
               if (delta < 0) {
                  this.correctionStack = Math.min(maxCount, this.correctionStack + delta);
               }

               if (this.lightningStack <= 0) {
                  this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.IgnoreTargetDEF, 15001022);
               } else {
                  this.getPlayer().temporaryStatSet(15001022, level.getY() * 1000, SecondaryStatFlag.IgnoreTargetDEF, ignoreTargetDef * this.lightningStack);
               }

               if (delta <= 0) {
                  Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                  statups.put(SecondaryStatFlag.StrikerCorrectionBuff, ignoreTargetDef * this.correctionStack);
                  this.getPlayer().temporaryStatSet(15001026, level.getLevel(), level.getU() * 1000, statups, false, 0, false, true);
               } else if (this.correctionStack > 0) {
                  this.correctionStack = Math.max(0, this.correctionStack - delta);
                  if (this.correctionStack <= 0) {
                     this.getPlayer().temporaryStatReset(SecondaryStatFlag.StrikerCorrectionBuff);
                  } else {
                     Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                     statups.put(SecondaryStatFlag.StrikerCorrectionBuff, ignoreTargetDef * this.correctionStack);
                     this.getPlayer().temporaryStatSet(15001026, level.getLevel(), level.getU() * 1000, statups, false, 0, false, true);
                  }
               }
            }
         }
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
   }

   public void cleanUpHashMap() {
      this.annihilate.clear();
   }
}
