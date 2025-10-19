package constants;

public class HottimeItemEntry {
   private long time = 0L;
   private int itemID = 0;
   private int quantity = 0;

   public HottimeItemEntry(long time, int itemID, int quantity) {
      this.time = time;
      this.itemID = itemID;
      this.quantity = quantity;
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public int getQuantity() {
      return this.quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }
}
