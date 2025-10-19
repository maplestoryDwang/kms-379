package network.auction;

import objects.item.Item;

public class AuctionItemPackage {
   private long expiredTime;
   private long buyTime;
   private long startTime;
   private long bid = 0L;
   private long mesos = 0L;
   private Item item;
   private boolean bargain;
   private int ownerid;
   private int accountid;
   private int buyer;
   private int type;
   private String ownername;
   private int historyID;

   public AuctionItemPackage(
      int ownerid,
      int accountid,
      String ownername,
      Item item,
      long bid,
      long mesos,
      long expiredTime,
      boolean bargain,
      int buyer,
      long buyTime,
      long startTime,
      int type,
      int historyID
   ) {
      this.ownerid = ownerid;
      this.accountid = accountid;
      this.ownername = ownername;
      this.item = item;
      this.bid = bid;
      this.mesos = mesos;
      this.expiredTime = expiredTime;
      this.bargain = bargain;
      this.buyer = buyer;
      this.buyTime = buyTime;
      this.startTime = startTime;
      this.type = type;
      this.historyID = historyID;
   }

   public int getOwnerId() {
      return this.ownerid;
   }

   public String getOwnerName() {
      return this.ownername;
   }

   public void setExpiredTime(long expiredTime) {
      this.expiredTime = expiredTime;
   }

   public long getExpiredTime() {
      return this.expiredTime;
   }

   public void setBuyTime(long buyTime) {
      this.buyTime = buyTime;
   }

   public long getBuyTime() {
      return this.buyTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public long getBid() {
      return this.bid;
   }

   public void setBid(long set) {
      this.bid = set;
   }

   public long getMesos() {
      return this.mesos;
   }

   public void setMesos(long set) {
      this.mesos = set;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean isBargain() {
      return this.bargain;
   }

   public int getBuyer() {
      return this.buyer;
   }

   public void setBuyer(int buyer) {
      this.buyer = buyer;
   }

   public int getType(boolean isOwner, boolean isReal) {
      if (isReal) {
         return this.type;
      } else if (this.type == 13) {
         return isOwner ? 8 : 2;
      } else if (this.type == 14) {
         return isOwner ? 3 : 7;
      } else if (this.type == 15) {
         return isOwner ? 8 : 7;
      } else if (this.type == 2) {
         return isOwner ? 3 : 2;
      } else {
         return this.type;
      }
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getHistoryID() {
      return this.historyID;
   }

   public void setHistoryID(int historyID) {
      this.historyID = historyID;
   }

   public int getAccountID() {
      return this.accountid;
   }
}
