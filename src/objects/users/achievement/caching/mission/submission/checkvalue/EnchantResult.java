package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class EnchantResult {
   boolean isSuccess;
   int min;
   int max;

   public EnchantResult(MapleData root) {
      this.isSuccess = MapleDataTool.getInt(root.getChildByPath("is_success"), 0) > 0;
      MapleData enchant_success_prob = root.getChildByPath("enchant_success_prob");
      if (enchant_success_prob != null) {
         this.min = MapleDataTool.getInt(enchant_success_prob.getChildByPath("min"));
         this.max = MapleDataTool.getInt(enchant_success_prob.getChildByPath("max"));
      }
   }

   public boolean check(int sucRate, boolean success) {
      boolean ret = false;
      if (this.min > 0) {
         if (this.min >= sucRate && sucRate <= this.max && (success && this.isSuccess || !success && !this.isSuccess)) {
            ret = true;
         }
      } else if (success && this.isSuccess || !success && !this.isSuccess) {
         ret = true;
      }

      return ret;
   }
}
