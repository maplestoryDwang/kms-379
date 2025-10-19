package objects.users.achievement;

public enum AchievementGrade {
   none(0),
   bronze(1),
   silver(2),
   gold(3),
   platinum(4),
   diamond(5),
   master(6);

   private int grade;

   private AchievementGrade(int grade) {
      this.grade = grade;
   }

   public int getGrade() {
      return this.grade;
   }

   public static AchievementGrade getGrade(int grade) {
      for (AchievementGrade g : values()) {
         if (g.getGrade() == grade) {
            return g;
         }
      }

      return null;
   }

   public static AchievementGrade getGradeByName(String name) {
      for (AchievementGrade g : values()) {
         if (g.name().equals(name)) {
            return g;
         }
      }

      return null;
   }
}
