package logging.entry;

public enum CreateCharLogType {
   CreateChar(0),
   DeleteChar(1),
   ChangeCharName(2);

   private int type;

   private CreateCharLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
