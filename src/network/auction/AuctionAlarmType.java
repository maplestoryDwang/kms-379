package network.auction;

public enum AuctionAlarmType {
   NotSold(0),
   Sold(1),
   MakeBid(2);

   private int type;

   private AuctionAlarmType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
