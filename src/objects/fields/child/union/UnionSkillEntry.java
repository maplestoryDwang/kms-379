package objects.fields.child.union;

public class UnionSkillEntry {
   private int skillID;
   private int skillLevel;

   public UnionSkillEntry(int skillID, int skillLevel) {
      this.skillID = skillID;
      this.skillLevel = skillLevel;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }

   public int getSkillLevel() {
      return this.skillLevel;
   }

   public void setSkillLevel(int skillLevel) {
      this.skillLevel = skillLevel;
   }
}
