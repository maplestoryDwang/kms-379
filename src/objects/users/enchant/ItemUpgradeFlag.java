package objects.users.enchant;

public enum ItemUpgradeFlag {
   INC_PAD(1),
   INC_MAD(2),
   INC_STR(4),
   INC_DEX(8),
   INC_INT(16),
   INC_LUK(32),
   INC_PDD(64),
   INC_MDD(128),
   INC_MHP(256),
   INC_MMP(512),
   INC_ACC(1024),
   INC_EVA(2048),
   INC_JUMP(4096),
   INC_SPEED(8192);

   private int flag;

   private ItemUpgradeFlag(int i) {
      this.flag = i;
   }

   public final int getValue() {
      return this.flag;
   }

   public boolean check(int flag) {
      return (this.getValue() & flag) != 0;
   }
}
