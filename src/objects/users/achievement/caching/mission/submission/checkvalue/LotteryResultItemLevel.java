package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class LotteryResultItemLevel {
   int level;

   public LotteryResultItemLevel(MapleData root) {
      this.level = MapleDataTool.getInt(root, 0);
   }

   public boolean check(int resultLv) {
      return resultLv == this.level;
   }
}
