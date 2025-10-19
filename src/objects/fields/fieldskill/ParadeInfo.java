package objects.fields.fieldskill;

import java.awt.Point;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class ParadeInfo {
   private int delay;
   private int type;
   private Point pos;
   private int preset;

   public ParadeInfo(int preset, MapleData data) {
      this.setPreset(preset);
      this.setPos(MapleDataTool.getPoint("pt", data, new Point(0, 0)));
      this.setDelay(MapleDataTool.getInt("delay", data, 0));
      this.setType(MapleDataTool.getInt("type", data, 0));
   }

   public Point getPos() {
      return this.pos;
   }

   public void setPos(Point pos) {
      this.pos = pos;
   }

   public int getDelay() {
      return this.delay;
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getPreset() {
      return this.preset;
   }

   public void setPreset(int preset) {
      this.preset = preset;
   }
}
