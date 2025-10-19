package objects.users.achievement;

public enum AchievementConditionType {
   or,
   and;

   public static AchievementConditionType getType(String name) {
      for (AchievementConditionType t : values()) {
         if (t.name().equals(name)) {
            return t;
         }
      }

      return null;
   }
}
