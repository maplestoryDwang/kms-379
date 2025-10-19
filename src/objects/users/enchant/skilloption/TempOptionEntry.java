package objects.users.enchant.skilloption;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class TempOptionEntry {
   private int id = 0;
   private int prob = 0;

   public TempOptionEntry(MapleData data) {
      this.id = MapleDataTool.getInt("id", data, 0);
      this.prob = MapleDataTool.getInt("prob", data, 0);
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getProb() {
      return this.prob;
   }

   public void setProb(int prob) {
      this.prob = prob;
   }
}
