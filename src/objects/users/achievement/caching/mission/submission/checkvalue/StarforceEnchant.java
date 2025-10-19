package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class StarforceEnchant {
   AchievementConditionType condition;
   int starforceStarsMin;
   int starforceStarsMax;
   String result;
   String starCatchResult;

   public StarforceEnchant(MapleData root) {
      MapleData starforce_enchant_result = root.getChildByPath("starforce_enchant_result");
      if (starforce_enchant_result != null) {
         this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", starforce_enchant_result, "or"));

         for (MapleData value : starforce_enchant_result.getChildByPath("values")) {
            this.result = MapleDataTool.getString(value, "");
         }
      }

      MapleData starforce_stars = root.getChildByPath("starforce_stars");
      if (starforce_stars != null) {
         this.starforceStarsMin = MapleDataTool.getInt(starforce_stars.getChildByPath("min"));
         this.starforceStarsMax = MapleDataTool.getInt(starforce_stars.getChildByPath("max"));
      }

      MapleData star_catch_result = root.getChildByPath("star_catch_result");
      if (star_catch_result != null) {
         this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", star_catch_result, "or"));

         for (MapleData value : star_catch_result.getChildByPath("values")) {
            this.starCatchResult = MapleDataTool.getString(value, "");
         }
      }

      if (this.condition == null) {
         this.condition = AchievementConditionType.or;
      }
   }

   public boolean check(String starforceResult, int starforce, String catchResult) {
      boolean ret = false;
      switch (this.condition) {
         case or:
            if (this.result != null) {
               if (this.starforceStarsMin > 0) {
                  if (this.result.equals(starforceResult) && this.starforceStarsMin >= starforce && this.starforceStarsMax <= starforce) {
                     ret = true;
                  }
               } else {
                  ret = this.result.equals(starforceResult);
               }
            }

            if (this.starCatchResult != null) {
               ret = this.starCatchResult.equals(catchResult);
            }

            if (this.result == null
               && this.starCatchResult == null
               && this.starforceStarsMin > 0
               && starforce >= this.starforceStarsMin
               && starforce <= this.starforceStarsMax) {
               ret = true;
            }
         default:
            return ret;
      }
   }
}
