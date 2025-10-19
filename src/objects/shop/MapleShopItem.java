package objects.shop;

import constants.GameConstants;

public class MapleShopItem {
   private final int shopItemID;
   private final short buyable;
   private final short quantity;
   private final int itemId;
   private final long price;
   private final short slot;
   private final int reqItem;
   private final int reqItemQ;
   private final int category;
   private final int minLevel;
   private final int expiration;
   private final byte rank;
   private int pointQuestExID;
   private int pointPrice;
   private int buyLimit;
   private int worldBuyLimit;
   private int limitQuestExID;
   private String limitQuestExKey;
   private int limitQuestExValue;
   private final boolean potential;

   public MapleShopItem(int shopItemID, int itemId, long price, short slot, short buyable) {
      this.shopItemID = shopItemID;
      this.buyable = (short)(GameConstants.isEquip(itemId) ? 1 : 1000);
      this.quantity = 1;
      this.itemId = itemId;
      this.price = price;
      this.slot = slot;
      this.reqItem = 0;
      this.reqItemQ = 0;
      this.rank = 0;
      this.category = 0;
      this.minLevel = 0;
      this.expiration = 0;
      this.pointQuestExID = 0;
      this.pointPrice = 0;
      this.potential = false;
      this.buyLimit = 0;
      this.worldBuyLimit = 0;
      this.limitQuestExID = 0;
      this.limitQuestExKey = "";
      this.limitQuestExValue = 0;
   }

   public MapleShopItem(
      int shopItemID,
      short buyable,
      short quantity,
      int itemId,
      long price,
      short slot,
      int reqItem,
      int reqItemQ,
      byte rank,
      int category,
      int minLevel,
      int expiration,
      boolean potential,
      int pointQuestExID,
      int pointPrice,
      int buyLimit,
      int worldBuyLimit,
      int limitQuestExID,
      String limitQuestExKey,
      int limitQuestExValue
   ) {
      this.shopItemID = shopItemID;
      this.buyable = (short)(GameConstants.isEquip(itemId) ? 1 : 1000);
      this.quantity = quantity;
      this.itemId = itemId;
      this.price = price;
      this.slot = slot;
      this.reqItem = reqItem;
      this.reqItemQ = reqItemQ;
      this.rank = rank;
      this.category = category;
      this.minLevel = minLevel;
      this.expiration = expiration;
      this.potential = potential;
      this.pointQuestExID = pointQuestExID;
      this.pointPrice = pointPrice;
      this.buyLimit = buyLimit;
      this.worldBuyLimit = worldBuyLimit;
      this.limitQuestExID = limitQuestExID;
      this.limitQuestExKey = limitQuestExKey;
      this.limitQuestExValue = limitQuestExValue;
   }

   public short getBuyable() {
      return this.buyable;
   }

   public short getQuantity() {
      return this.quantity;
   }

   public int getItemId() {
      return this.itemId;
   }

   public long getPrice() {
      return this.price;
   }

   public short getSlot() {
      return this.slot;
   }

   public int getReqItem() {
      return this.reqItem;
   }

   public int getReqItemQ() {
      return this.reqItemQ;
   }

   public byte getRank() {
      return this.rank;
   }

   public int getCategory() {
      return this.category;
   }

   public int getMinLevel() {
      return this.minLevel;
   }

   public int getExpiration() {
      return this.expiration;
   }

   public boolean hasPotential() {
      return this.potential;
   }

   public int getPointQuestExID() {
      return this.pointQuestExID;
   }

   public int getPointPrice() {
      return this.pointPrice;
   }

   public int getBuyLimit() {
      return this.buyLimit;
   }

   public int getWorldBuyLimit() {
      return this.worldBuyLimit;
   }

   public void setWorldBuyLimit(int worldBuyLimit) {
      this.worldBuyLimit = worldBuyLimit;
   }

   public int getLimitQuestExID() {
      return this.limitQuestExID;
   }

   public void setLimitQuestExID(int limitQuestExID) {
      this.limitQuestExID = limitQuestExID;
   }

   public String getLimitQuestExKey() {
      return this.limitQuestExKey;
   }

   public void setLimitQuestExKey(String limitQuestExKey) {
      this.limitQuestExKey = limitQuestExKey;
   }

   public int getLimitQuestExValue() {
      return this.limitQuestExValue;
   }

   public void setLimitQuestExValue(int limitQuestExValue) {
      this.limitQuestExValue = limitQuestExValue;
   }

   public int getShopItemID() {
      return this.shopItemID;
   }
}
