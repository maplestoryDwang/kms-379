package objects.fields.gameobject.lifes;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class BossLucid {
   public static BossLucid.ButterflyData butterfly;

   public static void load() {
      MapleData data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz")).getData("BossLucid.img");
      butterfly = new BossLucid.ButterflyData(data.getChildByPath("Butterfly"));
      System.out.println("보스 루시드 데이터가 캐싱되었습니다.");
   }

   public static class ButterflyData {
      public BossLucid.ButterflyData.InfoData info;
      public List<Point> phase1Pos;
      public List<Point> phase2Pos;

      public ButterflyData(MapleData data) {
         this.info = new BossLucid.ButterflyData.InfoData(data.getChildByPath("info"));
         this.phase1Pos = new ArrayList<>();
         this.phase2Pos = new ArrayList<>();

         for (MapleData d : data.getChildByPath("phase1_pos")) {
            this.phase1Pos.add(MapleDataTool.getPoint("pos", d, new Point(0, 0)));
         }

         for (MapleData d : data.getChildByPath("phase2_pos")) {
            this.phase2Pos.add(MapleDataTool.getPoint("pos", d, new Point(0, 0)));
         }
      }

      public class InfoData {
         public int attackIdx;
         public List<BossLucid.ButterflyData.InfoData.Entry> entries = new ArrayList<>();

         public InfoData(MapleData data) {
            this.attackIdx = MapleDataTool.getInt("attackIdx", data, 0);
            int i = 0;

            while (true) {
               MapleData child = data.getChildByPath(String.valueOf(i));
               if (child == null) {
                  return;
               }

               this.entries.add(new BossLucid.ButterflyData.InfoData.Entry(child));
               i++;
            }
         }

         public class Entry {
            public int createCount;
            public int hpMax;
            public int hpMin;
            public int term;

            public Entry(MapleData data) {
               this.hpMin = MapleDataTool.getInt("hpMin", data, 0);
               this.hpMax = MapleDataTool.getInt("hpMax", data, 0);
               this.term = MapleDataTool.getInt("term", data, 0);
               this.createCount = MapleDataTool.getInt("createCount", data, 0);
            }
         }
      }
   }
}
