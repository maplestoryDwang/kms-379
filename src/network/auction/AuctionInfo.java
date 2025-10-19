package network.auction;

public class AuctionInfo {
   private int cid;
   private long bid;
   private byte status;

   public AuctionInfo(long bid, int cid, byte status) {
      this.bid = bid;
      this.cid = cid;
      this.status = status;
   }

   public void setBid(long bid) {
      this.bid = bid;
   }

   public long getBid() {
      return this.bid;
   }

   public int getCharacterId() {
      return this.cid;
   }

   public void setStatus(byte status) {
      this.status = status;
   }

   public byte getStatus() {
      return this.status;
   }
}
