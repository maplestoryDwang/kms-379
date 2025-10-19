package objects.fields.gameobject;

public enum RuneStoneStep {
   Clear(0),
   Wrong(1),
   NotAllowed(2);

   private int type;

   private RuneStoneStep(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
