package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class SkillUse {
   public static List<Integer> allSkillIDList = new ArrayList<>();
   private AchievementConditionType condition;
   private List<Integer> skillIDList;

   public SkillUse(MapleData root) {
      this.setSkillIDList(new ArrayList<>());
      this.setCondition(AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or")));
      MapleData values = root.getChildByPath("values");
      if (values != null) {
         int index = 0;

         while (true) {
            MapleData value = values.getChildByPath(String.valueOf(index++));
            if (value == null) {
               break;
            }

            int skillID = MapleDataTool.getInt("skill_id", value, 0);
            this.getSkillIDList().add(skillID);
            if (!allSkillIDList.contains(skillID)) {
               allSkillIDList.add(skillID);
            }
         }
      }
   }

   public boolean check(int skillID) {
      switch (this.condition) {
         case or:
            if (this.skillIDList.contains(skillID)) {
               return true;
            }
         default:
            return false;
      }
   }

   public AchievementConditionType getCondition() {
      return this.condition;
   }

   public void setCondition(AchievementConditionType condition) {
      this.condition = condition;
   }

   public List<Integer> getSkillIDList() {
      return this.skillIDList;
   }

   public void setSkillIDList(List<Integer> skillIDList) {
      this.skillIDList = skillIDList;
   }
}
