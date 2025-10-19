package objects.fields.child.fritto;

import objects.utils.Randomizer;

public enum CourtshipCommandType {
   Down(0),
   Top(1),
   Left(2),
   Right(3);

   private int type;

   private CourtshipCommandType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static CourtshipCommandType random() {
      int random = Randomizer.rand(0, 3);
      return get(random);
   }

   public static CourtshipCommandType get(int type) {
      for (CourtshipCommandType t : values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
