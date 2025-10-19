package logging.entry;

public enum AuctionLogType {
   RegisterItem(0),
   BuyItem(1),
   ReRegisterItem(2),
   RegisterCancel(3),
   GetMeso(4),
   GetItem(5),
   HackLog(6),
   SellDone(7);

   private int type;

   private AuctionLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
