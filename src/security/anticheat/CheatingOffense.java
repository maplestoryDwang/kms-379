package security.anticheat;

public enum CheatingOffense {
   FAST_SUMMON_ATTACK((byte)5, 6000L, 50, (byte)2),
   FASTATTACK((byte)5, 6000L, 200, (byte)2),
   FASTATTACK2((byte)5, 6000L, 500, (byte)2),
   MOVE_MONSTERS((byte)5, 30000L, 500, (byte)2),
   FAST_HP_MP_REGEN((byte)5, 20000L, 100, (byte)2),
   SAME_DAMAGE((byte)5, 180000L),
   ATTACK_WITHOUT_GETTING_HIT((byte)1, 30000L, 1200, (byte)0),
   HIGH_DAMAGE_MAGIC((byte)5, 30000L),
   HIGH_DAMAGE_MAGIC_2((byte)10, 180000L),
   HIGH_DAMAGE((byte)5, 30000L),
   HIGH_DAMAGE_2((byte)10, 180000L),
   EXCEED_DAMAGE_CAP((byte)5, 60000L, 800, (byte)0),
   ATTACK_FARAWAY_MONSTER((byte)5, 180000L),
   ATTACK_FARAWAY_MONSTER_SUMMON((byte)5, 180000L, 200, (byte)2),
   REGEN_HIGH_HP((byte)10, 30000L, 1000, (byte)2),
   REGEN_HIGH_MP((byte)10, 30000L, 1000, (byte)2),
   ITEMVAC_CLIENT((byte)3, 10000L, 100),
   ITEMVAC_SERVER((byte)2, 10000L, 100, (byte)2),
   PET_ITEMVAC_CLIENT((byte)3, 10000L, 100),
   PET_ITEMVAC_SERVER((byte)2, 10000L, 100, (byte)2),
   USING_FARAWAY_PORTAL((byte)1, 60000L, 100, (byte)0),
   FAST_TAKE_DAMAGE((byte)1, 60000L, 100),
   HIGH_AVOID((byte)5, 180000L, 100),
   HIGH_JUMP((byte)1, 60000L),
   MISMATCHING_BULLETCOUNT((byte)1, 300000L),
   ETC_EXPLOSION((byte)1, 300000L),
   ATTACKING_WHILE_DEAD((byte)1, 300000L),
   USING_UNAVAILABLE_ITEM((byte)1, 300000L),
   FAMING_SELF((byte)1, 300000L),
   FAMING_UNDER_15((byte)1, 300000L),
   EXPLODING_NONEXISTANT((byte)1, 300000L),
   SUMMON_HACK((byte)1, 300000L),
   SUMMON_HACK_MOBS((byte)1, 300000L),
   ARAN_COMBO_HACK((byte)1, 600000L, 50, (byte)2),
   HEAL_ATTACKING_UNDEAD((byte)20, 30000L, 100);

   private final byte points;
   private final long validityDuration;
   private final int autobancount;
   private byte bantype = 0;

   public final byte getPoints() {
      return this.points;
   }

   public final long getValidityDuration() {
      return this.validityDuration;
   }

   public final boolean shouldAutoban(int count) {
      return this.autobancount < 0 ? false : count >= this.autobancount;
   }

   public final byte getBanType() {
      return this.bantype;
   }

   public final void setEnabled(boolean enabled) {
      this.bantype = (byte)(enabled ? 1 : 0);
   }

   public final boolean isEnabled() {
      return this.bantype >= 1;
   }

   private CheatingOffense(byte points, long validityDuration) {
      this(points, validityDuration, -1, (byte)2);
   }

   private CheatingOffense(byte points, long validityDuration, int autobancount) {
      this(points, validityDuration, autobancount, (byte)1);
   }

   private CheatingOffense(byte points, long validityDuration, int autobancount, byte bantype) {
      this.points = points;
      this.validityDuration = validityDuration;
      this.autobancount = autobancount;
      this.bantype = bantype;
   }
}
