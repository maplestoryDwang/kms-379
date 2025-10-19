package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class SkillCheck {
   AchievementConditionType condition;
   int skillID;
   int skillLevelMin = 0;
   int skillLevelMax = Integer.MAX_VALUE;

   public SkillCheck(MapleData root) {
      this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or"));
      if (this.condition != AchievementConditionType.or) {
         System.out.println("SkillCheck and발견");
         System.out.println(root.getParent().getParent().getParent().getParent().getParent().getParent().getName());
      }

      MapleData values = root.getChildByPath("values");
      if (values != null) {
         for (MapleData value : values) {
            this.skillID = MapleDataTool.getInt("skill_id", value, 0);
            this.skillLevelMin = MapleDataTool.getInt("skill_level/min", value, 0);
            this.skillLevelMax = MapleDataTool.getInt("skill_level/max", value, Integer.MAX_VALUE);
         }
      }
   }

   public boolean check(int skillID, int lv) {
      switch (this.condition) {
         case or:
            boolean ret = false;
            if (this.skillID == skillID && lv >= this.skillLevelMin && lv <= this.skillLevelMax) {
               ret = true;
            }

            return ret;
         default:
            return false;
      }
   }
}
