package objects.fields.child.karrotte;

public enum KalosActionType {
   GuardianAction(1),
   Phase2KalosAction(2),
   AssaultAction(3),
   UNK4(4),
   Init(5),
   CurseAction(6);

   private int type;

   private KalosActionType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static KalosActionType getType(int type) {
      for (KalosActionType t : values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
