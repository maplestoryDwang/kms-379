package logging.entry;

public enum ConsumeLogType {
   PickupItem(0),
   TradeItem(1),
   StorageStore(2),
   StorageTakeOut(3),
   AuctionItemReceipt(4),
   NpcShopBuy(5),
   NpcShopSell(6),
   CashShopMesoBuy(7),
   CashShopCashBuy(8);

   private int type;

   private ConsumeLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
