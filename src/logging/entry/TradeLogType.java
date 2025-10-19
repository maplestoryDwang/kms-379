package logging.entry;

public enum TradeLogType {
   CompleteTrade(0),
   TradeDenied(1);

   private int type;

   private TradeLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
