package objects.fields.child.minigame.yutgame;

import network.encode.PacketEncoder;

public class YutGameSuperItem {
   private YutGameSuperItem.YutItem yutItem = null;
   private YutGameSuperItem.PieceItem pieceItem = null;
   private int activeYutItem = -1;
   private int activePieceItem = -1;

   public YutGameSuperItem() {
      this.yutItem = new YutGameSuperItem.YutItem();
      this.pieceItem = new YutGameSuperItem.PieceItem();
   }

   public void encode(PacketEncoder packet) {
      this.getYutItem().encode(packet);
      this.getPieceItem().encode(packet);
   }

   public YutGameSuperItem.YutItem getYutItem() {
      return this.yutItem;
   }

   public void setYutItem(YutGameSuperItem.YutItemType type) {
      this.yutItem.usable = true;
      this.yutItem.yutItemType = type;
   }

   public YutGameSuperItem.PieceItem getPieceItem() {
      return this.pieceItem;
   }

   public void setPieceItem(YutGameSuperItem.PieceItemType type) {
      this.pieceItem.usable = true;
      this.pieceItem.pieceItemType = type;
   }

   public int getActiveYutItem() {
      return this.activeYutItem;
   }

   public void setActiveYutItem(int activeYutItem) {
      this.activeYutItem = activeYutItem;
   }

   public int getActivePieceItem() {
      return this.activePieceItem;
   }

   public void setActivePieceItem(int activePieceItem) {
      this.activePieceItem = activePieceItem;
   }

   public class PieceItem {
      private YutGameSuperItem.PieceItemType pieceItemType = YutGameSuperItem.PieceItemType.GoHome;
      private boolean usable = false;

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.getPieceItemType().getType());
         packet.write(this.isUsable());
         packet.writeInt(0);
      }

      public YutGameSuperItem.PieceItemType getPieceItemType() {
         return this.pieceItemType;
      }

      public void setPieceItemType(YutGameSuperItem.PieceItemType pieceItemType) {
         this.pieceItemType = pieceItemType;
      }

      public boolean isUsable() {
         return this.usable;
      }

      public void setUsable(boolean usable) {
         this.usable = usable;
      }
   }

   public static enum PieceItemType {
      GoHome(0),
      CarryAndGo(1),
      ChangePosition(2),
      GoMyFront(3),
      BackHug(4),
      ReverseCell(5),
      FinishLineFront(6),
      InstallBomb(7);

      private int type;

      private PieceItemType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static YutGameSuperItem.PieceItemType getType(int type) {
         for (YutGameSuperItem.PieceItemType t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }

   public class YutItem {
      private YutGameSuperItem.YutItemType yutItemType = YutGameSuperItem.YutItemType.APigAndADog;
      private boolean usable = false;

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.getYutItemType().getType());
         packet.write(this.isUsable());
         packet.writeInt(0);
      }

      public YutGameSuperItem.YutItemType getYutItemType() {
         return this.yutItemType;
      }

      public void setYutItemType(YutGameSuperItem.YutItemType yutItemType) {
         this.yutItemType = yutItemType;
      }

      public boolean isUsable() {
         return this.usable;
      }

      public void setUsable(boolean usable) {
         this.usable = usable;
      }
   }

   public static enum YutItemType {
      APigAndADog(0),
      UnconditionalASheep(1),
      Reverse(2),
      DoubleDouble(3),
      AHorseOrAPig(4),
      ACowOrAHorse(5),
      NextTurn(6),
      BackStep(7);

      private int type;

      private YutItemType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static YutGameSuperItem.YutItemType getType(int type) {
         for (YutGameSuperItem.YutItemType t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }
}
