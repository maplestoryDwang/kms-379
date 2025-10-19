package objects.fields.gameobject.lifes.mobskills;

public enum MobTeleportType {
   None(0),
   AggroTop(1),
   Controller(2),
   StaticPoint(3),
   OffsetX(4),
   RandomUser(5),
   NearestSP(6),
   NotController(7),
   Anywhere(8),
   SummonIllusion(9),
   OffsetX2(10),
   MapBorder(11),
   WillTeleport(12),
   StayX(14),
   RandomUser2(16);

   final int type;

   private MobTeleportType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static MobTeleportType get(int type) {
      for (MobTeleportType t : values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
