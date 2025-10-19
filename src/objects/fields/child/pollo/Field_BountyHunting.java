package objects.fields.child.pollo;

import constants.QuestExConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.fields.RandomPortal;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Pair;

public class Field_BountyHunting extends Field {
   private MapleCharacter player = null;
   private long displayStartGameTime = 0L;
   private boolean displayStartGame = false;
   private long startGameTime = 0L;
   private boolean startGame = false;
   private long endGameTime = 0L;
   private boolean endGame = false;
   private boolean clearGame = false;
   private boolean failGame = false;
   private static List<List<Pair<Point, Integer>>> mobLists = new ArrayList<>();
   private int stage = 0;
   private long nextStageTime = 0L;

   public Field_BountyHunting(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.player == null) {
         this.resetFully(false);
      } else if (this.failGame) {
         this.player.send(CField.showEffect("killing/fail"));
         this.player.setRegisterTransferField(993000600);
         this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         this.failGame = false;
         this.endGame = true;
      } else if (this.clearGame) {
         this.player.send(CField.showEffect("killing/clear"));
         this.player.setRegisterTransferField(993000600);
         this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         this.clearGame = false;
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
               this.nextStageTime = System.currentTimeMillis() + 3000L;
               this.endGameTime = System.currentTimeMillis() + 180000L;
               this.broadcastMessage(CField.getClock(180));
               this.broadcastMessage(CField.showEffect("defense/count"));
               this.displayStartGame = true;
               this.startGame = true;
            }

            if (this.startGame) {
               if (this.nextStageTime == 0L && this.stage <= 5 && this.getAllMonsterSize() <= 0) {
                  if (this.stage >= 5) {
                     this.clearGame = true;
                     return;
                  }

                  this.broadcastMessage(CField.showEffect("defense/count"));
                  this.nextStageTime = System.currentTimeMillis() + 3000L;
               }

               if (this.nextStageTime != 0L && this.nextStageTime <= System.currentTimeMillis()) {
                  this.nextStage();
                  List<Pair<Point, Integer>> list = mobLists.get(this.stage - 1);
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
                           long hp = portal.getMobAvgHp();
                           long exp = portal.getMobAvgExp() * 5L;
                           if (mobID == 9830004) {
                              hp *= 4L;
                              exp = (long)(exp * 2.5);
                           } else if (mobID == 9830007) {
                              hp *= 2L;
                              exp = (long)(exp * 1.5);
                           } else if (mobID == 9830008) {
                              hp *= 8L;
                              exp *= 5L;
                           } else if (mobID == 9830013) {
                              hp *= 10L;
                              exp *= 10L;
                           } else if (mobID >= 9830009 && mobID <= 9830012) {
                              hp *= 2L;
                              exp = (long)(exp * 1.5);
                           } else if (mobID >= 9830013 && mobID <= 9830017) {
                              hp *= 3L;
                              exp *= 2L;
                           } else if (mobID == 9830018) {
                              hp *= 20L;
                              exp *= 20L;
                           }

                           cs.hp = hp;
                           cs.exp = (int)exp;
                           mob.getStats().setLevel((short)level);
                           mob.getStats().setMaxHp(hp);
                           mob.getStats().setHp(hp);
                           mob.getStats().setExp((int)exp);
                           mob.getStats().setSpeed(0);
                           if (pos.getX() == -634.0) {
                              mob.setFh(2);
                              mob.setStance(2);
                           } else {
                              mob.setFh(22);
                              mob.setStance(2);
                           }

                           mob.setOverrideStats(cs);
                           this.spawnMonsterOnGroundBelow(mob, pos);
                        }
                     }
                  }

                  this.nextStageTime = 0L;
               }
            }
         }
      }
   }

   public int getAllMonsterSize() {
      int ret = 0;

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (mob.getId() != 9833004) {
            ret++;
         }
      }

      return ret;
   }

   public void nextStage() {
      if (this.stage <= 5) {
         this.stage++;
      }

      if (this.stage > 5) {
         this.clearGame = true;
      } else {
         this.player.send(CField.environmentChange("killing/stage/" + this.stage, 19, 0));
         this.player.send(CField.environmentChange("killing/first/start", 19, 0));
         this.sendStage();
      }
   }

   public void sendStage() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BOUNTY_HUNTING_STAGE.getValue());
      packet.writeInt(this.stage);
      this.player.send(packet.getPacket());
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.player = player;
      player.send(CField.startMapEffect("놈들이 사방에서 몰려오는군! 녀석들을 처치하면 막대한 경험치를 얻을 수 있다!", 5120159, true, 10));
      MapleMonster mob = MapleLifeFactory.getMonster(9833004);
      RandomPortal portal = player.getRandomPortal();
      if (portal != null) {
         int level = portal.getMobAvgLevel();
         ChangeableStats cs = new ChangeableStats(mob.getStats());
         cs.level = level;
         cs.hp = 247200L;
         mob.getStats().setLevel((short)level);
         mob.getStats().setMaxHp(247200L);
         mob.getStats().setHp(247200L);
         mob.setOverrideStats(cs);
         mob.setStance(2);
         this.spawnMonsterOnGroundBelow(mob, new Point(-10, 153));
         this.updateMonsterController(mob);
         player.updateOneInfo(15142, "gameType", String.valueOf(portal.getGameType().getType()));
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      this.player = null;
      this.displayStartGameTime = 0L;
      this.displayStartGame = false;
      this.startGameTime = 0L;
      this.startGame = false;
      this.endGameTime = 0L;
      this.endGame = false;
      this.clearGame = false;
      this.setFailGame(false);
      this.stage = 0;
      this.nextStageTime = 0L;
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventRandomPortal.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKRandomPortal.getQuestID());
   }

   public boolean isFailGame() {
      return this.failGame;
   }

   public void setFailGame(boolean failGame) {
      this.failGame = failGame;
   }

   static {
      List<Pair<Point, Integer>> stage1 = new ArrayList<>();
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      stage1.add(new Pair<>(new Point(-634, 153), 9830000));
      List<Pair<Point, Integer>> stage2 = new ArrayList<>();
      stage2.add(new Pair<>(new Point(-634, 153), 9830002));
      stage2.add(new Pair<>(new Point(634, 153), 9830002));
      stage2.add(new Pair<>(new Point(-634, 153), 9830003));
      stage2.add(new Pair<>(new Point(634, 153), 9830003));
      stage2.add(new Pair<>(new Point(-634, 153), 9830002));
      stage2.add(new Pair<>(new Point(634, 153), 9830002));
      stage2.add(new Pair<>(new Point(-634, 153), 9830003));
      stage2.add(new Pair<>(new Point(634, 153), 9830003));
      stage2.add(new Pair<>(new Point(-634, 153), 9830002));
      stage2.add(new Pair<>(new Point(634, 153), 9830002));
      stage2.add(new Pair<>(new Point(-634, 153), 9830003));
      stage2.add(new Pair<>(new Point(634, 153), 9830003));
      stage2.add(new Pair<>(new Point(-634, 153), 9830002));
      stage2.add(new Pair<>(new Point(634, 153), 9830002));
      stage2.add(new Pair<>(new Point(-634, 153), 9830003));
      stage2.add(new Pair<>(new Point(-634, 153), 9830003));
      stage2.add(new Pair<>(new Point(634, 153), 9830002));
      stage2.add(new Pair<>(new Point(-634, 153), 9830002));
      stage2.add(new Pair<>(new Point(634, 153), 9830003));
      stage2.add(new Pair<>(new Point(-634, 153), 9830003));
      stage2.add(new Pair<>(new Point(-634, 153), 9830004));
      List<Pair<Point, Integer>> stage3 = new ArrayList<>();
      stage3.add(new Pair<>(new Point(-634, 153), 9830005));
      stage3.add(new Pair<>(new Point(634, 153), 9830005));
      stage3.add(new Pair<>(new Point(-634, 153), 9830006));
      stage3.add(new Pair<>(new Point(634, 153), 9830006));
      stage3.add(new Pair<>(new Point(-634, 153), 9830005));
      stage3.add(new Pair<>(new Point(634, 153), 9830005));
      stage3.add(new Pair<>(new Point(-634, 153), 9830006));
      stage3.add(new Pair<>(new Point(634, 153), 9830006));
      stage3.add(new Pair<>(new Point(-634, 153), 9830005));
      stage3.add(new Pair<>(new Point(634, 153), 9830005));
      stage3.add(new Pair<>(new Point(-634, 153), 9830006));
      stage3.add(new Pair<>(new Point(634, 153), 9830007));
      stage3.add(new Pair<>(new Point(-634, 153), 9830005));
      stage3.add(new Pair<>(new Point(634, 153), 9830005));
      stage3.add(new Pair<>(new Point(-634, 153), 9830007));
      stage3.add(new Pair<>(new Point(634, 153), 9830007));
      stage3.add(new Pair<>(new Point(634, 153), 9830005));
      stage3.add(new Pair<>(new Point(-634, 153), 9830005));
      stage3.add(new Pair<>(new Point(634, 153), 9830007));
      stage3.add(new Pair<>(new Point(-634, 153), 9830007));
      stage3.add(new Pair<>(new Point(634, 153), 9830008));
      List<Pair<Point, Integer>> stage4 = new ArrayList<>();
      stage4.add(new Pair<>(new Point(634, 153), 9830009));
      stage4.add(new Pair<>(new Point(634, 153), 9830009));
      stage4.add(new Pair<>(new Point(634, 153), 9830009));
      stage4.add(new Pair<>(new Point(634, 153), 9830010));
      stage4.add(new Pair<>(new Point(634, 153), 9830010));
      stage4.add(new Pair<>(new Point(634, 153), 9830011));
      stage4.add(new Pair<>(new Point(634, 153), 9830011));
      stage4.add(new Pair<>(new Point(634, 153), 9830011));
      stage4.add(new Pair<>(new Point(634, 153), 9830012));
      stage4.add(new Pair<>(new Point(634, 153), 9830012));
      stage4.add(new Pair<>(new Point(-634, 153), 9830009));
      stage4.add(new Pair<>(new Point(-634, 153), 9830009));
      stage4.add(new Pair<>(new Point(-634, 153), 9830010));
      stage4.add(new Pair<>(new Point(-634, 153), 9830010));
      stage4.add(new Pair<>(new Point(-634, 153), 9830011));
      stage4.add(new Pair<>(new Point(-634, 153), 9830011));
      stage4.add(new Pair<>(new Point(-634, 153), 9830013));
      List<Pair<Point, Integer>> stage5 = new ArrayList<>();
      stage5.add(new Pair<>(new Point(-634, 153), 9830014));
      stage5.add(new Pair<>(new Point(-634, 153), 9830014));
      stage5.add(new Pair<>(new Point(-634, 153), 9830015));
      stage5.add(new Pair<>(new Point(-634, 153), 9830015));
      stage5.add(new Pair<>(new Point(-634, 153), 9830016));
      stage5.add(new Pair<>(new Point(-634, 153), 9830016));
      stage5.add(new Pair<>(new Point(-634, 153), 9830017));
      stage5.add(new Pair<>(new Point(-634, 153), 9830017));
      stage5.add(new Pair<>(new Point(634, 153), 9830014));
      stage5.add(new Pair<>(new Point(634, 153), 9830014));
      stage5.add(new Pair<>(new Point(634, 153), 9830015));
      stage5.add(new Pair<>(new Point(634, 153), 9830015));
      stage5.add(new Pair<>(new Point(634, 153), 9830016));
      stage5.add(new Pair<>(new Point(634, 153), 9830016));
      stage5.add(new Pair<>(new Point(634, 153), 9830018));
      mobLists.add(stage1);
      mobLists.add(stage2);
      mobLists.add(stage3);
      mobLists.add(stage4);
      mobLists.add(stage5);
   }
}
