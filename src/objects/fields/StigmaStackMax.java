package objects.fields;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class StigmaStackMax {
   public String screenEffectUOL;
   public int stackMax;
   public String weatherMsg;
   public int weatherType;

   public StigmaStackMax(MapleData node) {
      this.stackMax = MapleDataTool.getInt("stackMax", node, 0);
      this.weatherType = MapleDataTool.getInt("weatherType", node, 0);
      this.weatherMsg = MapleDataTool.getString("weatherMsg", node, "");
      this.screenEffectUOL = MapleDataTool.getString("screenEffectUOL", node, "");
   }
}
