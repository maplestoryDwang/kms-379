package objects.fields.gameobject;

public enum RuneStoneState {
   Stay(0),
   Appear(1),
   Disappear(2);

   private int type;

   private RuneStoneState(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
