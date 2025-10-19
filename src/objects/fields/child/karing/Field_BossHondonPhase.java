package objects.fields.child.karing;

import java.awt.Point;
import network.models.CField;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.skills.TemporarySkill;

public class Field_BossHondonPhase extends Field_BossKaring {
   public Field_BossHondonPhase(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      MapleMonster boss = this.findBoss();
      this.entry.setMemtality(this.entry.getMemtality() + 1);
      if (boss != null) {
         ;
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      int goongiRef = 8880832;
      MapleMonster guardianAttackMob = MapleLifeFactory.getMonster(goongiRef);
      this.spawnMonster_sSack(guardianAttackMob, new Point(416, 106), 0, false, true);
      int phase1BossMobID = 8880835;
      MapleMonster phase1Boss = MapleLifeFactory.getMonster(phase1BossMobID);
      this.spawnMonster_sSack(phase1Boss, new Point(513, 106), 1, false, true);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.send(CField.setTemporarySkill(29, new TemporarySkill[]{new TemporarySkill((byte)0, 80003226, (byte)1, 0, 0, 0)}));
   }

   @Override
   public void onUserHit(MapleCharacter player, int mobTemplateID, int skillID, int skillLevel, int attackIndex) {
      super.onUserHit(player, mobTemplateID, skillID, skillLevel, attackIndex);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      this.isClear(3);

      for (Reactor r : this.getAllReactorsThreadsafe()) {
         r.setState((byte)1);
         mob.getMap().broadcastMessage(CField.triggerReactor(r, 0));
      }
   }

   @Override
   public void onMobSkill(MapleMonster mob, int skillID, int skillLevel) {
      super.onMobSkill(mob, skillID, skillLevel);
   }
}
