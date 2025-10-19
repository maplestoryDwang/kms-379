package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class HitDamagePercent {
   private int hitDamagePercentMin;

   public HitDamagePercent(MapleData root) {
      this.hitDamagePercentMin = MapleDataTool.getInt("min", root, 0);
   }

   public boolean check(int hitDamagePercent) {
      boolean hitDamagePercentCheck = true;
      if (this.hitDamagePercentMin != 0 && hitDamagePercent < this.hitDamagePercentMin) {
         hitDamagePercentCheck = false;
      }

      return hitDamagePercentCheck;
   }
}
