package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class SkillID {
   AchievementConditionType condition;
   List<Integer> skillids = new ArrayList<>();

   public SkillID(MapleData root) {
      this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or"));
      if (this.condition != AchievementConditionType.or) {
         System.out.println("AND가 있다고!?");
         System.out.println(root.getParent().getParent().getParent().getParent().getParent().getParent().getName());
      }

      MapleData values = root.getChildByPath("values");
      if (values != null) {
         for (MapleData data : values) {
            int id = MapleDataTool.getInt(data, -1);
            if (id != -1) {
               this.skillids.add(id);
            }
         }
      }
   }

   public boolean check(int skillID) {
      switch (this.condition) {
         case or:
            if (this.skillids.contains(skillID)) {
               return true;
            }
         default:
            return false;
      }
   }
}
