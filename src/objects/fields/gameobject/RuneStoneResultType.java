package objects.fields.gameobject;

public enum RuneStoneResultType {
   DataMissMatch(0),
   Preoccupied(1),
   WaitToUseRune(2),
   WaitToUseDecoy(3),
   CannotUse(4),
   UnderRequiredLevel(5),
   UnderRequiredLevelDecoy(6),
   Failed(7),
   TooFar(8),
   Success(9);

   private int type;

   private RuneStoneResultType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
