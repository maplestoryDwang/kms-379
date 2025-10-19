package network.auction;

public enum AuctionResult {
   AuctionRes_NotInitialize(0),
   AuctionRes_RegisterItemDone(10),
   AuctionRes_ReRegisterItemDone(11),
   AuctionRes_CancelItemDone(12),
   AuctionRes_BuyItemDone(20),
   AuctionRes_BuyBundleItemDone(21),
   AuctionRes_PaymentReceiptDone(30),
   AuctionRes_ReturnItemDone(31),
   AuctionRes_SearchResult(40),
   AuctionRes_RegisterSaleItemList(40),
   AuctionRes_MarketPriceList(41),
   AuctionRes_RegisterWishItemDone(45),
   AuctionRes_WishList(46),
   AuctionRes_RemoveWishList(47),
   AuctionRes_MyItemList(50),
   AuctionRes_MyHistory(51),
   AuctionRes_RegisterItem(70),
   AuctionRes_RegisterHistoryItem(71),
   AuctionRes_BuyItem(72),
   AuctionRes_UpdateSaleItem(73),
   AuctionRes_Unknown(80);

   private int value;

   private AuctionResult(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }
}
