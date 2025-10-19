package logging.entry;

public enum EnchantLogResult {
   Success(0),
   Failed(1),
   Destroyed(2);

   private int type;

   private EnchantLogResult(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
