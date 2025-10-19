package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.item.MapleItemInformationProvider;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class SpelltraceEnchant {
   private boolean innocentScroll;
   private boolean whiteScroll;

   public SpelltraceEnchant(MapleData root) {
      this.innocentScroll = MapleDataTool.getInt(root.getChildByPath("is_innocent_scroll"), 0) > 0;
      this.whiteScroll = MapleDataTool.getInt(root.getChildByPath("is_white_scroll"), 0) > 0;
   }

   public boolean check(int scroll) {
      MapleItemInformationProvider info = MapleItemInformationProvider.getInstance();
      String name = info.getName(scroll);
      if (name != null) {
         if (name.contains("이노센트") && this.innocentScroll) {
            return true;
         }

         if (name.contains("백의") && this.whiteScroll) {
            return true;
         }
      }

      return false;
   }
}
