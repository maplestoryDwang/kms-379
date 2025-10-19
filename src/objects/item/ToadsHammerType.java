package objects.item;

public enum ToadsHammerType {
   PassDownResultPreview(0),
   PassDown(1),
   ScrollData(2);

   private int type;

   private ToadsHammerType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static ToadsHammerType getType(int t) {
      for (ToadsHammerType type : values()) {
         if (type.getType() == t) {
            return type;
         }
      }

      return null;
   }
}
