package objects.users.looks.mannequin;

public enum MannequinResultType {
   IncSlotMax(1),
   Save(2),
   Extend(3),
   Change(4),
   Unk(5);

   private int type;

   private MannequinResultType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static MannequinResultType getType(int type) {
      for (MannequinResultType t : values()) {
         if (t.type == type) {
            return t;
         }
      }

      return null;
   }
}
