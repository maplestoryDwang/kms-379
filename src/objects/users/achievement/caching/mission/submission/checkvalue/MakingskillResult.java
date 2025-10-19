package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.Arrays;
import objects.item.Equip;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class MakingskillResult {
   boolean is_success;
   ItemCheck itemCheck;

   public MakingskillResult(MapleData root) {
      this.is_success = MapleDataTool.getInt(root.getChildByPath("is_success"), 0) > 0;
      MapleData item = root.getChildByPath("item");
      if (item != null) {
         this.itemCheck = new ItemCheck(item);
      }
   }

   public boolean check(boolean is_success, Equip result) {
      if (this.itemCheck != null) {
         if (is_success && this.itemCheck.check(Arrays.asList(result))) {
            return true;
         }
      } else if (is_success) {
         return true;
      }

      return false;
   }
}
