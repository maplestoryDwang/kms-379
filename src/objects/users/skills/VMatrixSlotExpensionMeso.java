package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class VMatrixSlotExpensionMeso {
   private List<Long> mesos = new ArrayList<>();

   public VMatrixSlotExpensionMeso(MapleData data) {
      int i = 0;

      while (true) {
         MapleData d = data.getChildByPath(String.valueOf(i));
         if (d == null) {
            return;
         }

         long meso_ = MapleDataTool.getLong(d, 0);
         this.mesos.add(meso_);
         i++;
      }
   }

   public long getExpendedSlotMeso(int curExpandedSlot) {
      return this.mesos.size() <= curExpandedSlot ? -1L : this.mesos.get(curExpandedSlot);
   }
}
