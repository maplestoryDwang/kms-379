package objects.fields.gameobject.lifes.mobskills;

import java.util.ArrayList;
import java.util.List;
import objects.fields.gameobject.lifes.WeatherMsg;

public class MobSkill {
   private int mobSkillSN;
   private int mobSkillID;
   private byte action;
   private int index;
   private int level;
   private int skillAfter;
   private boolean onlyOtherSkill;
   private int effectAfter;
   private byte priority;
   private boolean firstAttack;
   private boolean onlyFsm;
   private int skillForbid;
   private int afterDelay;
   private int fixDamR;
   private boolean doFirst;
   private int preSkillIndex;
   private int preSkillCount;
   private String info;
   private String text;
   private boolean afterDead;
   private int afterAttack = -1;
   private int afterAttackCount;
   private int castTime;
   private int coolTime;
   private int delay;
   private int useLimit;
   private String speak;
   private WeatherMsg weatherMsg;
   private List<Integer> cooltimeOnSkill = new ArrayList<>();

   public int getMobSkillSN() {
      return this.mobSkillSN;
   }

   public void setMobSkillSN(int mobSkillSN) {
      this.mobSkillSN = mobSkillSN;
   }

   public int getMobSkillID() {
      return this.mobSkillID;
   }

   public void setMobSkillID(int mobSkillID) {
      this.mobSkillID = mobSkillID;
   }

   public byte getAction() {
      return this.action;
   }

   public void setAction(byte action) {
      this.action = action;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public int getSkillAfter() {
      return this.skillAfter;
   }

   public void setSkillAfter(int skillAfter) {
      this.skillAfter = skillAfter;
   }

   public boolean isOnlyOtherSkill() {
      return this.onlyOtherSkill;
   }

   public void setOnlyOtherSkill(boolean onlyOtherSkill) {
      this.onlyOtherSkill = onlyOtherSkill;
   }

   public int getEffectAfter() {
      return this.effectAfter;
   }

   public void setEffectAfter(int effectAfter) {
      this.effectAfter = effectAfter;
   }

   public byte getPriority() {
      return this.priority;
   }

   public void setPriority(byte priority) {
      this.priority = priority;
   }

   public boolean isFirstAttack() {
      return this.firstAttack;
   }

   public void setFirstAttack(boolean firstAttack) {
      this.firstAttack = firstAttack;
   }

   public boolean isOnlyFsm() {
      return this.onlyFsm;
   }

   public void setOnlyFsm(boolean onlyFsm) {
      this.onlyFsm = onlyFsm;
   }

   public int getSkillForbid() {
      return this.skillForbid;
   }

   public void setSkillForbid(int skillForbid) {
      this.skillForbid = skillForbid;
   }

   public int getAfterDelay() {
      return this.afterDelay;
   }

   public void setAfterDelay(int afterDelay) {
      this.afterDelay = afterDelay;
   }

   public int getFixDamR() {
      return this.fixDamR;
   }

   public void setFixDamR(int fixDamR) {
      this.fixDamR = fixDamR;
   }

   public boolean isDoFirst() {
      return this.doFirst;
   }

   public void setDoFirst(boolean doFirst) {
      this.doFirst = doFirst;
   }

   public int getPreSkillIndex() {
      return this.preSkillIndex;
   }

   public void setPreSkillIndex(int preSkillIndex) {
      this.preSkillIndex = preSkillIndex;
   }

   public int getPreSkillCount() {
      return this.preSkillCount;
   }

   public void setPreSkillCount(int preSkillCount) {
      this.preSkillCount = preSkillCount;
   }

   public String getInfo() {
      return this.info;
   }

   public void setInfo(String info) {
      this.info = info;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public boolean isAfterDead() {
      return this.afterDead;
   }

   public void setAfterDead(boolean afterDead) {
      this.afterDead = afterDead;
   }

   public int getAfterAttack() {
      return this.afterAttack;
   }

   public void setAfterAttack(int afterAttack) {
      this.afterAttack = afterAttack;
   }

   public int getAfterAttackCount() {
      return this.afterAttackCount;
   }

   public void setAfterAttackCount(int afterAttackCount) {
      this.afterAttackCount = afterAttackCount;
   }

   public int getCastTime() {
      return this.castTime;
   }

   public void setCastTime(int castTime) {
      this.castTime = castTime;
   }

   public int getCoolTime() {
      return this.coolTime;
   }

   public void setCoolTime(int coolTime) {
      this.coolTime = coolTime;
   }

   public int getDelay() {
      return this.delay;
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   public int getUseLimit() {
      return this.useLimit;
   }

   public void setUseLimit(int useLimit) {
      this.useLimit = useLimit;
   }

   public String getSpeak() {
      return this.speak;
   }

   public void setSpeak(String speak) {
      this.speak = speak;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public List<Integer> getCooltimeOnSkill() {
      return this.cooltimeOnSkill;
   }

   public void setCooltimeOnSkill(List<Integer> cooltimeOnSkill) {
      this.cooltimeOnSkill = cooltimeOnSkill;
   }

   public WeatherMsg getWeatherMsg() {
      return this.weatherMsg;
   }

   public void setWeatherMsg(WeatherMsg weatherMsg) {
      this.weatherMsg = weatherMsg;
   }
}
