package objects.users.achievement;

public enum AchievementScoreOptionType {
   achievement_reset,
   achievement_complete;

   public static AchievementScoreOptionType getType(String name) {
      for (AchievementScoreOptionType t : values()) {
         if (t.name().equals(name)) {
            return t;
         }
      }

      return null;
   }
}
