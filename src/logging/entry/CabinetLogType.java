package logging.entry;

public enum CabinetLogType {
   ExpiredPendantSlot(0),
   TakeOutItem(1),
   ExpiredCabinetItem(2);

   private int type;

   private CabinetLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
