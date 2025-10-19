package objects.users.extra;

public enum ExtraAbilityGrade {
   Rare(0, "레어"),
   Epic(1, "에픽"),
   Unique(2, "유니크"),
   Legendary(3, "레전드리");

   private int grade;
   private String desc;

   private ExtraAbilityGrade(int grade, String desc) {
      this.grade = grade;
      this.desc = desc;
   }

   public ExtraAbilityGrade getGrade() {
      for (ExtraAbilityGrade g : values()) {
         if (g.grade == this.grade) {
            return g;
         }
      }

      return null;
   }

   public static ExtraAbilityGrade getGrade(int grade) {
      for (ExtraAbilityGrade g : values()) {
         if (g.grade == grade) {
            return g;
         }
      }

      return null;
   }

   public int getGradeID() {
      return this.grade;
   }

   public String getDesc() {
      return this.desc;
   }
}
