package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class ItemReward {
   AchievementConditionType condition;
   int itemLabelGradeMin;
   int itemLabelGradeMax;

   public ItemReward(MapleData root) {
      this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or"));
      if (this.condition != AchievementConditionType.or) {
         System.out.println("ItemReward and발견");
         System.out.println(root.getParent().getParent().getParent().getParent().getParent().getParent().getName());
      }

      MapleData values = root.getChildByPath("values");
      if (values != null) {
         for (MapleData value : values) {
            MapleData itemLabelGrade = value.getChildByPath("item_label_grade");
            if (itemLabelGrade != null) {
               this.itemLabelGradeMin = MapleDataTool.getInt(itemLabelGrade.getChildByPath("min"), 0);
               this.itemLabelGradeMax = MapleDataTool.getInt(itemLabelGrade.getChildByPath("max"), Integer.MAX_VALUE);
            }
         }
      }
   }

   public boolean check(int labelGrade) {
      switch (this.condition) {
         case or:
            boolean ret = false;
            if (this.itemLabelGradeMin <= labelGrade && this.itemLabelGradeMax >= labelGrade) {
               ret = true;
            }

            return ret;
         default:
            return false;
      }
   }
}
