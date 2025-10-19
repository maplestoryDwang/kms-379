package objects.fields.gameobject.lifes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class MobZoneInfo {
   private static Map<Integer, MobZoneInfo> infos = new HashMap<>();
   public MobZoneInfo.InOut autoDec;
   public MobZoneInfo.InOut consume;
   public MobZoneInfo.InOut damage;
   public MobZoneInfo.InOut healRate;

   public static void load() {
      String WZpath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz"));

      for (MapleData node : prov.getData("MobZoneInfo.img")) {
         int templateID = Integer.parseInt(node.getName());
         MobZoneInfo info = new MobZoneInfo();
         info.damage = new MobZoneInfo.InOut(node.getChildByPath("damage"));
         info.consume = new MobZoneInfo.InOut(node.getChildByPath("consume"));
         info.autoDec = new MobZoneInfo.InOut(node.getChildByPath("autoDec"));
         info.healRate = new MobZoneInfo.InOut(node.getChildByPath("healRate"));
         infos.put(templateID, info);
      }
   }

   public static MobZoneInfo getInfo(int templateID) {
      return infos.get(templateID);
   }

   public static class InOut {
      public int in;
      public int out;

      public InOut(MapleData data) {
         this.in = MapleDataTool.getInt("in", data, 0);
         this.out = MapleDataTool.getInt("out", data, 0);
      }
   }
}
