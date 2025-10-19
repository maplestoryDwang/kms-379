package objects.users.enchant;

public enum EquipStat {
   ACC(1, -1),
   AVOID(2, -1),
   PVP_DAMAGE(4, -1),
   MDEF(8, -1),
   STR(1, 0),
   DEX(2, 0),
   INT(4, 0),
   LUK(8, 0),
   MHP(16, 0),
   MMP(32, 0),
   WATK(64, 0),
   MATK(128, 0),
   WDEF(256, 0),
   HANDS(512, 0),
   SPEED(1024, 0),
   JUMP(2048, 0),
   SLOTS(1, 1),
   LEVEL(2, 1),
   FLAG(4, 1),
   INC_SKILL(8, 1),
   ITEM_LEVEL(16, 1),
   ITEM_EXP(32, 1),
   DURABILITY(64, 1),
   VICIOUS_HAMMER(128, 1),
   DOWNLEVEL(256, 1),
   SPECIAL_ATTRIBUTE(512, 1),
   DURABILITY_SPECIAL(1024, 1),
   REQUIRED_LEVEL(2048, 1),
   GROWTH_ENCHANT(4096, 1),
   FINAL_STRIKE(8192, 1),
   BOSS_DAMAGE(16384, 1),
   IGNORE_PDR(32768, 1),
   DAM_R(65536, 1),
   STAT_R(131072, 1),
   CUTTABLE(262144, 1),
   EX_GRADE_OPTION(524288, 1),
   ITEM_STATE(1048576, 1),
   ExceptSTR(1, 2),
   ExceptDEX(2, 2),
   ExceptINT(4, 2),
   ExceptLUK(8, 2),
   ExceptMHP(16, 2),
   ExceptMMP(32, 2),
   ExceptWATK(64, 2),
   ExceptMATK(128, 2),
   ExceptWDEF(256, 2),
   ExceptMDEF(512, 2),
   ExceptACC(1024, 2),
   ExceptSPEED(2048, 2),
   ExceptJUMP(4096, 2),
   TOTAL_DAMAGE(1, 3),
   ALL_STAT(2, 3);

   private final int value;
   private final int first;

   private EquipStat(int value, int first) {
      this.value = value;
      this.first = first;
   }

   public final int getValue() {
      return this.value;
   }

   public final int getPosition() {
      return this.first;
   }

   public final boolean check(int flag) {
      return (flag & this.value) != 0;
   }

   public static enum EnhanctBuff {
      UPGRADE_TIER(1),
      NO_DESTROY(2),
      SCROLL_SUCCESS(4);

      private final int value;

      private EnhanctBuff(int value) {
         this.value = value;
      }

      public final byte getValue() {
         return (byte)this.value;
      }

      public final boolean check(int flag) {
         return (flag & this.value) != 0;
      }
   }
}
