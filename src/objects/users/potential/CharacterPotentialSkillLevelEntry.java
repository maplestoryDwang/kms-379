package objects.users.potential;

public class CharacterPotentialSkillLevelEntry {
   private int grade;
   private int[] skillLevels;

   public CharacterPotentialSkillLevelEntry(int grade, int[] skillLevels) {
      this.grade = grade;
      this.skillLevels = skillLevels;
   }

   public int getGrade() {
      return this.grade;
   }

   public void setGrade(int grade) {
      this.grade = grade;
   }

   public int[] getSkillLevels() {
      return this.skillLevels;
   }

   public void setSkillLevels(int[] skillLevels) {
      this.skillLevels = skillLevels;
   }
}
