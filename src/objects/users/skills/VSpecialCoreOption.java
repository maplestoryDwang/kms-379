package objects.users.skills;

public class VSpecialCoreOption {
   private String condType;
   private int coolTime;
   private int count;
   private int validTime;
   private double prob;
   private String effectType;
   private int skillId;
   private int skillLevel;
   private int healPercent;
   private int reducePercent;

   public String getCondType() {
      return this.condType;
   }

   public void setCondType(String condType) {
      this.condType = condType;
   }

   public int getCoolTime() {
      return this.coolTime;
   }

   public void setCoolTime(int cooltime) {
      this.coolTime = cooltime;
   }

   public int getCount() {
      return this.count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public int getValidTime() {
      return this.validTime;
   }

   public void setValidTime(int validTime) {
      this.validTime = validTime;
   }

   public double getProb() {
      return this.prob;
   }

   public void setProb(double prob) {
      this.prob = prob;
   }

   public String getEffectType() {
      return this.effectType;
   }

   public void setEffectType(String effectType) {
      this.effectType = effectType;
   }

   public int getSkillId() {
      return this.skillId;
   }

   public void setSkillId(int skillid) {
      this.skillId = skillid;
   }

   public int getSkillLevel() {
      return this.skillLevel;
   }

   public void setSkillLevel(int skilllevel) {
      this.skillLevel = skilllevel;
   }

   public int getHealPercent() {
      return this.healPercent;
   }

   public void setHealPercent(int healPercent) {
      this.healPercent = healPercent;
   }

   public int getReducePercent() {
      return this.reducePercent;
   }

   public void setReducePercent(int reducePercent) {
      this.reducePercent = reducePercent;
   }
}
