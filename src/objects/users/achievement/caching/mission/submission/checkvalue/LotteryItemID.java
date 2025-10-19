package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class LotteryItemID {
   List<LotteryItemID.lotteryItem> items = new ArrayList<>();

   public LotteryItemID(MapleData root) {
      MapleData values = root.getChildByPath("values");
      if (values != null) {
         for (MapleData value : values) {
            int min = MapleDataTool.getInt("min", value, 0);
            int max = MapleDataTool.getInt("max", value, 0);
            this.items.add(new LotteryItemID.lotteryItem(min, max));
         }
      }
   }

   public boolean check(int lotteryItemId) {
      for (LotteryItemID.lotteryItem item : this.items) {
         if (item.min <= lotteryItemId && item.max >= lotteryItemId) {
            return true;
         }
      }

      return false;
   }

   private class lotteryItem {
      int min;
      int max;

      public lotteryItem(int min, int max) {
         this.min = min;
         this.max = max;
      }
   }
}
