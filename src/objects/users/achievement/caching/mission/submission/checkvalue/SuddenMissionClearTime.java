package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class SuddenMissionClearTime {
   private int min;
   private int max;

   public SuddenMissionClearTime(MapleData root) {
      this.min = MapleDataTool.getInt(root.getChildByPath("min"), 0);
      this.max = MapleDataTool.getInt(root.getChildByPath("max"), Integer.MAX_VALUE);
   }

   public boolean check(int clearTimeSecond) {
      return clearTimeSecond >= this.min && clearTimeSecond <= this.max;
   }
}
