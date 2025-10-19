package objects.fields.gameobject.lifes.mobskills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class TransInfo {
   private int cooltime;
   private int hpTriggerOff;
   private int hpTriggerOn;
   private boolean linkHP;
   private List<Integer> targets;
   private Map<Integer, TransInfo.SkillInfo> skillInfos;
   private int time;
   private int withMob;

   public TransInfo(MapleData root) {
      int i = 0;
      this.setTargets(new ArrayList<>());

      while (true) {
         int data = MapleDataTool.getInt(String.valueOf(i++), root, -1);
         if (data == -1) {
            MapleData skill = root.getChildByPath("skill");
            this.setSkillInfos(new HashMap<>());
            if (skill != null) {
               for (MapleData r : skill.getChildren()) {
                  int name = Integer.parseInt(r.getName());
                  int level = MapleDataTool.getInt("level", r, 0);
                  int skillID = MapleDataTool.getInt("skill", r, 0);
                  this.getSkillInfos().put(name, new TransInfo.SkillInfo(level, skillID));
               }
            }

            this.setCooltime(MapleDataTool.getInt("cooltime", root, 0));
            this.setHpTriggerOff(MapleDataTool.getInt("hpTriggerOff", root, 0));
            this.setHpTriggerOn(MapleDataTool.getInt("hpTriggerOn", root, 0));
            this.setLinkHP(MapleDataTool.getInt("linkHP", root, 0) == 1);
            this.setTime(MapleDataTool.getInt("time", root, 0));
            this.setWithMob(MapleDataTool.getInt("withMob", root, 0));
            return;
         }

         this.getTargets().add(data);
      }
   }

   public int getCooltime() {
      return this.cooltime;
   }

   public void setCooltime(int cooltime) {
      this.cooltime = cooltime;
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

   public boolean isLinkHP() {
      return this.linkHP;
   }

   public void setLinkHP(boolean linkHP) {
      this.linkHP = linkHP;
   }

   public List<Integer> getTargets() {
      return this.targets;
   }

   public void setTargets(List<Integer> targets) {
      this.targets = targets;
   }

   public Map<Integer, TransInfo.SkillInfo> getSkillInfos() {
      return this.skillInfos;
   }

   public void setSkillInfos(Map<Integer, TransInfo.SkillInfo> skillInfos) {
      this.skillInfos = skillInfos;
   }

   public int getTime() {
      return this.time;
   }

   public void setTime(int time) {
      this.time = time;
   }

   public int getWithMob() {
      return this.withMob;
   }

   public void setWithMob(int withMob) {
      this.withMob = withMob;
   }

   public class SkillInfo {
      private int level;
      private int skill;

      public SkillInfo(int level, int skill) {
         this.setLevel(level);
         this.setSkill(skill);
      }

      public int getLevel() {
         return this.level;
      }

      public void setLevel(int level) {
         this.level = level;
      }

      public int getSkill() {
         return this.skill;
      }

      public void setSkill(int skill) {
         this.skill = skill;
      }
   }
}
