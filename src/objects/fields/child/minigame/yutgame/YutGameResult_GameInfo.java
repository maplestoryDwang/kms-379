package objects.fields.child.minigame.yutgame;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class YutGameResult_GameInfo extends YutGameResultEntry {
   private int team;
   private int playerID;
   private YutGameSuperItem superItem = null;
   private YutGamePiece[] pieces = null;
   private YutGameYutInfo currentYutInfo = null;
   private boolean nextTurn = false;
   private boolean throwYut = false;
   private boolean disableYutItem = false;
   private boolean activeBackStep = false;
   private boolean activeGoHome = false;
   private int foulCount = 0;
   public static int MAX_PIECE_COUNT = 4;

   public void copy(YutGameResult_GameInfo info) {
   }

   @Override
   public YutGameResultType getType() {
      return YutGameResultType.YutGameInfo;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(YutGameResultType.YutGameInfo.getType());
      packet.writeInt(this.getTeam());
      packet.write(1);
      packet.write(1);
      packet.write(this.isNextTurn());
      packet.write(this.isActiveBackStep());
      packet.write(this.isDisableYutItem());
      packet.write(0);
      packet.writeInt(this.getTeam());
      packet.writeInt(this.getPlayerID());
      packet.writeInt(this.isNextTurn() ? 1 : 0);
      packet.writeInt(this.isActiveGoHome() ? 1 : -1);
      this.getSuperItem().encode(packet);

      for (YutGamePiece piece : this.getPieces()) {
         piece.encode(packet);
      }

      this.getCurrentYutInfo().encode(packet);
   }

   public int getTeam() {
      return this.team;
   }

   public void setTeam(int team) {
      this.team = team;
   }

   public int getPlayerID() {
      return this.playerID;
   }

   public void setPlayerID(int playerID) {
      this.playerID = playerID;
   }

   public YutGameSuperItem getSuperItem() {
      return this.superItem;
   }

   public void setSuperItem(YutGameSuperItem superItem) {
      this.superItem = superItem;
   }

   public YutGamePiece[] getPieces() {
      return this.pieces;
   }

   public YutGamePiece getPiece(int index) {
      return this.pieces[index];
   }

   public List<YutGamePiece> getPieceByPosition(int position) {
      List<YutGamePiece> ret = new ArrayList<>();

      for (YutGamePiece piece : this.pieces) {
         if (piece.getPosition() == position) {
            ret.add(piece);
         }
      }

      return ret;
   }

   public boolean isAllPieceInHome() {
      for (YutGamePiece piece : this.pieces) {
         if (piece.getPosition() != 0 && piece.getPosition() != 30) {
            return false;
         }
      }

      return true;
   }

   public boolean allPieceGoalIn() {
      for (YutGamePiece piece : this.pieces) {
         if (piece.getPosition() < 30) {
            return false;
         }
      }

      return true;
   }

   public List<Integer> checkPieceCatch(int index) {
      List<Integer> ret = new ArrayList<>();
      if (index == 30) {
         return ret;
      } else {
         for (YutGamePiece piece : this.pieces) {
            if (piece.getPosition() == index) {
               ret.add(index);
            }
         }

         return ret;
      }
   }

   public void setPieces(YutGamePiece[] pieces) {
      this.pieces = pieces;
   }

   public YutGameYutInfo getCurrentYutInfo() {
      return this.currentYutInfo;
   }

   public void setCurrentYutInfo(YutGameYutInfo currentYutInfo) {
      this.currentYutInfo = currentYutInfo;
   }

   public boolean isNextTurn() {
      return this.nextTurn;
   }

   public void setNextTurn(boolean nextTurn) {
      this.nextTurn = nextTurn;
   }

   public boolean isThrowYut() {
      return this.throwYut;
   }

   public void setThrowYut(boolean throwYut) {
      this.throwYut = throwYut;
   }

   public int getFoulCount() {
      return this.foulCount;
   }

   public void setFoulCount(int foulCount) {
      this.foulCount = foulCount;
   }

   public boolean isDisableYutItem() {
      return this.disableYutItem;
   }

   public void setDisableYutItem(boolean disableYutItem) {
      this.disableYutItem = disableYutItem;
   }

   public boolean isActiveBackStep() {
      return this.activeBackStep;
   }

   public void setActiveBackStep(boolean activeBackStep) {
      this.activeBackStep = activeBackStep;
   }

   public boolean isActiveGoHome() {
      return this.activeGoHome;
   }

   public void setActivePieceItem(boolean activeGoHome) {
      this.activeGoHome = activeGoHome;
   }
}
