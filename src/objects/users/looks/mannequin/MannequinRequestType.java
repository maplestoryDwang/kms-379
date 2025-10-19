package objects.users.looks.mannequin;

public enum MannequinRequestType {
   Expend(1),
   Save(2),
   Delete(3),
   Change(4);

   private int type;

   private MannequinRequestType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static MannequinRequestType getType(int type) {
      for (MannequinRequestType t : values()) {
         if (t.type == type) {
            return t;
         }
      }

      return null;
   }
}
