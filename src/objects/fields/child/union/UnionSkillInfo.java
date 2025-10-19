package objects.fields.child.union;

public class UnionSkillInfo {
   private boolean changeable;
   private int skillID;

   public UnionSkillInfo(boolean changeable, int skillID) {
      this.changeable = changeable;
      this.skillID = skillID;
   }

   public boolean isChangeable() {
      return this.changeable;
   }

   public void setChangeable(boolean changeable) {
      this.changeable = changeable;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }
}
