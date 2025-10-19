package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class NpcID {
   public static List<Integer> allNpcIDList = new ArrayList<>();
   AchievementConditionType condition;
   List<Integer> npcIDList;

   public NpcID(MapleData root) {
      this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or"));
      this.npcIDList = new ArrayList<>();
      MapleData values = root.getChildByPath("values");
      if (values != null) {
         int index = 0;

         while (true) {
            int value = MapleDataTool.getInt(String.valueOf(index++), values, -1);
            if (value == -1) {
               break;
            }

            this.npcIDList.add(value);
            if (!allNpcIDList.contains(value)) {
               allNpcIDList.add(value);
            }
         }
      }
   }

   public boolean check(int npcID) {
      switch (this.condition) {
         case or:
            if (!this.npcIDList.contains(npcID)) {
               return false;
            }
         default:
            return true;
      }
   }
}
