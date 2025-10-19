package objects.users;

import network.encode.PacketEncoder;
import network.models.PacketHelper;

public class BuyLimitEntry {
   private int shopID;
   private int itemIndex;
   private int itemID;
   private int buyCount;
   private long buyTime;

   public BuyLimitEntry(int shopID, int itemIndex, int itemID, int buyCount, long buyTime) {
      this.setShopID(shopID);
      this.setItemIndex(itemIndex);
      this.setItemID(itemID);
      this.setBuyCount(buyCount);
      this.setBuyTime(buyTime);
   }

   public int getShopID() {
      return this.shopID;
   }

   public void setShopID(int shopID) {
      this.shopID = shopID;
   }

   public int getItemIndex() {
      return this.itemIndex;
   }

   public void setItemIndex(int itemIndex) {
      this.itemIndex = itemIndex;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public int getBuyCount() {
      return this.buyCount;
   }

   public void setBuyCount(int buyCount) {
      this.buyCount = buyCount;
   }

   public long getBuyTime() {
      return this.buyTime;
   }

   public void setBuyTime(long buyTime) {
      this.buyTime = buyTime;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.shopID);
      packet.writeShort(this.itemIndex);
      packet.writeInt(this.itemID);
      packet.writeShort(this.buyCount);
      packet.writeLong(PacketHelper.getKoreanTimestamp(this.buyTime));
      packet.writeMapleAsciiString("");
      packet.writeInt(0);
   }
}
