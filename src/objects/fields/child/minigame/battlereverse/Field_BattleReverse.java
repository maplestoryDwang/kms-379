package objects.fields.child.minigame.battlereverse;

import network.models.CField;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class Field_BattleReverse extends Field {
   private boolean setGame = false;
   private boolean setBoard = false;
   private long setBoardTime = 0L;
   private BattleReverseGameDlg gameDlg = null;
   private BattleReverseGameInfo gameInfo = null;
   private long nextTimeOutTurn = 0L;
   private int currentTeam = 0;
   private boolean auto = false;
   private long nextAutoTime = 0L;
   private boolean surrender = false;
   private boolean endGame = false;
   private boolean displayEndGame = false;
   private int winTeam = -1;
   private long endGameTime = 0L;

   public Field_BattleReverse(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.setBoard && this.getCharactersSize() == 0) {
         this.resetFully();
      }

      if (!this.endGame) {
         if (this.endGameTime != 0L && this.endGameTime <= System.currentTimeMillis()) {
            this.winTeam = 2;
            this.endGame = true;
         } else {
            if (!this.setGame && this.getCharactersSize() >= 2 && this.gameDlg == null) {
               this.gameDlg = new BattleReverseGameDlg(this);
               this.gameDlg.init(this.getCharactersThreadsafe());
               this.gameDlg.updateGameInfo();
               this.gameInfo = this.gameDlg.getGameInfo();
               this.setBoardTime = System.currentTimeMillis();
               this.endGameTime = System.currentTimeMillis() + 900000L;
               this.broadcastMessage(CField.getClock(900));
               this.setGame = true;
            }

            if (this.setGame) {
               if (this.getCharactersSize() == 1) {
                  MapleCharacter chr = this.getCharacters().get(0);
                  int winteam = this.gameInfo.getTeamByChr(chr);
                  this.setWinTeam(winteam);
                  this.setEndGame(true);
               }

               if (this.gameInfo.getTeamHP(0) <= 0) {
                  this.setWinTeam(1);
                  this.setEndGame(true);
               }

               if (this.gameInfo.getTeamHP(1) <= 0) {
                  this.setWinTeam(0);
                  this.setEndGame(true);
               }
            }
         }
      } else {
         if (!this.displayEndGame) {
            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               if (player.getRegisterTransferField() == 0) {
                  if (this.winTeam == 2) {
                     player.send(BattleReversePacket.EndBattleReverse(2));
                     player.updateOneInfo(1234569, "miniGame_br_result", "3");
                  } else if (player.getMiniGameTeam() == this.winTeam) {
                     player.send(BattleReversePacket.EndBattleReverse(1));
                     player.updateOneInfo(1234569, "miniGame_br_result", "1");
                  } else if (player.getOneInfoQuestInteger(1234569, "miniGame1_result") != 2) {
                     player.send(BattleReversePacket.EndBattleReverse(0));
                     player.updateOneInfo(1234569, "miniGame_br_result", "4");
                  }

                  player.setRegisterTransferField(993189601);
                  player.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
                  player.setMiniGameTeam(-1);
               }
            }

            this.displayEndGame = true;
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setGame = false;
      this.setBoard = false;
      this.setBoardTime = 0L;
      this.gameDlg = null;
      this.gameInfo = null;
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

   public BattleReverseGameDlg getBattleReverseGameDlg() {
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
