package objects.fields.child.pollo;

import constants.QuestExConstants;
import java.awt.Point;
import network.models.CField;
import objects.fields.Field;
import objects.fields.RandomPortal;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Field_StormwingArea extends Field {
   private int stormWingCount = 0;
   private MapleCharacter player = null;
   private long endGameTime = 0L;
   private boolean endGame = false;

   public Field_StormwingArea(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.player == null) {
         this.resetFully(false);
      } else {
         if (this.endGameTime == 0L) {
            this.endGameTime = System.currentTimeMillis() + 30000L;
         }

         if (!this.endGame && this.endGameTime <= System.currentTimeMillis()) {
            this.player.setRegisterTransferField(993000600);
            this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            this.endGame = true;
         } else if (!this.endGame) {
            if (this.getStormWingCount() < 5) {
               int count = 0;

               for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
                  if (mob.getId() == 9832001) {
                     count++;
                  }
               }

               if (count <= 0) {
                  RandomPortal portal = this.player.getRandomPortal();
                  if (portal == null) {
                     return;
                  }

                  MapleMonster mobx = MapleLifeFactory.getMonster(9832001);
                  ChangeableStats cs = new ChangeableStats(mobx.getStats());
                  int level = portal.getMobAvgLevel();
                  cs.level = level;
                  cs.exp = (int)portal.getMobAvgExp() * 7;
                  cs.hp = portal.getMobAvgHp();
                  mobx.getStats().setLevel((short)level);
                  mobx.getStats().setMaxHp(portal.getMobAvgHp());
                  mobx.getStats().setHp(portal.getMobAvgHp());
                  mobx.getStats().setExp((int)portal.getMobAvgExp() * 7);
                  mobx.setOverrideStats(cs);
                  this.spawnMonsterOnGroundBelow(mobx, new Point(2, -296));
               }
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.resetMonsterSpawn();
      this.setMobGen(false);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.player = player;
      player.send(CField.getClock(30));
      player.send(CField.startMapEffect("재빠른 황금빛 녀석이 스톰윙일세. 그 녀석을 잡으면 머물 수 있는 시간이 늘어나지!", 5120159, true, 5));
      RandomPortal portal = player.getRandomPortal();
      if (portal != null) {
         MapleMonster mob = MapleLifeFactory.getMonster(9832010);
         int level = portal.getMobAvgLevel();
         ChangeableStats cs = new ChangeableStats(mob.getStats());
         cs.level = level;
         cs.exp = (int)portal.getMobAvgExp() * 5;
         cs.hp = portal.getMobAvgHp();
         mob.getStats().setLevel((short)level);
         mob.getStats().setMaxHp(portal.getMobAvgHp());
         mob.getStats().setHp(portal.getMobAvgHp());
         mob.getStats().setExp((int)portal.getMobAvgExp() * 5);
         mob.setOverrideStats(cs);
         mob.setPosition(new Point(-959, -536));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(80, -536));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-916, -656));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1092, -416));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-809, -656));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1274, -296));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-17, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-977, -416));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(169, -536));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(195, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-872, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob = MapleLifeFactory.getMonster(9832012);
         cs.level = level;
         cs.exp = (int)portal.getMobAvgExp() * 5;
         cs.hp = portal.getMobAvgHp();
         mob.getStats().setLevel((short)level);
         mob.getStats().setMaxHp(portal.getMobAvgHp());
         mob.getStats().setHp(portal.getMobAvgHp());
         mob.getStats().setExp((int)portal.getMobAvgExp() * 5);
         mob.setOverrideStats(cs);
         mob.setPosition(new Point(-872, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(212, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1057, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-797, -656));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1123, -536));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-971, -536));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1040, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1008, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1219, -656));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1168, -416));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(205, -536));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1033, -656));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1244, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1227, -296));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(1289, -296));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(121, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(20, -176));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-970, -656));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(-1047, -296));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         mob.setPosition(new Point(2, -296));
         this.addMonsterSpawn(mob, 3, (byte)-1, "");
         this.setMaxRegularSpawn(31);
         this.setMobGen(true);
         player.updateOneInfo(15142, "gameType", String.valueOf(portal.getGameType().getType()));
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      this.setStormWingCount(0);
      this.player = null;
      this.endGame = false;
      this.endGameTime = 0L;
      this.stormWingCount = 0;
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventRandomPortal.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKRandomPortal.getQuestID());
   }

   @Override
   public int getStormWingCount() {
      return this.stormWingCount;
   }

   @Override
   public void setStormWingCount(int stormWingCount) {
      this.stormWingCount = stormWingCount;
   }

   public void addBonusTime() {
      this.endGameTime += 6000L;
      int remain = (int)(this.endGameTime - System.currentTimeMillis());
      this.player.send(CField.getClock(remain / 1000));
   }
}
