package logging.entry;

public enum HackLogType {
   SpeedHack(0),
   WZEdit(1);

   private int type;

   private HackLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
