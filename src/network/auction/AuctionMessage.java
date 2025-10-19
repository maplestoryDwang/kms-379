package network.auction;

public enum AuctionMessage {
   SUCCESS(0),
   UNKNOWN_ERROR(1),
   NOT_EXSIST_ITEM(102),
   NOT_ENOUGH_SELL_SLOT(103),
   CAN_NOT_BUY_OWN_ITEM(104),
   CAN_NOT_BUY_BUNDLE_ITEM(105),
   NOT_ENOUGH_MONEY(106),
   NO_MATCH_MONEY(107),
   NOT_ENOUGH_INVENTORY_SLOT(116);

   private int value;

   private AuctionMessage(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }
}
