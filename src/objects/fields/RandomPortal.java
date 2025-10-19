package objects.fields;

import java.awt.Point;
import network.encode.PacketEncoder;

public class RandomPortal {
   private RandomPortalType type = null;
   private int objectID;
   private Point position;
   private int ownerID;
   private RandomPortalGameType gameType;
   private int mobAvgLevel = 0;
   private long mobAvgExp = 0L;
   private long mobAvgHp = 0L;

   public RandomPortal(RandomPortalType type, int objectID, Point position, int ownerID, RandomPortalGameType gameType) {
      this.setType(type);
      this.setObjectID(objectID);
      this.setPosition(position);
      this.setOwnerID(ownerID);
      this.setGameType(gameType);
   }

   public RandomPortalType getType() {
      return this.type;
   }

   public void setType(RandomPortalType type) {
      this.type = type;
   }

   public int getObjectID() {
      return this.objectID;
   }

   public void setObjectID(int objectID) {
      this.objectID = objectID;
   }

   public Point getPosition() {
      return this.position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   public int getOwnerID() {
      return this.ownerID;
   }

   public void setOwnerID(int ownerID) {
      this.ownerID = ownerID;
   }

   public void encode(PacketEncoder packet) {
      packet.write(this.type.getType());
      packet.writeInt(this.objectID);
      packet.encodePos(this.position);
      packet.writeInt(0);
      packet.writeInt(this.ownerID);
   }

   public RandomPortalGameType getGameType() {
      return this.gameType;
   }

   public void setGameType(RandomPortalGameType gameType) {
      this.gameType = gameType;
   }

   public int getMobAvgLevel() {
      return this.mobAvgLevel;
   }

   public void setMobAvgLevel(int mobAvgLevel) {
      this.mobAvgLevel = mobAvgLevel;
   }

   public long getMobAvgExp() {
      return this.mobAvgExp;
   }

   public void setMobAvgExp(long mobAvgExp) {
      this.mobAvgExp = mobAvgExp;
   }

   public long getMobAvgHp() {
      return this.mobAvgHp;
   }

   public void setMobAvgHp(long mobAvgHp) {
      this.mobAvgHp = mobAvgHp;
   }

   public int getMapID() {
      switch (this.gameType) {
         case EagleHunt:
            return 993000200;
         case ReceivingTreasure:
            return 993000430;
         case StealDragonsEgg:
            return 993000300;
         case CourtshipDance:
            return 993000400;
         case StormwingArea:
            return 993000650;
         case ProtectPollo:
            return 993000000;
         case GuardTheCastleGates:
            return 993000100;
         case MidnightMonsterHunting:
            return 993000130;
         case FireWolf:
            return 993000500;
         default:
            return 15;
      }
   }
}
