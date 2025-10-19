package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Meso {
   private int min;
   private int max;

   public Meso(MapleData root) {
      this.min = MapleDataTool.getInt(root.getChildByPath("min"), 0);
      this.max = MapleDataTool.getInt(root.getChildByPath("max"), Integer.MAX_VALUE);
   }

   public boolean check(int meso) {
      return meso >= this.min && meso <= this.max;
   }
}
