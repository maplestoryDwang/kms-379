package objects.users.achievement.caching;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class AchievementInsigniaInfo {
   private String name;
   private String grade;
   private int skill;
   private boolean unlockByGrade;
   private String desc;

   public AchievementInsigniaInfo(MapleData data) {
      this.setName(MapleDataTool.getString("name", data, ""));
      this.setGrade(MapleDataTool.getString("grade", data, ""));
      this.setSkill(MapleDataTool.getInt("skill", data, 0));
      this.setUnlockByGrade(MapleDataTool.getInt("unlockByGrade", data, 0) == 1);
      this.setDesc(MapleDataTool.getString("desc", data, ""));
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getGrade() {
      return this.grade;
   }

   public void setGrade(String grade) {
      this.grade = grade;
   }

   public int getSkill() {
      return this.skill;
   }

   public void setSkill(int skill) {
      this.skill = skill;
   }

   public boolean isUnlockByGrade() {
      return this.unlockByGrade;
   }

   public void setUnlockByGrade(boolean unlockByGrade) {
      this.unlockByGrade = unlockByGrade;
   }

   public String getDesc() {
      return this.desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }
}
