package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class LotteryResultItemID {
   int itemID;

   public LotteryResultItemID(MapleData root) {
      this.itemID = MapleDataTool.getInt(root, 0);
   }

   public boolean check(int result) {
      return result == this.itemID;
   }
}
