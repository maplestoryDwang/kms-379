package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class RuneStoneID {
   AchievementConditionType condition;
   List<Integer> runes = new ArrayList<>();

   public RuneStoneID(MapleData root) {
      this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or"));
      if (this.condition != AchievementConditionType.or) {
         System.out.println("룬스톤ID에서 and발견");
         System.out.println(root.getParent().getParent().getParent().getParent().getParent().getParent().getName());
      }

      MapleData values = root.getChildByPath("values");
      if (values != null) {
         int index = 0;

         while (true) {
            int rid = MapleDataTool.getInt(String.valueOf(index++), values, -1);
            if (rid == -1) {
               break;
            }

            this.runes.add(rid);
         }
      }
   }

   public boolean check(int runeNumber) {
      switch (this.condition) {
         case or:
            boolean ret = true;
            if (this.runes.size() > 0 && !this.runes.contains(runeNumber)) {
               ret = false;
            }

            return ret;
         default:
            return false;
      }
   }
}
