package objects.fields;

public enum RandomPortalType {
   MesoEvent(0),
   Unk(1),
   PoloFritto(2),
   FireWolf(3);

   private int type;

   private RandomPortalType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static RandomPortalType get(int t) {
      for (RandomPortalType type : values()) {
         if (type.type == t) {
            return type;
         }
      }

      return null;
   }
}
