package objects.fields.child.pollo;

import constants.QuestExConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.models.CField;
import objects.fields.Field;
import objects.fields.RandomPortal;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;

public class Field_MidnightMonsterHunting extends Field {
   private MapleCharacter player = null;
   private long displayStartGameTime = 0L;
   private boolean displayStartGame = false;
   private long startGameTime = 0L;
   private boolean startGame = false;
   private long endGameTime = 0L;
   private boolean endGame = false;
   private boolean clearGame = false;
   private int remainCount = 100;
   private int spawnIndex = 0;
   private static List<List<Pair<Point, Integer>>> mobLists = new ArrayList<>();

   public Field_MidnightMonsterHunting(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.player == null) {
         this.resetFully(false);
      } else if (this.isClearGame()) {
         this.player.send(CField.showEffect("killing/clear"));
         this.player.setRegisterTransferField(993000600);
         this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         this.setClearGame(false);
         this.endGame = true;
      } else {
         if (this.endGameTime == 0L) {
            this.endGameTime = System.currentTimeMillis() + 180000L;
         }

         if (!this.endGame && this.endGameTime <= System.currentTimeMillis()) {
            this.player.send(CField.showEffect("killing/timeout"));
            this.player.setRegisterTransferField(993000600);
            this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            this.endGame = true;
         } else if (!this.endGame) {
            if (this.displayStartGameTime == 0L) {
               this.displayStartGameTime = System.currentTimeMillis() + 2000L;
            }

            if (!this.displayStartGame && this.displayStartGameTime <= System.currentTimeMillis()) {
               this.player.send(CField.showEffect("defense/count"));
               this.displayStartGame = true;
               this.startGameTime = System.currentTimeMillis() + 3000L;
            }

            if (!this.startGame && this.startGameTime != 0L && this.startGameTime <= System.currentTimeMillis()) {
               this.endGameTime = System.currentTimeMillis() + 180000L;
               this.player.send(CField.getClock(180));
               this.player.send(CField.environmentChange("killing/first/start", 19, 0));
               this.startGame = true;
            }

            if (this.startGame) {
               List<Pair<Point, Integer>> list = mobLists.get(this.spawnIndex++);
               if (list != null) {
                  RandomPortal portal = this.player.getRandomPortal();
                  if (portal != null) {
                     for (Pair<Point, Integer> pair : list) {
                        Point pos = pair.left;
                        int mobID = pair.right;
                        MapleMonster mob = MapleLifeFactory.getMonster(mobID);
                        ChangeableStats cs = new ChangeableStats(mob.getStats());
                        int level = portal.getMobAvgLevel();
                        cs.level = level;
                        long hp = 777777L;
                        long exp = portal.getMobAvgExp() * 6L;
                        cs.hp = hp;
                        cs.exp = (int)exp;
                        mob.getStats().setLevel((short)level);
                        mob.getStats().setMaxHp(hp);
                        mob.getStats().setHp(hp);
                        mob.getStats().setExp((int)exp);
                        mob.getStats().setSpeed(0);
                        mob.setOverrideStats(cs);
                        this.spawnMonsterOnGroundBelow(mob, pos);
                     }
                  }

                  if (this.spawnIndex >= mobLists.size()) {
                     this.spawnIndex = 0;
                  }
               }
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.player = null;
      this.displayStartGameTime = 0L;
      this.displayStartGame = false;
      this.startGameTime = 0L;
      this.startGame = false;
      this.endGameTime = 0L;
      this.endGame = false;
      this.setClearGame(false);
      this.spawnIndex = 0;
      this.setRemainCount(100);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.player = player;
      player.temporaryStatSet(80002894, Integer.MAX_VALUE, SecondaryStatFlag.RideVehicle, 1939017);
      player.send(CField.startMapEffect("npc/채집 키로 공격을 할 수 있다. 몰려오는 놈들을 나와 함께 다 쓸어버리자고.", 5120159, true, 10));
      RandomPortal portal = player.getRandomPortal();
      if (portal != null) {
         player.updateOneInfo(15142, "gameType", String.valueOf(portal.getGameType().getType()));
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      player.temporaryStatReset(SecondaryStatFlag.RideVehicle);
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventRandomPortal.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKRandomPortal.getQuestID());
   }

   public boolean isClearGame() {
      return this.clearGame;
   }

   public void setClearGame(boolean clearGame) {
      this.clearGame = clearGame;
   }

   public int getRemainCount() {
      return this.remainCount;
   }

   public void setRemainCount(int remainCount) {
      this.remainCount = remainCount;
   }

   static {
      List<Pair<Point, Integer>> list = new ArrayList<>();
      list.add(new Pair<>(new Point(150, 270), 9833760));
      list.add(new Pair<>(new Point(295, 270), 9833762));
      list.add(new Pair<>(new Point(444, 270), 9833763));
      list.add(new Pair<>(new Point(533, 270), 9833764));
      mobLists.add(list);
      list = new ArrayList<>();
      list.add(new Pair<>(new Point(150, 270), 9833765));
      list.add(new Pair<>(new Point(295, 270), 9833767));
      list.add(new Pair<>(new Point(444, 270), 9833768));
      list.add(new Pair<>(new Point(613, 270), 9833770));
      list.add(new Pair<>(new Point(744, 270), 9833772));
      list.add(new Pair<>(new Point(912, 270), 9833773));
      mobLists.add(list);
      List<Pair<Point, Integer>> var2 = new ArrayList();
      var2.add(new Pair<>(new Point(150, 270), 9833763));
      var2.add(new Pair<>(new Point(220, 270), 9833764));
      var2.add(new Pair<>(new Point(295, 270), 9833774));
      var2.add(new Pair<>(new Point(380, 270), 9833766));
      var2.add(new Pair<>(new Point(444, 270), 9833769));
      var2.add(new Pair<>(new Point(533, 270), 9833770));
      mobLists.add(var2);
      List<Pair<Point, Integer>> var3 = new ArrayList();
      var3.add(new Pair<>(new Point(150, 270), 9833772));
      var3.add(new Pair<>(new Point(220, 270), 9833773));
      var3.add(new Pair<>(new Point(295, 270), 9833760));
      var3.add(new Pair<>(new Point(380, 270), 9833761));
      var3.add(new Pair<>(new Point(444, 270), 9833763));
      var3.add(new Pair<>(new Point(533, 270), 9833764));
      var3.add(new Pair<>(new Point(613, 270), 9833768));
      var3.add(new Pair<>(new Point(687, 270), 9833767));
      var3.add(new Pair<>(new Point(744, 270), 9833769));
      var3.add(new Pair<>(new Point(823, 270), 9833770));
      mobLists.add(var3);
      List<Pair<Point, Integer>> var4 = new ArrayList();
      mobLists.add(var4);
      List<Pair<Point, Integer>> var5 = new ArrayList();
      mobLists.add(var5);
      List<Pair<Point, Integer>> var6 = new ArrayList();
      var6.add(new Pair<>(new Point(150, 270), 9833774));
      var6.add(new Pair<>(new Point(295, 270), 9833773));
      var6.add(new Pair<>(new Point(380, 270), 9833762));
      var6.add(new Pair<>(new Point(444, 270), 9833760));
      var6.add(new Pair<>(new Point(533, 270), 9833763));
      mobLists.add(var6);
      List<Pair<Point, Integer>> var7 = new ArrayList();
      mobLists.add(var7);
      List<Pair<Point, Integer>> var8 = new ArrayList();
      mobLists.add(var8);
      List<Pair<Point, Integer>> var9 = new ArrayList();
      var9.add(new Pair<>(new Point(150, 270), 9833764));
      var9.add(new Pair<>(new Point(220, 270), 9833765));
      var9.add(new Pair<>(new Point(380, 270), 9833766));
      var9.add(new Pair<>(new Point(533, 270), 9833771));
      var9.add(new Pair<>(new Point(613, 270), 9833769));
      var9.add(new Pair<>(new Point(744, 270), 9833772));
      var9.add(new Pair<>(new Point(912, 270), 9833773));
      mobLists.add(var9);
      List<Pair<Point, Integer>> var10 = new ArrayList();
      mobLists.add(var10);
      List<Pair<Point, Integer>> var11 = new ArrayList();
      var11.add(new Pair<>(new Point(460, 270), 9833761));
      var11.add(new Pair<>(new Point(550, 270), 9833763));
      var11.add(new Pair<>(new Point(626, 270), 9833764));
      var11.add(new Pair<>(new Point(743, 270), 9833765));
      var11.add(new Pair<>(new Point(760, 270), 9833767));
      mobLists.add(var11);
      List<Pair<Point, Integer>> var12 = new ArrayList();
      var12.add(new Pair<>(new Point(150, 270), 9833769));
      var12.add(new Pair<>(new Point(220, 270), 9833770));
      var12.add(new Pair<>(new Point(295, 270), 9833774));
      var12.add(new Pair<>(new Point(380, 270), 9833773));
      var12.add(new Pair<>(new Point(444, 270), 9833760));
      var12.add(new Pair<>(new Point(533, 270), 9833762));
      mobLists.add(var12);
      List<Pair<Point, Integer>> var13 = new ArrayList();
      mobLists.add(var13);
      List<Pair<Point, Integer>> var14 = new ArrayList();
      mobLists.add(var14);
      List<Pair<Point, Integer>> var15 = new ArrayList();
      var15.add(new Pair<>(new Point(150, 270), 9833763));
      var15.add(new Pair<>(new Point(220, 270), 9833764));
      var15.add(new Pair<>(new Point(295, 270), 9833771));
      var15.add(new Pair<>(new Point(380, 270), 9833766));
      var15.add(new Pair<>(new Point(444, 270), 9833769));
      mobLists.add(var15);
      List<Pair<Point, Integer>> var16 = new ArrayList();
      var16.add(new Pair<>(new Point(220, 270), 9833768));
      var16.add(new Pair<>(new Point(295, 270), 9833772));
      var16.add(new Pair<>(new Point(380, 270), 9833773));
      var16.add(new Pair<>(new Point(444, 270), 9833768));
      var16.add(new Pair<>(new Point(533, 270), 9833762));
      var16.add(new Pair<>(new Point(613, 270), 9833774));
      mobLists.add(var16);
      List<Pair<Point, Integer>> var17 = new ArrayList();
      var17.add(new Pair<>(new Point(295, 270), 9833764));
      var17.add(new Pair<>(new Point(380, 270), 9833765));
      mobLists.add(var17);
      List<Pair<Point, Integer>> var18 = new ArrayList();
      var18.add(new Pair<>(new Point(444, 270), 9833766));
      var18.add(new Pair<>(new Point(533, 270), 9833769));
      mobLists.add(var18);
      List<Pair<Point, Integer>> var19 = new ArrayList();
      mobLists.add(var19);
      List<Pair<Point, Integer>> var20 = new ArrayList();
      var20.add(new Pair<>(new Point(150, 270), 9833770));
      var20.add(new Pair<>(new Point(220, 270), 9833772));
      var20.add(new Pair<>(new Point(295, 270), 9833773));
      mobLists.add(var20);
      List<Pair<Point, Integer>> var21 = new ArrayList();
      var21.add(new Pair<>(new Point(150, 270), 9833760));
      var21.add(new Pair<>(new Point(220, 270), 9833761));
      var21.add(new Pair<>(new Point(295, 270), 9833763));
      var21.add(new Pair<>(new Point(380, 270), 9833764));
      var21.add(new Pair<>(new Point(444, 270), 9833771));
      var21.add(new Pair<>(new Point(444, 270), 9833767));
      var21.add(new Pair<>(new Point(533, 270), 9833769));
      var21.add(new Pair<>(new Point(613, 270), 9833774));
      var21.add(new Pair<>(new Point(687, 270), 9833772));
      var21.add(new Pair<>(new Point(687, 270), 9833773));
      var21.add(new Pair<>(new Point(744, 270), 9833760));
      var21.add(new Pair<>(new Point(823, 270), 9833762));
      var21.add(new Pair<>(new Point(912, 270), 9833763));
      var21.add(new Pair<>(new Point(912, 270), 9833764));
      var21.add(new Pair<>(new Point(969, 270), 9833765));
      var21.add(new Pair<>(new Point(969, 270), 9833768));
      mobLists.add(var21);
   }
}
