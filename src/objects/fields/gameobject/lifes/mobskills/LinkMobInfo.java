package objects.fields.gameobject.lifes.mobskills;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class LinkMobInfo {
   private int hpTriggerOff;
   private int hpTriggerOn;
   private int level;
   private boolean linkRevive;
   private int mob;
   private int range;
   private int reviveDelay;
   private int reviveHP;
   private int skill;

   public LinkMobInfo(MapleData root) {
      this.hpTriggerOff = MapleDataTool.getInt("hpTriggerOff", root, 0);
      this.hpTriggerOn = MapleDataTool.getInt("hpTriggerOn", root, 0);
      this.level = MapleDataTool.getInt("level", root, 0);
      this.linkRevive = MapleDataTool.getInt("linkRevive", root, 0) == 1;
      this.mob = MapleDataTool.getInt("mob", root, 0);
      this.range = MapleDataTool.getInt("range", root, 0);
      this.reviveDelay = MapleDataTool.getInt("reviveDelay", root, 0);
      this.reviveHP = MapleDataTool.getInt("reviveHP", root, 0);
      this.skill = MapleDataTool.getInt("skill", root, 0);
   }

   public int getHpTriggerOff() {
      return this.hpTriggerOff;
   }

   public void setHpTriggerOff(int hpTriggerOff) {
      this.hpTriggerOff = hpTriggerOff;
   }

   public int getHpTriggerOn() {
      return this.hpTriggerOn;
   }

   public void setHpTriggerOn(int hpTriggerOn) {
      this.hpTriggerOn = hpTriggerOn;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public boolean isLinkRevive() {
      return this.linkRevive;
   }

   public void setLinkRevive(boolean linkRevive) {
      this.linkRevive = linkRevive;
   }

   public int getMob() {
      return this.mob;
   }

   public void setMob(int mob) {
      this.mob = mob;
   }

   public int getRange() {
      return this.range;
   }

   public void setRange(int range) {
      this.range = range;
   }

   public int getReviveDelay() {
      return this.reviveDelay;
   }

   public void setReviveDelay(int reviveDelay) {
      this.reviveDelay = reviveDelay;
   }

   public int getReviveHP() {
      return this.reviveHP;
   }

   public void setReviveHP(int reviveHP) {
      this.reviveHP = reviveHP;
   }

   public int getSkill() {
      return this.skill;
   }

   public void setSkill(int skill) {
      this.skill = skill;
   }
}
