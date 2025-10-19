package objects.users.achievement;

public enum AchievementCompareType {
   greater_than_or_equal_to;

   public static AchievementCompareType getType(String name) {
      for (AchievementCompareType t : values()) {
         if (t.name().equals(name)) {
            return t;
         }
      }

      return null;
   }
}
