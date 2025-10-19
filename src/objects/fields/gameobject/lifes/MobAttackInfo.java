package objects.fields.gameobject.lifes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class MobAttackInfo {
   public boolean deadlyAttack;
   private int mpBurn;
   private int mpCon;
   private int diseaseSkill;
   private int diseaseLevel;
   public int PADamage;
   public int MADamage;
   public int range = 0;
   public Point lt = null;
   public Point rb = null;
   public boolean magic = false;
   public boolean isElement = false;
   public boolean afterDead;
   public int afterAttack;
   public int afterAttackCount;
   private int fixDamR = 0;
   private int fixDamRType = 0;
   public WeatherMsg weatherMsg = null;
   public List<CallSkillInfo> callSkillInfo = new ArrayList<>();
   public CallSkillInfoWithData callSkillWithData = null;

   public void setDeadlyAttack(boolean isDeadlyAttack) {
      this.deadlyAttack = isDeadlyAttack;
   }

   public boolean isDeadlyAttack() {
      return this.deadlyAttack;
   }

   public void setMpBurn(int mpBurn) {
      this.mpBurn = mpBurn;
   }

   public int getMpBurn() {
      return this.mpBurn;
   }

   public void setDiseaseSkill(int diseaseSkill) {
      this.diseaseSkill = diseaseSkill;
   }

   public int getDiseaseSkill() {
      return this.diseaseSkill;
   }

   public void setDiseaseLevel(int diseaseLevel) {
      this.diseaseLevel = diseaseLevel;
   }

   public int getDiseaseLevel() {
      return this.diseaseLevel;
   }

   public void setMpCon(int mpCon) {
      this.mpCon = mpCon;
   }

   public int getMpCon() {
      return this.mpCon;
   }

   public int getRange() {
      int maxX = Math.max(Math.abs(this.lt == null ? 0 : this.lt.x), Math.abs(this.rb == null ? 0 : this.rb.x));
      int maxY = Math.max(Math.abs(this.lt == null ? 0 : this.lt.y), Math.abs(this.rb == null ? 0 : this.rb.y));
      return Math.max(maxX * maxX + maxY * maxY, this.range);
   }

   public List<CallSkillInfo> getCallSkillInfo() {
      return this.callSkillInfo;
   }

   public CallSkillInfoWithData getCallSkillInfoWithData() {
      return this.callSkillWithData;
   }

   public int getFixDamR() {
      return this.fixDamR;
   }

   public void setFixDamR(int fixDamR) {
      this.fixDamR = fixDamR;
   }

   public int getFixDamRType() {
      return this.fixDamRType;
   }

   public void setFixDamRType(int fixDamRType) {
      this.fixDamRType = fixDamRType;
   }
}
