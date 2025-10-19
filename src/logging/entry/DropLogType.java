package logging.entry;

public enum DropLogType {
   PlayerDrop(0),
   MobDrop(1);

   private int type;

   private DropLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
