package objects.fields.fieldskill;

import java.awt.Point;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class PresetInfo {
   private int index;
   private int preset;
   private Point pos;

   public PresetInfo(int preset, MapleData data) {
      this.preset = preset;
      this.setPos(MapleDataTool.getPoint(data));
   }

   public PresetInfo() {
   }

   public Point getPos() {
      return this.pos;
   }

   public void setPos(Point pos) {
      this.pos = pos;
   }
}
