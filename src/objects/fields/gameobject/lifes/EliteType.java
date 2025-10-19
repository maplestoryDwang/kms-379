package objects.fields.gameobject.lifes;

public enum EliteType {
   None(0),
   Monster(1),
   Boss(2);

   private int type;

   private EliteType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static EliteType get(int type) {
      for (EliteType e : values()) {
         if (e.getType() == type) {
            return e;
         }
      }

      return null;
   }
}
