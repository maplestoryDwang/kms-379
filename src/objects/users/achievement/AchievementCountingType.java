package objects.users.achievement;

public enum AchievementCountingType {
   counting,
   sum,
   none,
   set,
   set_if_higher,
   consecutive_date_counting;

   public static AchievementCountingType getType(String name) {
      for (AchievementCountingType t : values()) {
         if (t.name().equals(name)) {
            return t;
         }
      }

      return null;
   }
}
