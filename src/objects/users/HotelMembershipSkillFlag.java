package objects.users;

public enum HotelMembershipSkillFlag {
   ADD_ATTACK(0),
   ADD_ALL_STAT(256),
   ADD_IGNORE_DEFENSE(512),
   ADD_MONSTER_DAMAGE(768),
   ADD_BOSS_DAMAGE(1024),
   ADD_BUFF_TIME(1280),
   ADD_CRITICAL_PROB(1536),
   ADD_ARCANE_FORCE(1792),
   ADD_EXP(2048);

   private int flag;

   private HotelMembershipSkillFlag(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }
}
