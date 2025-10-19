package objects.fields;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class StigmaStackType {
   public String type;
   public String weatherMsg;
   public int weatherType;

   public StigmaStackType(MapleData node) {
      this.type = MapleDataTool.getString("type", node, "");
      this.weatherType = MapleDataTool.getInt("weatherType", node, 0);
      this.weatherMsg = MapleDataTool.getString("weatherMsg", node, "");
   }
}
