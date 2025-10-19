package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class MultiKillCount {
   private int multiKillCountMin;

   public MultiKillCount(MapleData root) {
      this.multiKillCountMin = MapleDataTool.getInt("min", root, -1);
   }

   public boolean check(int multiKill) {
      return this.multiKillCountMin != -1 ? this.multiKillCountMin <= multiKill : true;
   }
}
