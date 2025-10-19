package objects.users.achievement;

public enum AchievementDifficulty {
   normal(0),
   rare(1),
   epic(2),
   unique(3),
   legendary(4);

   private int difficulty;

   private AchievementDifficulty(int difficulty) {
      this.difficulty = difficulty;
   }

   public int getDifficulty() {
      return this.difficulty;
   }

   public static AchievementDifficulty getDifficulty(String name) {
      for (AchievementDifficulty e : values()) {
         if (e.name().equals(name)) {
            return e;
         }
      }

      return null;
   }
}
