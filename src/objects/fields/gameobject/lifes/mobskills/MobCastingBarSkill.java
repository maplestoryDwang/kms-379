package objects.fields.gameobject.lifes.mobskills;

public enum MobCastingBarSkill {
   Start(0),
   End(1),
   During(2),
   Reduce(3);

   private int type;

   private MobCastingBarSkill(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
