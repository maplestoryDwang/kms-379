package constants;

public class AutoHottimeEntry {
   private AutoHottimeEntry.EventType eventType = null;
   private long startTime = 0L;
   private long endTime = 0L;
   private double rate = 0.0;
   private int itemID = 0;
   private int itemCount = 0;
   private boolean started = false;

   public AutoHottimeEntry(AutoHottimeEntry.EventType eventType, long startTime, long endTime, double rate, boolean started) {
      this.setEventType(eventType);
      this.setStartTime(startTime);
      this.setEndTime(endTime);
      this.setRate(rate);
      this.setStarted(started);
   }

   public AutoHottimeEntry(AutoHottimeEntry.EventType eventType, long startTime, long endTime, int itemID, int itemCount, boolean started) {
      this.setEventType(eventType);
      this.setStartTime(startTime);
      this.setEndTime(endTime);
      this.setItemID(itemID);
      this.setItemCount(itemCount);
      this.setStarted(started);
   }

   public AutoHottimeEntry.EventType getEventType() {
      return this.eventType;
   }

   public void setEventType(AutoHottimeEntry.EventType eventType) {
      this.eventType = eventType;
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

   public double getRate() {
      return this.rate;
   }

   public void setRate(double rate) {
      this.rate = rate;
   }

   public boolean isStarted() {
      return this.started;
   }

   public void setStarted(boolean started) {
      this.started = started;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public int getItemCount() {
      return this.itemCount;
   }

   public void setItemCount(int itemCount) {
      this.itemCount = itemCount;
   }

   public static enum EventType {
      Exp(0),
      Meso(1),
      Drop(2),
      Give(3);

      private int type;

      private EventType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static AutoHottimeEntry.EventType getType(int type) {
         for (AutoHottimeEntry.EventType e : values()) {
            if (e.getType() == type) {
               return e;
            }
         }

         return null;
      }
   }
}
