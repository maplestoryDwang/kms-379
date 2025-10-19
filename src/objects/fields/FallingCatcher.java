package objects.fields;

public class FallingCatcher {
   private int skillID;
   private int skillLevel;
   private int x;

   public FallingCatcher(int skillID, int skillLevel, int x) {
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.x = x;
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

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }
}
