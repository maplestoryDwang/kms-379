package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.fields.child.union.MapleUnion;
import objects.users.MapleCharacter;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class UnionInfo {
   private int unionAttackerCountMin;
   private int unionAttackerCountMax;

   public UnionInfo(MapleData root) {
      this.setUnionAttackerCountMin(MapleDataTool.getInt("min", root, 0));
      this.setUnionAttackerCountMax(MapleDataTool.getInt("max", root, 0));
   }

   public int getUnionAttackerCountMin() {
      return this.unionAttackerCountMin;
   }

   public void setUnionAttackerCountMin(int unionAttackerCountMin) {
      this.unionAttackerCountMin = unionAttackerCountMin;
   }

   public int getUnionAttackerCountMax() {
      return this.unionAttackerCountMax;
   }

   public void setUnionAttackerCountMax(int unionAttackerCountMax) {
      this.unionAttackerCountMax = unionAttackerCountMax;
   }

   public boolean check(MapleCharacter player) {
      MapleUnion union = player.getMapleUnion();
      if (union == null) {
         return false;
      } else {
         return this.unionAttackerCountMin > 0
            ? this.unionAttackerCountMin <= union.activeRaiders.size() && this.unionAttackerCountMax >= union.activeRaiders.size()
            : true;
      }
   }
}
