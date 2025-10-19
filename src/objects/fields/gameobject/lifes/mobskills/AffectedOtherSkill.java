package objects.fields.gameobject.lifes.mobskills;

public class AffectedOtherSkill {
   private int affectedOtherSkillID;
   private int affectedOtherSkillLev;

   public AffectedOtherSkill(int affectedOtherSkillID, int affectedOtherSkillLev) {
      this.setAffectedOtherSkillID(affectedOtherSkillID);
      this.setAffectedOtherSkillLev(affectedOtherSkillLev);
   }

   public int getAffectedOtherSkillID() {
      return this.affectedOtherSkillID;
   }

   public void setAffectedOtherSkillID(int affectedOtherSkillID) {
      this.affectedOtherSkillID = affectedOtherSkillID;
   }

   public int getAffectedOtherSkillLev() {
      return this.affectedOtherSkillLev;
   }

   public void setAffectedOtherSkillLev(int affectedOtherSkillLev) {
      this.affectedOtherSkillLev = affectedOtherSkillLev;
   }
}
