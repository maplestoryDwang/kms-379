package objects.fields.gameobject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import objects.utils.Pair;
import objects.utils.StringUtil;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class ReactorFactory {
   private static final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Reactor.wz"));
   private static Map<Integer, ReactorStats> reactorStats = new HashMap<>();

   public static final ReactorStats getReactor(int rid) {
      ReactorStats stats = reactorStats.get(rid);
      if (stats == null) {
         int infoId = rid;
         MapleData reactorData = data.getData(StringUtil.getLeftPaddedStr(Integer.toString(rid) + ".img", '0', 11));
         MapleData link = reactorData.getChildByPath("info/link");
         if (link != null) {
            infoId = MapleDataTool.getIntConvert("info/link", reactorData);
            stats = reactorStats.get(infoId);
         }

         if (stats == null) {
            stats = new ReactorStats();
            reactorData = data.getData(StringUtil.getLeftPaddedStr(Integer.toString(infoId) + ".img", '0', 11));
            if (reactorData == null) {
               return stats;
            }

            boolean canTouch = MapleDataTool.getInt("info/activateByTouch", reactorData, 0) > 0;
            boolean areaSet = false;
            boolean foundState = false;
            byte i = 0;

            while (true) {
               MapleData reactorD = reactorData.getChildByPath(String.valueOf((int)i));
               if (reactorD == null) {
                  reactorStats.put(infoId, stats);
                  if (rid != infoId) {
                     reactorStats.put(rid, stats);
                  }
                  break;
               }

               MapleData reactorInfoData_ = reactorD.getChildByPath("event");
               if (reactorInfoData_ != null && reactorInfoData_.getChildByPath("0") != null) {
                  MapleData reactorInfoData = reactorInfoData_.getChildByPath("0");
                  Pair<Integer, Integer> reactItem = null;
                  int type = MapleDataTool.getIntConvert("type", reactorInfoData);
                  if (type == 100) {
                     reactItem = new Pair<>(MapleDataTool.getIntConvert("0", reactorInfoData), MapleDataTool.getIntConvert("1", reactorInfoData, 1));
                     if (!areaSet) {
                        stats.setTL(MapleDataTool.getPoint("lt", reactorInfoData));
                        stats.setBR(MapleDataTool.getPoint("rb", reactorInfoData));
                        areaSet = true;
                     }
                  }

                  foundState = true;
                  stats.addState(
                     i,
                     type,
                     reactItem,
                     (byte)MapleDataTool.getIntConvert("state", reactorInfoData),
                     MapleDataTool.getIntConvert("timeOut", reactorInfoData_, -1),
                     (byte)(
                        canTouch
                           ? 2
                           : (
                              MapleDataTool.getIntConvert("2", reactorInfoData, 0) <= 0 && reactorInfoData.getChildByPath("clickArea") == null && type != 9
                                 ? 0
                                 : 1
                           )
                     )
                  );
               } else {
                  stats.addState(i, 999, null, (byte)(foundState ? -1 : i + 1), 0, (byte)0);
               }

               i++;
            }
         } else {
            reactorStats.put(rid, stats);
         }
      }

      return stats;
   }
}
