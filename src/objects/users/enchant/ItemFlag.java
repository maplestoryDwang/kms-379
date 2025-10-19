package objects.users.enchant;

public enum ItemFlag {
   None(0),
   PROTECTED(1),
   PREVENT_SLIP(2),
   KARMA_USE(2),
   BINDED(4),
   POSSIBLE_TRADING(8),
   KARMA_EQ(16),
   CRAFTED_USE(16),
   CHARM_EQUIPPED(32),
   ANDROID_ACTIVATED(64),
   CRAFTED(128),
   PROTECTION_SCROLLED(256),
   LUCKY_DAY_SCROLLED(512),
   KARMA_ACC_USE(1024),
   LOCK(2048),
   POSSIBLE_ONCE_TRADE_IN_ACCOUNT(4096),
   SAFETY_SCROLLED(8192),
   RECOVERY_SCROLLED(16384),
   RETURN_SCROLLED(32768);

   private final int i;

   private ItemFlag(int i) {
      this.i = i;
   }

   public final int getValue() {
      return this.i;
   }

   public final boolean check(int flag) {
      return (flag & this.i) == this.i;
   }
}
