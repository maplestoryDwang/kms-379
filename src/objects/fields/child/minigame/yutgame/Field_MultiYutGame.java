package objects.fields.child.minigame.yutgame;

import network.models.CField;
import network.models.FontColorType;
import network.models.FontType;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class Field_MultiYutGame extends Field {
   private boolean setGame = false;
   private boolean setItem = false;
   private long setItemTime = 0L;
   private MultiYutGameDlg gameDlg = null;
   private long nextTimeOutTurn = 0L;
   private int currentTeam = 0;
   private boolean auto = false;
   private long nextAutoTime = 0L;
   private boolean surrender = false;
   private boolean endGame = false;
   private boolean displayEndGame = false;
   private int winTeam = -1;
   private long endGameTime = 0L;

   public Field_MultiYutGame(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (!this.endGame) {
         if (this.endGameTime != 0L && this.endGameTime <= System.currentTimeMillis()) {
            this.winTeam = 2;
            this.endGame = true;
         } else {
            if (!this.setGame) {
               if (this.getCharactersSize() >= 2 && this.gameDlg == null) {
                  this.gameDlg = new MultiYutGameDlg(this);
                  this.gameDlg.init(this.getCharactersThreadsafe());
                  this.gameDlg.updateGameInfo();
                  this.setItemTime = System.currentTimeMillis() + 3000L;
                  this.endGameTime = System.currentTimeMillis() + 900000L;
                  this.broadcastMessage(CField.getClock(900));
                  this.setGame = true;
               }
            } else {
               if (!this.setItem && this.setItemTime <= System.currentTimeMillis()) {
                  this.gameDlg.firstSetSuperItem();
                  this.setItem = true;
               }

               YutGameResult_GameInfo gameInfo = this.gameDlg.getGameInfo(this.currentTeam);
               if (this.isAuto() && this.nextAutoTime <= System.currentTimeMillis()) {
                  this.autoPlay(gameInfo);
               }

               if (this.nextTimeOutTurn != 0L && this.nextTimeOutTurn <= System.currentTimeMillis() && !this.isAuto()) {
                  MapleCharacter player = this.getCharacterById(gameInfo.getPlayerID());
                  if (gameInfo.getFoulCount() >= 5) {
                     if (player != null) {
                        player.updateOneInfo(1234569, "miniGame1_can_time", String.valueOf(System.currentTimeMillis() + 900000L));
                        player.updateOneInfo(1234569, "miniGame1_result", "2");
                     }

                     this.setWinTeam(gameInfo.getTeam() ^ 1);
                     this.setEndGame(true);
                     return;
                  }

                  this.autoPlay(gameInfo);
                  gameInfo.setFoulCount(gameInfo.getFoulCount() + 1);
                  player.send(
                     CField.UIPacket.sendBigScriptProgressMessage(
                        "제한 시간 초과 " + gameInfo.getFoulCount() + "회. 5회 이상 시 퇴장 처리됩니다.", FontType.NanumGothic, FontColorType.Green
                     )
                  );
                  this.setAuto(true);
               }
            }
         }
      } else {
         if (!this.displayEndGame) {
            if (this.winTeam != 2 && !this.surrender) {
               this.gameDlg.gameResult(this.winTeam);
            }

            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               if (player.getRegisterTransferField() == 0) {
                  if (this.winTeam == 2) {
                     player.updateOneInfo(1234569, "miniGame1_result", "3");
                  } else if (player.getMiniGameTeam() == this.winTeam) {
                     player.updateOneInfo(1234569, "miniGame1_result", "1");
                  } else if (player.getOneInfoQuestInteger(1234569, "miniGame1_result") != 2) {
                     player.updateOneInfo(1234569, "miniGame1_result", "4");
                  }

                  player.setRegisterTransferField(993189801);
                  player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
                  player.setMiniGameTeam(-1);
               }
            }

            this.displayEndGame = true;
         }
      }
   }

   public void autoPlay(YutGameResult_GameInfo gameInfo) {
      if (gameInfo.isThrowYut() ? !this.gameDlg.movePieceByAuto(gameInfo) : !this.gameDlg.throwYut(gameInfo)) {
      }

      this.nextAutoTime = System.currentTimeMillis() + 2000L;
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setGame = false;
      this.setItem = false;
      this.setItemTime = 0L;
      this.gameDlg = null;
      this.nextTimeOutTurn = 0L;
      this.nextAutoTime = 0L;
      this.displayEndGame = false;
      this.setAuto(false);
      this.setCurrentTeam(0);
      this.setEndGame(false);
      this.setWinTeam(-1);
      this.setSurrender(false);
      this.endGameTime = 0L;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   public MultiYutGameDlg getYutGameDlg() {
      return this.gameDlg;
   }

   public long getNextTimeOutTurn() {
      return this.nextTimeOutTurn;
   }

   public void setNextTimeOutTurn(long nextTimeOutTurn) {
      this.nextTimeOutTurn = nextTimeOutTurn;
   }

   public int getCurrentTeam() {
      return this.currentTeam;
   }

   public void setCurrentTeam(int currentTeam) {
      this.currentTeam = currentTeam;
   }

   public boolean isAuto() {
      return this.auto;
   }

   public void setAuto(boolean auto) {
      this.auto = auto;
   }

   public boolean isEndGame() {
      return this.endGame;
   }

   public void setEndGame(boolean endGame) {
      this.endGame = endGame;
   }

   public int getWinTeam() {
      return this.winTeam;
   }

   public void setWinTeam(int winTeam) {
      this.winTeam = winTeam;
   }

   public boolean isSurrender() {
      return this.surrender;
   }

   public void setSurrender(boolean surrender) {
      this.surrender = surrender;
   }
}
