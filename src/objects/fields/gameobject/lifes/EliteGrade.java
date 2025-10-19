package objects.fields.gameobject.lifes;

public enum EliteGrade {
   None(-1),
   Yellow(0),
   Orange(1),
   Red(2);

   private int type;

   private EliteGrade(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static EliteGrade get(int type) {
      for (EliteGrade e : values()) {
         if (e.getType() == type) {
            return e;
         }
      }

      return null;
   }
}
