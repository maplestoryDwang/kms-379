package objects.fields.gameobject;

public enum RuneStoneArrow {
   Down(0),
   Up(1),
   Left(2),
   Right(3),
   Count(4);

   private int type;

   private RuneStoneArrow(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static RuneStoneArrow get(int type) {
      for (RuneStoneArrow arrow : values()) {
         if (arrow.type == type) {
            return arrow;
         }
      }

      return null;
   }
}
