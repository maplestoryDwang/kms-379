package objects.fields;

public enum RandomPortalGameType {
   EagleHunt(0),
   ReceivingTreasure(1),
   StealDragonsEgg(2),
   CourtshipDance(3),
   StormwingArea(4),
   MidnightMonsterHunting(5),
   GuardTheCastleGates(6),
   ProtectPollo(7),
   FireWolf(8),
   Rabbit(9);

   private int type;

   private RandomPortalGameType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static RandomPortalGameType get(int type) {
      for (RandomPortalGameType t : values()) {
         if (t.type == type) {
            return t;
         }
      }

      return null;
   }
}
