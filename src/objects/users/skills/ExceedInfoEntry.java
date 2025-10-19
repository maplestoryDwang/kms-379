package objects.users.skills;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class ExceedInfoEntry {
   private int skillID;
   private int time;

   public ExceedInfoEntry(MapleData data) {
      this.skillID = MapleDataTool.getInt("skill", data, 0);
      this.time = MapleDataTool.getInt("time", data, 0);
   }

   public int getSkillID() {
      return this.skillID;
   }

   public int getTime() {
      return this.time;
   }
}
