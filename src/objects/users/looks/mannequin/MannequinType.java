package objects.users.looks.mannequin;

public enum MannequinType {
   HairRoom(0),
   FaceRoom(1),
   SkinRoom(2);

   private int type;

   private MannequinType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static MannequinType getType(int type) {
      for (MannequinType t : values()) {
         if (t.type == type) {
            return t;
         }
      }

      return null;
   }
}
