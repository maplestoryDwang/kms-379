package objects.fields;

public enum EliteState {
   Normal(0),
   EliteMob(1),
   EliteBoss(2),
   BonusStage(3),
   HasteEvent(6);

   private int type;

   private EliteState(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
