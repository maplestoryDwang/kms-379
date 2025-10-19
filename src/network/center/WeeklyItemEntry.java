package network.center;

public class WeeklyItemEntry {
   private WeeklyItemEntry.ItemType itemType;
   private long SN;
   private int itemID;
   private int itemQuantity;
   private int remainCount;
   private long startTime;
   private long endTime;

   public WeeklyItemEntry(WeeklyItemEntry.ItemType itemType, long SN, int itemID, int itemQuantity, int remainCount, long startTime, long endTime) {
      this.SN = SN;
      this.itemType = itemType;
      this.itemID = itemID;
      this.itemQuantity = itemQuantity;
      this.remainCount = remainCount;
      this.startTime = startTime;
      this.endTime = endTime;
   }

   public WeeklyItemEntry.ItemType getItemType() {
      return this.itemType;
   }

   public void setItemType(WeeklyItemEntry.ItemType itemType) {
      this.itemType = itemType;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public int getItemQuantity() {
      return this.itemQuantity;
   }

   public void setItemQuantity(int itemQuantity) {
      this.itemQuantity = itemQuantity;
   }

   public int getRemainCount() {
      return this.remainCount;
   }

   public void setRemainCount(int remainCount) {
      this.remainCount = remainCount;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public long getEndTime() {
      return this.endTime;
   }

   public void setEndTime(long endTime) {
      this.endTime = endTime;
   }

   public long getSN() {
      return this.SN;
   }

   public void setSN(long SN) {
      this.SN = SN;
   }

   public static enum ItemType {
      BonusItem(0),
      ExtremeItem(1);

      private int type;

      private ItemType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static WeeklyItemEntry.ItemType get(int type) {
         for (WeeklyItemEntry.ItemType t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }
}
