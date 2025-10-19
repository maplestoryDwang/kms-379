package logging.entry;

public enum ConnectLogType {
   Connect(0),
   Disconnect(1),
   Denied(2);

   private int type;

   private ConnectLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
