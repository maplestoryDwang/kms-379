package logging;

public enum LoggingType {
   Chatting(0, "log_chatting"),
   ConsumeLog(1, "consume_log"),
   DonationLog(2, "donation_log"),
   MacroLog(3, "macro_log"),
   ItemLog(4, "item_log"),
   AuctionLog(5, "auction_log"),
   DamageHackLog(6, "damage_hack_log"),
   TransferFieldLog(7, "transfer_field_log"),
   BossLog(8, "boss_log"),
   EnchantLog(9, "enchant_log"),
   CreateCharLog(10, "create_char_log"),
   ConnectLog(11, "connect_log"),
   DropLog(12, "drop_log"),
   HackLog(13, "hack_log"),
   CustomLog(14, "custom_log"),
   CabinetLog(15, "cabinet_log"),
   TradeLog(16, "trade_log"),
   PickupLog(17, "pickup_log");

   private int type;
   private String schemaName;

   private LoggingType(int type, String schemaName) {
      this.setType(type);
      this.setSchemaName(schemaName);
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public String getSchemaName() {
      return this.schemaName;
   }

   public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
   }

   public static LoggingType getType(int type) {
      for (LoggingType t : values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
