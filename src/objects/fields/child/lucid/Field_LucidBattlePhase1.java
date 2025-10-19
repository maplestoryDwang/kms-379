package objects.fields.child.lucid;

import database.DBConfig;
import java.awt.Point;
import java.util.List;
import network.models.CField;
import objects.fields.fieldset.instance.HardLucidBoss;
import objects.fields.fieldset.instance.HellLucidBoss;
import objects.fields.fieldset.instance.NormalLucidBoss;
import objects.fields.gameobject.lifes.BossLucid;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;

public class Field_LucidBattlePhase1 extends Field_LucidBattle {
   private boolean clear = false;

   public Field_LucidBattlePhase1(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate, 1);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
         mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + System.currentTimeMillis(), null, 0);
         if (this.getBoss() != null && this.getBoss().getId() == mob.getId()) {
            MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
            e.setCancelTask(5000L);
            mob.applyStatus(e);
         }
      }

      if (DBConfig.isGanglim
         && (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardLucidBoss || this.getFieldSetInstance() instanceof NormalLucidBoss)
         && this.getBoss() != null
         && this.getBoss().getId() == mob.getId()) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      MapleMonster boss = this.getBoss();
      if (boss != null && this.getBoss().getId() == mob.getId()) {
         this.phase = 1;
         if (boss.getId() == 8880190) {
            mob.addAllowedFsmSkill(5);
         } else {
            mob.addAllowedFsmSkill(5);
            mob.addAllowedFsmSkill(6);
         }
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      MapleMonster boss = this.getBoss();
      if (boss != null && this.getBoss().getId() == mob.getId()) {
         int hpPercent = mob.getHPPercent();
         if (mob.getId() != 8880190) {
            if (hpPercent < 25) {
               this.sendLucidNotice("루시드가 분노한 것 같습니다!", 3);
               mob.removeAllowedFsmSkill(9);
               mob.removeAllowedFsmSkill(10);
               mob.removeAllowedFsmSkill(11);
               mob.addAllowedFsmSkill(12);
               mob.addAllowedFsmSkill(13);
               mob.addAllowedFsmSkill(14);
            } else if (hpPercent < 50) {
               this.sendLucidNotice("루시드가 더 강한 힘을 발휘할 겁니다!", 2);
               mob.removeAllowedFsmSkill(7);
               mob.removeAllowedFsmSkill(8);
               mob.addAllowedFsmSkill(9);
               mob.addAllowedFsmSkill(10);
               mob.addAllowedFsmSkill(11);
            } else if (hpPercent < 70) {
               this.sendLucidNotice("루시드가 힘을 이끌어내고 있습니다!", 1);
               mob.removeAllowedFsmSkill(5);
               mob.removeAllowedFsmSkill(6);
               mob.addAllowedFsmSkill(7);
               mob.addAllowedFsmSkill(8);
            }
         } else if (hpPercent < 25) {
            this.sendLucidNotice("루시드가 분노한 것 같습니다!", 3);
            mob.addAllowedFsmSkill(8);
         } else if (hpPercent < 50) {
            this.sendLucidNotice("루시드가 더 강한 힘을 발휘할 겁니다!", 2);
            mob.addAllowedFsmSkill(7);
         } else if (hpPercent < 70) {
            this.sendLucidNotice("루시드가 힘을 이끌어내고 있습니다!", 1);
            mob.addAllowedFsmSkill(6);
         }
      }

      super.onMobChangeHP(mob);
   }

   @Override
   public MapleMonster getBoss() {
      int[] boss = new int[]{8880140, 8880141, 8880142, 8880190};

      for (int mob : boss) {
         MapleMonster ret = this.getMonsterById(mob);
         if (ret != null) {
            return ret;
         }
      }

      return null;
   }

   @Override
   public List<Point> getBufferflyPos() {
      return BossLucid.butterfly.phase1Pos;
   }

   @Override
   public void onButterflyFull() {
      this.sendLucidAttackButterfly(3, 2000);
      this.butterflies.clear();
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setClear(false);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      MapleMonster boss = this.getBoss();
      if (boss == null) {
         int sub = this.getId() % 50;
         if (this.getId() == 450004450) {
            this.setClear(true);

            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               if (DBConfig.isGanglim && player.getParty() != null && player.getParty().getLeader().isSkipIntro()) {
                  player.getClient().getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
               }

               player.setRegisterTransferField(450004500 + sub);
               player.setRegisterTransferFieldTime(4000L);
            }
         } else if (this.getId() == 450004150) {
            this.setClear(true);

            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               if (DBConfig.isGanglim && player.getParty() != null && player.getParty().getLeader().isSkipIntro()) {
                  player.getClient().getSession().writeAndFlush(CField.blind(1, -1, 0, 0, 0, 0, 0));
               }

               player.setRegisterTransferField(450004200 + sub);
               player.setRegisterTransferFieldTime(4000L);
            }
         }
      }
   }

   public boolean isClear() {
      return this.clear;
   }

   public void setClear(boolean clear) {
      this.clear = clear;
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 8880140 || mob.getId() == 8880141 || mob.getId() == 8880190) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            player.setCurrentBossPhase(2);
         }
      }
   }
}
