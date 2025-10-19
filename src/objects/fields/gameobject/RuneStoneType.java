package objects.fields.gameobject;

public enum RuneStoneType {
   DefenceUp(0),
   DotAttack(1),
   ThunderAttack(2),
   EarthQuake(3),
   EliteMob(4),
   Mimic(5),
   ReduceCooltime(6),
   Increase(7),
   Razer(8),
   Ignition(9);

   private int type;

   private RuneStoneType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static RuneStoneType get(int type) {
      for (RuneStoneType t : values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
