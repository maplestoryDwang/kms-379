package objects.fields.gameobject.lifes;

public enum MoveAbility {
   Stop(0),
   Walk(1),
   Jump(2),
   Fly(4);

   private int type;

   private MoveAbility(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
