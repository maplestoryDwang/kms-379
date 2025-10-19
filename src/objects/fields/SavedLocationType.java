package objects.fields;

public enum SavedLocationType {
   FREE_MARKET(0),
   MULUNG_TC(1),
   WORLDTOUR(2),
   FLORINA(3),
   FISHING(4),
   RICHIE(5),
   DONGDONGCHIANG(6),
   EVENT(7),
   AMORIA(8),
   CHRISTMAS(9),
   ARDENTMILL(10),
   GUILD(11),
   MONSTERPARK(12),
   PROFESSION(13),
   MULUNG_DOJO(14);

   private int index;

   private SavedLocationType(int index) {
      this.index = index;
   }

   public int getValue() {
      return this.index;
   }

   public static SavedLocationType fromString(String Str) {
      return valueOf(Str);
   }
}
