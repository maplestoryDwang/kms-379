package objects.users.jobs.hero;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCoolDownValueHolder;
import objects.users.jobs.Magician;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.LarknessDirection;
import objects.users.skills.LarknessInfo;
import objects.users.skills.Skill;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import scripting.newscripting.ScriptManager;

public class Luminous extends Magician {
   private long lastActiveLiberationOrbTime = 0L;
   private int larkness;
   private int larknessDG;
   private int larknessLG;
   private int larknessX;
   public LarknessDirection larknessDirection;
   private LarknessInfo[] larknessInfo = new LarknessInfo[2];
   public byte stackBuffCount = 0;
   private boolean barrageCooltimeReset = false;
   boolean equalibriumLock = false;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 27121052) {
         this.getPlayer().temporaryStatSet(27121052, 4000, SecondaryStatFlag.NotDamaged, 1);
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
      this.checkHitAbsoluteKill(attack.skillID);
      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.targets > 0) {
         SecondaryStatEffect self = this.getPlayer().getSkillLevelData(27110007);
         if (self != null && Randomizer.nextInt(100) < 30) {
            if (boss) {
               int recoveryMp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) / 100L * 3L);
               this.getPlayer().addMP(recoveryMp);
            } else {
               int recoveryMp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) / 100L * 5L);
               this.getPlayer().addMP(recoveryMp);
            }
         }

         if (GameConstants.isLarknessMixSkill(attack.skillID)
            && this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.Larkness, 0) == 2
            && this.getPlayer().skillisCooling(400021071)) {
            this.getPlayer().changeCooldown(400021071, -3000L);
         }

         if (this.getPlayer().hasBuffBySkillID(400021071)) {
         }

         this.changeLarknessStack(attack.skillID, effect);
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(27120005);
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.StackBuff) != null && eff != null) {
            if (eff.makeChanceResult()) {
               if (this.stackBuffCount < eff.getX()) {
                  this.stackBuffCount++;
               }

               this.getPlayer().temporaryStatSet(27120005, eff.getDuration(), SecondaryStatFlag.StackBuff, eff.getDAMRate() * this.stackBuffCount);
            }
         } else if (eff != null && eff.makeChanceResult()) {
            this.stackBuffCount = 1;
            this.getPlayer().temporaryStatSet(27120005, eff.getDuration(), SecondaryStatFlag.StackBuff, eff.getDAMRate() * this.stackBuffCount);
         }

         if (this.larknessX == 1 && GameConstants.isLarknessLightSkill(attack.skillID)) {
            int x = this.getPlayer().getSkillLevelDataOne(20040219, SecondaryStatEffect::getX);
            if (x > 0) {
               int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * x);
               this.getPlayer().addHP(hp, false);
            }
         }

         if (this.getPlayer().getTotalSkillLevel(400021105) > 0) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400021105);
            if (e != null) {
               if (this.getPlayer().getBuffedValue(SecondaryStatFlag.LiberationOrbActive) != null
                  && this.getPlayer().checkInterval(this.lastActiveLiberationOrbTime, e.getU2() * 1000)
                  && this.getPlayer().getSecondaryStat().LiberationOrbActiveRemainCount > 0) {
                  int balance = this.getPlayer().getBuffedValue(SecondaryStatFlag.LiberationOrbActive) % 1000;
                  this.getPlayer().sendRegisterExtraSkillWithIndex(attack.forcedPos, (attack.display & 32768) != 0, 400021105, 2 + balance);
                  this.lastActiveLiberationOrbTime = System.currentTimeMillis();
                  this.getPlayer().getSecondaryStat().LiberationOrbActiveRemainCount--;
                  SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.LiberationOrbActive, 400021105, 1);
                  statManager.temporaryStatSet();
               }

               if (this.larknessX == 1) {
                  if (this.getPlayer().getBuffedValue(SecondaryStatFlag.LiberationOrbActive) == null
                     && (
                        GameConstants.isLarknessLightSkill(attack.skillID)
                           || GameConstants.isLarknessDarkSkill(attack.skillID)
                           || GameConstants.isLarknessMixSkill(attack.skillID)
                     )
                     && this.getPlayer().getCooldownLimit(400021106) <= 0L) {
                     int u = e.getU();
                     this.getPlayer().setLiberationOrbLightMad(Math.min(u, this.getPlayer().getLiberationOrbLightMad() + 1));
                     this.getPlayer().setLiberationOrbDarkMad(Math.min(u, this.getPlayer().getLiberationOrbDarkMad() + 1));
                     this.getPlayer().temporaryStatSet(400021105, Integer.MAX_VALUE, SecondaryStatFlag.LiberationOrb, 1);
                     this.sendLiberationOrb(attack.forcedPos, (attack.display & 32768) != 0, 400021107);
                     this.sendLiberationOrb(attack.forcedPos, (attack.display & 32768) != 0, 400021108);
                     this.getPlayer().send(CField.skillCooldown(400021106, e.getX() * 1000));
                     this.getPlayer().addCooldown(400021106, System.currentTimeMillis(), e.getX() * 1000);
                  }
               } else if (this.getPlayer().getBuffedValue(SecondaryStatFlag.LiberationOrbActive) == null) {
                  if (this.larknessDirection == LarknessDirection.DarkToLight) {
                     if (GameConstants.isLarknessDarkSkill(attack.skillID) && this.getPlayer().getCooldownLimit(400021106) <= 0L) {
                        int u = e.getU();
                        this.getPlayer().setLiberationOrbDarkMad(Math.min(u, this.getPlayer().getLiberationOrbDarkMad() + 1));
                        this.getPlayer().temporaryStatSet(400021105, Integer.MAX_VALUE, SecondaryStatFlag.LiberationOrb, 1);
                        this.sendLiberationOrb(attack.forcedPos, (attack.display & 32768) != 0, 400021108);
                        this.getPlayer().send(CField.skillCooldown(400021106, e.getX() * 1000));
                        this.getPlayer().addCooldown(400021106, System.currentTimeMillis(), e.getX() * 1000);
                     }
                  } else if (this.larknessDirection == LarknessDirection.LightToDark
                     && GameConstants.isLarknessLightSkill(attack.skillID)
                     && this.getPlayer().getCooldownLimit(400021106) <= 0L) {
                     int u = e.getU();
                     this.getPlayer().setLiberationOrbLightMad(Math.min(u, this.getPlayer().getLiberationOrbLightMad() + 1));
                     this.getPlayer().temporaryStatSet(400021105, Integer.MAX_VALUE, SecondaryStatFlag.LiberationOrb, 1);
                     this.sendLiberationOrb(attack.forcedPos, (attack.display & 32768) != 0, 400021107);
                     this.getPlayer().send(CField.skillCooldown(400021106, e.getX() * 1000));
                     this.getPlayer().addCooldown(400021106, System.currentTimeMillis(), e.getX() * 1000);
                  }
               }
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 27111009:
            this.tryApplyLarknessEquilibrium(true);
            break;
         case 27111101:
            effect.applyTo(this.getPlayer(), true);
            break;
         case 27121012:
            ScriptManager.runScript(this.getPlayer().getClient(), "luminous_type", null, null);
            effect.applyTo(this.getPlayer(), false);
            break;
         case 27121054:
            if (this.larknessDirection != LarknessDirection.LightToDark) {
               this.larkness = 9999;
               this.larknessDirection = LarknessDirection.DarkToLight;
            } else {
               this.larkness = 1;
            }

            this.tryApplyLarknessEquilibrium(false);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 400021005:
            int larknessReason;
            if (this.larknessDirection != LarknessDirection.DarkToLight) {
               larknessReason = 20040220;
            } else {
               larknessReason = 20040219;
            }

            this.larknessX = 0;
            this.deferringEquilbriumCheck(this.getActiveSkillID());
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.Larkness, larknessReason, 2);
            statManager.temporaryStatSet();
            super.onActiveSkill(skill, effect, packet);
            break;
         case 400021041:
         case 400021071:
            this.deferringEquilbriumCheck(this.getActiveSkillID());
            super.onActiveSkill(skill, effect, packet);
            break;
         case 400021105:
            int darkMad = this.getPlayer().getLiberationOrbDarkMad();
            int lightMad = this.getPlayer().getLiberationOrbLightMad();
            this.getPlayer().setLiberationOrbDarkMad(0);
            this.getPlayer().setLiberationOrbLightMad(0);
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.LiberationOrb);
            boolean balance = darkMad == lightMad;
            int total = (darkMad + lightMad) * 1000;
            int value = total + (balance ? 2 : 1);
            this.getPlayer().getSecondaryStat().LiberationOrbActiveRemainCount = effect.getV2();
            this.getPlayer()
               .temporaryStatSet(400021105, effect.getDuration(effect.getDuration(), this.getPlayer()), SecondaryStatFlag.LiberationOrbActive, value);
            effect.applyTo(this.getPlayer(), true);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   public void checkHitAbsoluteKill(int skillID) {
      if (GameConstants.isLuminous(this.getPlayer().getJob()) && skillID == 27121303) {
         int sid = 400021071;
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(sid);
         if (effect != null) {
            int buffTime = effect.getDuration(effect.getDuration(), this.getPlayer());
            Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.SwordBaptism);
            int v = 0;
            if (value != null) {
               v = value;
            }

            this.getPlayer().temporaryStatSet(sid, buffTime, SecondaryStatFlag.SwordBaptism, v + 1);
            if (v == effect.getX()) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.SwordBaptism);
               this.getPlayer().clearCooldown(sid);
            }
         }
      }
   }

   public void sendLiberationOrb(Point position, boolean isLeft, int skillID) {
      List<ExtraSkillInfo> extraSkills = new ArrayList<>();
      extraSkills.add(new ExtraSkillInfo(skillID, 0));
      this.getPlayer().send(CField.getRegisterExtraSkill(400021105, position.x, position.y, isLeft, extraSkills, 1));
   }

   private void barrageOfLightAndDarknessCooltimeSet(int skillID) {
      if (this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.Larkness, 0) == 2 && this.getPlayer().skillisCooling(400021071)) {
         if (this.barrageCooltimeReset) {
            this.getPlayer().clearCooldown(400021071);
            this.barrageCooltimeReset = false;
         } else if (GameConstants.isLarknessMixSkill(skillID)) {
            this.getPlayer().changeCooldown(400021071, -3000L);
         }
      }
   }

   private boolean deferringEquilbriumCheck(int skillID) {
      boolean equlibriumAuto = this.getPlayer().getOneInfoQuestInteger(21770, "lm0") == 1;
      if (!equlibriumAuto
         || this.equalibriumLock
         || !GameConstants.isLarknessMixSkill(skillID) && skillID != 400021005 && skillID != 400021041 && skillID != 400021071 && skillID != 27111009) {
         return false;
      } else {
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.Larkness, 27111009);
         this.tryApplyLarknessEquilibrium(true);
         return true;
      }
   }

   public void changeLarknessStack(int skillID, SecondaryStatEffect effect) {
      if (this.getPlayer().getTotalSkillLevel(20040216) != 0) {
         if (this.getPlayer().getTotalSkillLevel(20040217) != 0) {
            if (GameConstants.isLuminousSkill(skillID)) {
               if (!this.tryFirstLarknessSkill(skillID)) {
                  if (this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.Larkness, 0) != 2) {
                     if (!this.deferringEquilbriumCheck(skillID)) {
                        int delta = 0;
                        int darkToLightAdd = 0;
                        SecondaryStatEffect darkLightMastery = this.getPlayer().getSkillLevelData(27120008);
                        if (skillID == 27120047) {
                           darkToLightAdd += this.getPlayer().getSkillLevelData(27120047).getGauge();
                        }

                        if (GameConstants.isLarknessLightSkill(skillID) && this.larknessDirection == LarknessDirection.LightToDark) {
                           delta = -(effect.getGauge() + (darkLightMastery != null ? darkLightMastery.getY() : 0));
                        } else if (GameConstants.isLarknessDarkSkill(skillID) && this.larknessDirection == LarknessDirection.DarkToLight) {
                           delta = effect.getGauge() + darkToLightAdd + (darkLightMastery != null ? darkLightMastery.getZ() : 0);
                        }

                        this.larkness = Math.max(1, Math.min(this.larkness + delta, 9999));
                        if (!this.tryApplyLarknessEquilibrium(false) && !GameConstants.isLarknessMixSkill(skillID) && this.larknessDirection != null) {
                           this.getPlayer().send(CWvsContext.BuffPacket.sendLarknessStack(this.larkness, this.larknessDirection));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean tryFirstLarknessSkill(int skillID) {
      if (this.larknessDirection != null && this.larknessDirection != LarknessDirection.None) {
         return false;
      } else if (GameConstants.isLarknessMixSkill(skillID)) {
         return false;
      } else {
         if (GameConstants.isLarknessLightSkill(skillID)) {
            this.larkness = 1;
            this.larknessDirection = LarknessDirection.DarkToLight;
         } else if (GameConstants.isLarknessDarkSkill(skillID)) {
            this.larkness = 9999;
            this.larknessDirection = LarknessDirection.LightToDark;
         }

         if (this.larknessDirection != null) {
            this.getPlayer().send(CWvsContext.BuffPacket.sendLarknessStack(this.larkness, this.larknessDirection));
         }

         this.applyLarkness();
         return true;
      }
   }

   public boolean tryApplyLarknessEquilibrium(boolean fromDefferingFunction) {
      boolean autoEqulibrium = this.getPlayer().getOneInfoQuestInteger(21770, "lm0") == 0;
      if (autoEqulibrium) {
         this.equalibriumLock = false;
      }

      if (!autoEqulibrium && !fromDefferingFunction) {
         if (this.larkness == 9999) {
            this.larkness = 10000;
            this.equalibriumLock = false;
         }

         if (this.larkness == 1) {
            this.larkness = 0;
            this.equalibriumLock = false;
         }

         if (this.larkness == 0 || this.larkness == 10000) {
            this.larknessX = 1;
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.Larkness, this.larknessInfo[0].getLarknessReason(), 1);
            statManager.temporaryStatSet();
         }

         return false;
      } else {
         if (this.larkness <= 1 && this.larknessDirection == LarknessDirection.LightToDark) {
            if (!fromDefferingFunction) {
               this.larknessDirection = LarknessDirection.DarkToLight;
            }

            if (fromDefferingFunction) {
               this.larkness = 9999;
            }

            this.getPlayer().send(CWvsContext.BuffPacket.sendLarknessStack(this.larkness, this.larknessDirection));
            if (this.canApplyEquilibrium() && !this.equalibriumLock) {
               this.equalibriumLock = true;
               this.applyLarknessEquilibrium(LarknessDirection.LightToDark);
               return true;
            }

            this.applyLarknessDark();
         } else if (this.larkness >= 9999 && this.larknessDirection == LarknessDirection.DarkToLight) {
            if (!fromDefferingFunction) {
               this.larknessDirection = LarknessDirection.LightToDark;
            }

            if (fromDefferingFunction) {
               this.larkness = 1;
            }

            this.getPlayer().send(CWvsContext.BuffPacket.sendLarknessStack(this.larkness, this.larknessDirection));
            if (this.canApplyEquilibrium() && !this.equalibriumLock) {
               this.equalibriumLock = true;
               this.applyLarknessEquilibrium(LarknessDirection.DarkToLight);
               return true;
            }

            this.applyLarknessLight();
         }

         return false;
      }
   }

   private boolean canApplyEquilibrium() {
      return this.getPlayer().getTotalSkillLevel(20040219) != 0;
   }

   public void updateLarkness(boolean first) {
      boolean autoEqulibrium = this.getPlayer().getOneInfoQuestInteger(21770, "lm0") == 0;
      if (!first) {
         if (autoEqulibrium) {
            this.applyLarkness();
         } else {
            this.applyEquilibriumLiberation();
         }
      }

      if (this.larknessDirection != null) {
         this.getPlayer().send(CWvsContext.BuffPacket.sendLarknessStack(this.larkness, this.larknessDirection));
      }
   }

   private void applyLarkness() {
      if (this.larknessDirection == LarknessDirection.LightToDark) {
         this.applyLarknessLight();
      }

      if (this.larknessDirection == LarknessDirection.DarkToLight) {
         this.applyLarknessDark();
      }
   }

   private void applyEquilibriumLiberation() {
      if (this.larknessDirection == LarknessDirection.LightToDark) {
         this.larknessDirection = LarknessDirection.DarkToLight;
         this.larkness = 2;
         this.applyLarknessDark();
      } else {
         this.larknessDirection = LarknessDirection.LightToDark;
         this.larkness = 9998;
         this.applyLarknessLight();
      }
   }

   private void applyLarknessDark() {
      this.larknessInfo[0] = new LarknessInfo();
      this.larknessInfo[1] = new LarknessInfo();
      this.larknessInfo[0].setLarknessReason(20040217);
      this.larknessInfo[0].setLarknessTime(200000000);
      this.larknessInfo[1].setLarknessReason(0);
      this.larknessInfo[1].setLarknessTime(0);
      this.larknessDG = -1;
      this.larknessLG = 10000;
      this.larknessX = 0;
      this.getPlayer().temporaryStatSet(20040217, Integer.MAX_VALUE, SecondaryStatFlag.Larkness, 1);
   }

   private void applyLarknessLight() {
      this.larknessInfo[0] = new LarknessInfo();
      this.larknessInfo[1] = new LarknessInfo();
      this.larknessInfo[0].setLarknessReason(20040216);
      this.larknessInfo[0].setLarknessTime(200000000);
      this.larknessInfo[1].setLarknessReason(0);
      this.larknessInfo[1].setLarknessTime(0);
      this.larknessDG = -1;
      this.larknessLG = 1;
      this.larknessX = 0;
      this.getPlayer().temporaryStatSet(20040216, Integer.MAX_VALUE, SecondaryStatFlag.Larkness, 1);
   }

   private void applyLarknessEquilibrium(LarknessDirection direction) {
      int larknessReason = 0;
      if (direction != LarknessDirection.DarkToLight) {
         this.larknessInfo[0] = new LarknessInfo();
         this.larknessInfo[1] = new LarknessInfo();
         this.larknessInfo[0].setLarknessReason(20040216);
         this.larknessInfo[0].setLarknessTime(2100000000);
         this.larknessInfo[1].setLarknessReason(20040217);
         this.larknessInfo[1].setLarknessTime(2100000000);
         this.larknessDG = 10000;
         this.larknessLG = -1;
         larknessReason = 20040220;
      } else {
         this.larknessInfo[0] = new LarknessInfo();
         this.larknessInfo[1] = new LarknessInfo();
         this.larknessInfo[0].setLarknessReason(20040217);
         this.larknessInfo[0].setLarknessTime(2100000000);
         this.larknessInfo[1].setLarknessReason(20040216);
         this.larknessInfo[1].setLarknessTime(2100000000);
         this.larknessDG = -1;
         this.larknessLG = 10000;
         larknessReason = 20040219;
      }

      boolean autoEqulibrium = this.getPlayer().getOneInfoQuestInteger(21770, "lm0") == 0;
      if (autoEqulibrium) {
         this.larknessX = 1;
      }

      SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(20040219);
      if (effect != null) {
         int buffTime = effect.getDuration(effect.getDuration(), this.getPlayer())
            + this.getPlayer().getSkillLevelDataOne(27120008, SecondaryStatEffect::getDuration);
         this.getPlayer().temporaryStatSet(larknessReason, buffTime, SecondaryStatFlag.Larkness, 2);
         this.getPlayer().clearCooldown(400021071);
         this.barrageCooltimeReset = true;

         for (MapleCoolDownValueHolder holder : this.getPlayer().getCooldowns()) {
            if (GameConstants.isLarknessMixSkill(holder.skillId)) {
               this.getPlayer().clearCooldown(holder.skillId);
            }
         }
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case Larkness:
            for (int i = 0; i < 2; i++) {
               LarknessInfo info = this.larknessInfo[i];
               packet.writeInt(info.getLarknessReason());
               packet.writeInt(info.getLarknessTime());
            }

            packet.writeInt(this.larknessDG);
            packet.writeInt(this.larknessLG);
            packet.writeInt(this.larknessX);
            break;
         case StackBuff:
            packet.write(this.stackBuffCount);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
