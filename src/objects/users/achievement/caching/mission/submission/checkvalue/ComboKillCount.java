package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class ComboKillCount {
   private int comboKillCountMin;
   private int comboKillCountMax;

   public ComboKillCount(MapleData root) {
      this.comboKillCountMin = MapleDataTool.getInt("min", root, -1);
      this.comboKillCountMax = MapleDataTool.getInt("max", root, -1);
   }

   public boolean check(int comboKill) {
      return this.comboKillCountMin <= comboKill && this.comboKillCountMax >= comboKill;
   }
}
