package objects.fields.child.minigame.soccer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.gameobject.Drop;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.skills.TemporarySkill;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;

public class Field_MultiSoccer extends Field {
   public static final int INIT_GAME = 1;
   public static final int SET_TIMER = 2;
   public static final int START_GAME = 3;
   public static final int END_GAME = 4;
   public static final int PLAYER_COUNT = 10;
   public static final int DURATION = 180000;
   public static final int WIN_GAME = 0;
   public static final int LOSE_GAME = 1;
   public static final int DRAW_GAME = 2;
   public boolean init;
   public boolean startGame;
   public boolean endGame;
   public boolean warp;
   public int state;
   public long startGameTime;
   public long endGameTime;
   public long warpTime;
   public List<Drop> ballItem;
   public List<MultiSoccerPlayer> playerList;

   public Field_MultiSoccer(int mapID, int channel, int returnMapId, float monsterRate) {
      super(mapID, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();

      try {
         if (!this.init) {
            this.initGame();
         } else if (!this.startGame) {
            if (this.startGameTime < System.currentTimeMillis()) {
               this.startGame();
            }
         } else if (!this.endGame) {
            if (this.endGameTime < System.currentTimeMillis()) {
               this.endGame();
            } else {
               this.checkGame();
            }
         } else if (!this.warp && this.warpTime < System.currentTimeMillis()) {
            this.warp();
         }
      } catch (Exception var2) {
         System.out.println("Soccer Err");
         var2.printStackTrace();
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.init = false;
      this.startGame = false;
      this.endGame = false;
      this.warp = false;
      this.state = -1;
      this.startGameTime = 0L;
      this.endGameTime = 0L;
      this.warpTime = 0L;
      this.playerList = new ArrayList<>();
      this.ballItem = new ArrayList<>();
   }

   public void warp() {
      this.warp = true;
      this.playerList.forEach(player -> player.getPlayer().warp(993195101));
   }

   public void endGame() {
      this.endGame = true;
      this.warpTime = System.currentTimeMillis() + 5000L;
      this.setState(4);
      int redScore = this.playerList.stream().filter(player -> player.getTeam() == 0).mapToInt(MultiSoccerPlayer::getScore).sum();
      int blueScore = this.playerList.stream().filter(player -> player.getTeam() == 1).mapToInt(MultiSoccerPlayer::getScore).sum();
      List<MultiSoccerPlayer> redTeam = this.playerList.stream().filter(player -> player.getTeam() == 0).collect(Collectors.toList());
      List<MultiSoccerPlayer> blueTeam = this.playerList.stream().filter(player -> player.getTeam() == 1).collect(Collectors.toList());
      if (redScore > blueScore) {
         blueTeam.forEach(player -> {
            player.getPlayer().send(CField.environmentChange("UI/UIWindow5.img/2021WaterGunGame/Effect/result/lose", 16));
            player.getPlayer().send(MultiSoccerPacket.onSetMultiSoccerResult(1));
            player.getPlayer().updateOneInfo(1234569, "miniGame3_result", "4");
         });
         redTeam.forEach(player -> {
            player.getPlayer().send(CField.environmentChange("UI/UIWindow5.img/2021WaterGunGame/Effect/result/win", 16));
            player.getPlayer().send(MultiSoccerPacket.onSetMultiSoccerResult(0));
            player.getPlayer().updateOneInfo(1234569, "miniGame3_result", "1");
         });
      } else if (redScore < blueScore) {
         redTeam.forEach(player -> {
            player.getPlayer().send(CField.environmentChange("UI/UIWindow5.img/2021WaterGunGame/Effect/result/lose", 16));
            player.getPlayer().send(MultiSoccerPacket.onSetMultiSoccerResult(1));
            player.getPlayer().updateOneInfo(1234569, "miniGame3_result", "4");
         });
         blueTeam.forEach(player -> {
            player.getPlayer().send(CField.environmentChange("UI/UIWindow5.img/2021WaterGunGame/Effect/result/win", 16));
            player.getPlayer().send(MultiSoccerPacket.onSetMultiSoccerResult(0));
            player.getPlayer().updateOneInfo(1234569, "miniGame3_result", "1");
         });
      } else {
         this.broadcastMessage(CField.environmentChange("UI/UIWindow5.img/2021WaterGunGame/Effect/result/draw", 16));
         this.broadcastMessage(MultiSoccerPacket.onSetMultiSoccerResult(2));
         this.playerList.forEach(player -> player.getPlayer().updateOneInfo(1234569, "miniGame3_result", "3"));
      }
   }

   public void checkGame() {
      Iterator<Drop> iter = this.ballItem.iterator();
      this.broadcastMessage(MultiSoccerPacket.onMultiSoccerInit(this.playerList));

      while (iter.hasNext()) {
         Drop ball = iter.next();
         MultiSoccerPlayer player = this.getPlayer(ball.getOwnerID());
         if (player != null && ball.getTruePosition().y >= -80 && ball.getTruePosition().y <= 120) {
            if (ball.getTruePosition().x <= -940) {
               if (player.getTeam() == 1) {
                  player.setScore(player.getScore() + 10);
                  this.broadcastMessage(MultiSoccerPacket.onSetMultiSoccerGoalPlayer(player));
               }

               this.resetBall(ball);
            } else if (ball.getTruePosition().x >= 940) {
               if (player.getTeam() == 0) {
                  player.setScore(player.getScore() + 10);
                  this.broadcastMessage(MultiSoccerPacket.onSetMultiSoccerGoalPlayer(player));
               }

               this.resetBall(ball);
            }
         }
      }
   }

   public void startGame() {
      this.startGame = true;
      this.setQuestInfoReset();
      this.showMsg();
      this.setState(3);
   }

   public void initGame() {
      this.init = true;
      this.startGameTime = System.currentTimeMillis() + 3000L;
      this.setState(1);
      this.showEffect();
      this.setState(2);
   }

   private void addPlayer() {
      this.playerList = new ArrayList<>();

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         this.playerList.add(new MultiSoccerPlayer(this.playerList.size() % 2, player));
         this.broadcastMessage(MultiSoccerPacket.onMultiSoccerInit(this.playerList));
         this.broadcastMessage(MultiSoccerPacket.onSetMultiSoccerAddPlayer(player.getId()));
      }
   }

   public void stunPlayer(List<Integer> targetList) {
      targetList.forEach(
         targetID -> {
            MapleCharacter target = this.getCharacterById(targetID);
            if (target != null) {
               target.giveDebuff(SecondaryStatFlag.Stun, 1, 0, 2000L, 80003086, 1);
               if (target.getBuffedValue(SecondaryStatFlag.MultiSoccerAddBall) != null) {
                  target.temporaryStatReset(SecondaryStatFlag.MultiSoccerAddBall);
                  Point pos = new Point(target.getTruePosition().x + (target.isFacingLeft() ? -50 : 50), target.getTruePosition().y);
                  Drop drop = new Drop(new Item(2633855, (byte)0, (short)1), pos);
                  drop.setSpecialType(1);
                  drop.setDropMotionType(0);
                  drop.setDropSpeed(100);
                  drop.setOwnerID(0);
                  drop.setUnk1(700);
                  drop.setCollisionPickUp(true);
                  this.spawnAndAddRangedMapObject(
                     drop, c -> c.getSession().writeAndFlush(CField.dropItemFromMapObject(drop, target.getTruePosition(), pos, (byte)2))
                  );
                  this.ballItem.add(drop);
               }
            }
         }
      );
   }

   public void setState(int nState) {
      this.state = nState;
      this.broadcastMessage(MultiSoccerPacket.onSetMultiSoccerState(this.state));
      switch (this.state) {
         case 1:
            this.setTemporarySkill(true);
            this.addPlayer();
            break;
         case 3:
            this.setTimer(180000);
            this.initBall();
      }
   }

   public void resetBall(Drop ball) {
      this.ballItem.remove(ball);
      this.broadcastMessage(CField.removeItemFromMap(ball.getObjectId(), 0, 0));
      int[] yPos = new int[]{-130, 30, 190, 350};
      Point pos = new Point(-2, yPos[Randomizer.nextInt(yPos.length)]);
      Drop drop = new Drop(new Item(2633855, (byte)0, (short)1), pos);
      drop.setSpecialType(1);
      drop.setDropMotionType(0);
      drop.setDropSpeed(100);
      drop.setOwnerID(0);
      drop.setUnk1(700);
      drop.setCollisionPickUp(true);
      this.spawnAndAddRangedMapObject(drop, c -> c.getSession().writeAndFlush(CField.dropItemFromMapObject(drop, pos, pos, (byte)2)));
      this.ballItem.add(drop);
   }

   public void shootBall(MapleCharacter player, int destX, int destY) {
      player.getClient().getSession().writeAndFlush(CWvsContext.enableActions(player));
      if (player.getBuffedValue(SecondaryStatFlag.MultiSoccerAddBall) != null) {
         player.temporaryStatReset(SecondaryStatFlag.MultiSoccerAddBall);
         Point dropTo = new Point(destX, destY);
         Drop drop = new Drop(new Item(2633910, (byte)0, (short)1), dropTo);
         drop.setSpecialType(1);
         drop.setDropMotionType(5);
         drop.setDropSpeed(100);
         drop.setOwnerID(player.getId());
         drop.setUnk1(700);
         drop.setCollisionPickUp(true);
         this.spawnAndAddRangedMapObject(drop, c -> c.getSession().writeAndFlush(CField.dropItemFromMapObject(drop, player.getTruePosition(), dropTo, (byte)1)));
         this.ballItem.add(drop);
         player.send(CWvsContext.enableActions(player));
         player.addCooldown(80003087, 2000L, System.currentTimeMillis());
         player.send(CField.skillCooldown(80003087, 2000));
         player.send(CField.temporarySkillCooldown(80003087, 2000));
      }
   }

   public void updateBall(MapleCharacter target, Drop drop) {
      for (Drop ball : this.ballItem) {
         if (ball.getObjectId() == drop.getObjectId()) {
            target.getClient().getSession().writeAndFlush(CField.removeItemFromMap(ball.getObjectId(), 2, target.getId()));
            this.broadcastMessage(target, CField.removeItemFromMap(ball.getObjectId(), 0, target.getId()), false);
            target.getClient().getSession().writeAndFlush(CField.playMiniGameSound("MultiSoccer/getball_people"));
            target.temporaryStatSet(80003089, Integer.MAX_VALUE, SecondaryStatFlag.MultiSoccerAddBall, 1);
            this.removeMapObject(ball);
            this.ballItem.remove(ball);
            return;
         }
      }
   }

   public void initBall() {
      this.ballItem = new ArrayList<>();
      int[] yPos = new int[]{-130, 190, 350};

      for (int i = 0; i < 3; i++) {
         Point pos = new Point(-2, yPos[i]);
         Drop drop = new Drop(new Item(2633855, (byte)0, (short)1), pos);
         drop.setSpecialType(1);
         drop.setDropMotionType(0);
         drop.setDropSpeed(100);
         drop.setOwnerID(0);
         drop.setUnk1(700);
         drop.setCollisionPickUp(true);
         this.spawnAndAddRangedMapObject(drop, c -> c.getSession().writeAndFlush(CField.dropItemFromMapObject(drop, pos, pos, (byte)2)));
         this.ballItem.add(drop);
      }
   }

   public MultiSoccerPlayer getPlayer(int charID) {
      return this.playerList.stream().filter(player -> player.getPlayer().getId() == charID).findFirst().orElse(null);
   }

   public void setTimer(int nTime) {
      this.endGameTime = System.currentTimeMillis() + nTime;
      this.broadcastMessage(MultiSoccerPacket.onSetMultiSoccerTime(nTime));
   }

   public void showEffect() {
      this.broadcastMessage(CWvsContext.showNewEffect("2021MultiSoccer_4", "2021MultiSoccer_", false));
   }

   public void showMsg() {
      this.broadcastMessage(CField.sendWeatherEffectNotice(0, 10000, true, "   방향키로 이동하고 CTRL 또는 ALT키로 스킬을 사용할 수 있습니다.   "));
      this.broadcastMessage(CField.addPopupSay(9062546, 4000, "경기~~ 시작합니다~~~!!!", ""));
   }

   public void setQuestInfoReset() {
      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         player.updateOneInfo(1234569, "miniGame3_result", "2");
         player.updateInfoQuest(100891, "owngoal=0;score=0;lazy=0;result=giveup");
      }
   }

   public void setTemporarySkill(boolean start) {
      if (start) {
         TemporarySkill skill1 = new TemporarySkill((byte)0, 80003086, (byte)1, 0, 0, 0);
         skill1.setUnk3(3000);
         TemporarySkill skill2 = new TemporarySkill((byte)0, 80003087, (byte)1, 0, 0, 0);
         skill2.setUnk3(3000);
         skill2.setProcessKeyAtDisable(true);
         this.broadcastMessage(CField.setTemporarySkill(27, new TemporarySkill[]{skill1, skill2}));
      } else {
         this.broadcastMessage(CField.setTemporarySkill(0, new TemporarySkill[0]));
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      this.setTemporarySkill(false);
      player.temporaryStatReset(SecondaryStatFlag.MultiSoccerAddBall);
   }
}
