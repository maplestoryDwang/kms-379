package objects.fields.child.minigame.topgame;

import network.game.GameServer;
import network.models.CField;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class Field_BuzzingHouse extends Field {
   private long startGameTime = 0L;
   private boolean gameStart = false;
   private boolean endGame = false;
   private long endGameTime = 0L;
   private MapleCharacter player = null;

   public Field_BuzzingHouse(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (!this.gameStart && this.endGameTime == 0L && this.startGameTime != 0L && this.startGameTime <= System.currentTimeMillis()) {
         this.player.send(CField.getClock(300));
         this.player.send(CField.getBuzzingHouseRequest(180, 5));
         this.player.send(CField.getBuzzingHouseResult(1));
         this.player.send(CField.getBuzzingHouseResult(2));
         this.endGameTime = System.currentTimeMillis() + 300000L;
         this.gameStart = true;
      }

      if (!this.endGame && this.endGameTime != 0L && this.endGameTime <= System.currentTimeMillis()) {
         int neoGem = (this.player.getBuzzingHouseBlockCount() - this.player.getBuzzingHousePerfectCount()) * 5
            + this.player.getBuzzingHousePerfectCount() * 10;
         this.player.updateOneInfo(1234569, "miniGame2_coin", String.valueOf(neoGem));
         this.player.updateOneInfo(1234569, "miniGame2_top", String.valueOf(this.player.getBuzzingHouseBlockCount()));
         this.player.send(CField.getBuzzingHouseCoinCount(neoGem));
         this.player.send(CField.getBuzzingHouseResult(3));
         this.player.send(CField.UIPacket.setStandAloneMode(false));
         this.player.send(CField.UIPacket.IntroLock(false));
         GameServer gameServer = this.player.getClient().getChannelServer();
         Field target = gameServer.getMapFactory().getMap(993190700);
         this.player.changeMap(target, target.getPortal(0));
         this.player.setStackEventGauge();
         this.player.setBuzzingHouseBlockCount(0);
         this.player.setBuzzingHousePerfectCount(0);
         this.endGame = false;
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.startGameTime = 0L;
      this.setEndGameTime(0L);
      this.gameStart = false;
      this.endGame = false;
      this.player = null;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      this.resetFully(true);
      super.onEnter(player);
      this.player = player;
      player.send(CField.onUserTeleport(65535, 0));
      player.send(CField.UIPacket.setStandAloneMode(true));
      player.send(CField.UIPacket.IntroLock(true));
      this.startGameTime = System.currentTimeMillis() + 3000L;
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   public long getEndGameTime() {
      return this.endGameTime;
   }

   public void setEndGameTime(long endGameTime) {
      this.endGameTime = endGameTime;
   }
}
