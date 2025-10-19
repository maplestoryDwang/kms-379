package objects.users.enchant;

public enum BonusStatPlaceType {
   FromNormal(1),
   FromEliteBoss(2),
   ChanceTime(3),
   LevelledRebirthFlame(4),
   PowerfulRebirthFlame(5),
   EternalRebirthFlame(6),
   BlackRebirthFlame(7);

   final int type;

   private BonusStatPlaceType(int type) {
      this.type = type;
   }
}
