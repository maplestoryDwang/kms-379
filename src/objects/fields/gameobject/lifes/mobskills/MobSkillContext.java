package objects.fields.gameobject.lifes.mobskills;

public class MobSkillContext {
   private boolean afterDead;
   private long lastSkillUse;
   private int skillID;
   private int skillLevel;
   private int summoned;
   private int used;

   public boolean isAfterDead() {
      return this.afterDead;
   }

   public void setAfterDead(boolean afterDead) {
      this.afterDead = afterDead;
   }

   public long getLastSkillUse() {
      return this.lastSkillUse;
   }

   public void setLastSkillUse(long lastSkillUse) {
      this.lastSkillUse = lastSkillUse;
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

   public int getSummoned() {
      return this.summoned;
   }

   public void setSummoned(int summoned) {
      this.summoned = summoned;
   }

   public int getUsed() {
      return this.used;
   }

   public void setUsed(int used) {
      this.used = used;
   }
}
