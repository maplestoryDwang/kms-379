package objects.summoned;

public enum SummonMoveAbility {
   STATIONARY(0),
   FOLLOW(1),
   WALK_STATIONARY(2),
   BIRD_FOLLOW(3),
   CIRCLE_FOLLOW(4),
   CIRCLE_STATIONARY(5),
   FIX_V_MOVE(6),
   FLAME_SUMMON(7),
   SHADOW_SERVANT(8),
   SUMMON_JAGUAR(11),
   WALK_CLONE2(13),
   SHADOW_SERVANT_EXTEND(14),
   WALK_FOLLOW(16),
   MECA_CARRIER(17),
   TRANSCENDENT_TIME(18);

   private final int val;

   private SummonMoveAbility(int val) {
      this.val = val;
   }

   public int getValue() {
      return this.val;
   }
}
