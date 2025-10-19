package objects.users.enchant.skilloption;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class SocketEntry {
   private int reqLevelMax = 0;
   private int reqLevelMin = 0;
   private int soulMax = 0;

   public SocketEntry(MapleData data) {
      this.reqLevelMax = MapleDataTool.getInt("reqLevelMax", data, 0);
      this.reqLevelMin = MapleDataTool.getInt("reqLevelMin", data, 0);
      this.soulMax = MapleDataTool.getInt("soulMax", data, 0);
   }

   public int getReqLevelMax() {
      return this.reqLevelMax;
   }

   public void setReqLevelMax(int reqLevelMax) {
      this.reqLevelMax = reqLevelMax;
   }

   public int getReqLevelMin() {
      return this.reqLevelMin;
   }

   public void setReqLevelMin(int reqLevelMin) {
      this.reqLevelMin = reqLevelMin;
   }

   public int getSoulMax() {
      return this.soulMax;
   }

   public void setSoulMax(int soulMax) {
      this.soulMax = soulMax;
   }
}
