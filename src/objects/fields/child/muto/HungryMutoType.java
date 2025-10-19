package objects.fields.child.muto;

public enum HungryMutoType {
   HungryMutoResult_TimerSet(1),
   HungryMutoResult_GameInit(3),
   HungryMutoResult_RecipeUpdate(4),
   HungryMutoResult_PickupItemUpdate(5);

   final int type;

   private HungryMutoType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
