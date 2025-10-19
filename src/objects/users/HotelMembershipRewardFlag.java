package objects.users;

public enum HotelMembershipRewardFlag {
   ELITE_REWARD(0),
   PREMIUM_REWARD(256),
   PRESTIGE_REWARD(512);

   private int flag;

   private HotelMembershipRewardFlag(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }
}
