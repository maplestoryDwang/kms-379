package objects.users.skills;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;

public class VMatrixOption {
   private static Map<Integer, VMatrixSlotExpensionMeso> mesos = new HashMap<>();
   public static VMatrixOptionInfo info;

   public static void Load() {
      MapleData data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz")).getData("VMatrixOption.img");
      info = new VMatrixOptionInfo(data.getChildByPath("info"));

      for (MapleData d : data.getChildByPath("slotExpansionMeso")) {
         Integer key = Integer.parseInt(d.getName());
         VMatrixSlotExpensionMeso m = new VMatrixSlotExpensionMeso(d);
         mesos.put(key, m);
      }
   }

   public static VMatrixSlotExpensionMeso getSlotExpesionMeso(Integer userLevel) {
      return mesos.get(userLevel);
   }
}
