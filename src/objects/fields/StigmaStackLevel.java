package objects.fields;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class StigmaStackLevel {
   public int hp;
   public int mobSkillID;
   public int mobSkillLev;
   public int stack;
   public int stackTime;

   public StigmaStackLevel(MapleData node) {
      this.hp = MapleDataTool.getInt("hp", node, 0);
      this.stack = MapleDataTool.getInt("stack", node, 0);
      this.stackTime = MapleDataTool.getInt("stackTime", node, 0);
      this.mobSkillID = MapleDataTool.getInt("mobSkillID", node, 0);
      this.mobSkillLev = MapleDataTool.getInt("mobSkillLev", node, 0);
   }
}
