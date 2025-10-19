package logging.entry;

public enum BossLogType {
   EnterLog(0),
   ClearLog(1);

   private int type;

   private BossLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
