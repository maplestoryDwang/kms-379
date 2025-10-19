package objects.fields.gameobject.lifes;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import objects.utils.Rect;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class BossPapulatus {
   public static BossPapulatus.HealMissionData healMissionData;
   public static BossPapulatus.TickTockCraneData tickTockCraneData;
   public static BossPapulatus.TickTockLaserData tickTockLaserData;

   public static void load() {
      MapleData data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz")).getData("BossPapulatus.img");
      healMissionData = new BossPapulatus.HealMissionData(data.getChildByPath("heal_Mission"));
      tickTockCraneData = new BossPapulatus.TickTockCraneData(data.getChildByPath("ticktockCrane"));
      tickTockLaserData = new BossPapulatus.TickTockLaserData(data.getChildByPath("ticktockLaser"));
      System.out.println("보스 파풀라투스 데이터가 캐싱되었습니다.");
   }

   public static class HealMissionData {
      public static Map<Integer, Integer> hp;
      public static BossPapulatus.HealMissionData.DifficultyData easy;
      public static BossPapulatus.HealMissionData.DifficultyData normal;
      public static BossPapulatus.HealMissionData.DifficultyData chaos;

      public HealMissionData(MapleData data) {
         MapleData hp = data.getChildByPath("hp");
         if (hp != null) {
            BossPapulatus.HealMissionData.hp = new HashMap<>();

            for (MapleData d : hp.getChildren()) {
               BossPapulatus.HealMissionData.hp.put(Integer.parseInt(d.getName()), MapleDataTool.getInt(d.getName(), hp, 0));
            }
         }

         MapleData difficulty = data.getChildByPath("difficulty");
         if (difficulty != null) {
            easy = new BossPapulatus.HealMissionData.DifficultyData(difficulty.getChildByPath("easy"));
            normal = new BossPapulatus.HealMissionData.DifficultyData(difficulty.getChildByPath("normal"));
            chaos = new BossPapulatus.HealMissionData.DifficultyData(difficulty.getChildByPath("chaos"));
         }
      }

      public class DifficultyData {
         public int hpMax;
         public int hpMin;
         public Map<Integer, Integer> setCount;

         public DifficultyData(MapleData root) {
            this.hpMax = MapleDataTool.getInt("hp_max", root, 0);
            this.hpMin = MapleDataTool.getInt("hp_min", root, 0);
            MapleData setCount = root.getChildByPath("setCount");
            this.setCount = new HashMap<>();
            if (setCount != null) {
               for (MapleData sc : setCount.getChildren()) {
                  this.setCount.put(Integer.parseInt(sc.getName()), MapleDataTool.getInt(sc.getName(), setCount, 0));
               }
            }
         }
      }
   }

   public static class TickTockCraneData {
      public static Map<Integer, BossPapulatus.TickTockCraneData.InfoData> info;
      public static Map<String, BossPapulatus.TickTockCraneData.InfoData> infoByTag;

      public TickTockCraneData(MapleData data) {
         MapleData info = data.getChildByPath("info");
         if (info != null) {
            BossPapulatus.TickTockCraneData.info = new HashMap<>();
            infoByTag = new HashMap<>();

            for (MapleData d : info.getChildren()) {
               int idx = Integer.parseInt(d.getName());
               BossPapulatus.TickTockCraneData.InfoData infoData = new BossPapulatus.TickTockCraneData.InfoData(idx, d);
               BossPapulatus.TickTockCraneData.info.put(idx, infoData);
               infoByTag.put(infoData.objTag, infoData);
            }
         }
      }

      public class InfoData {
         public int idx;
         public Rect ltRb;
         public int moveBoundY;
         public String objTag;

         public InfoData(int idx, MapleData data) {
            this.idx = idx;
            Point lb = MapleDataTool.getPoint("lt", data, new Point(0, 0));
            Point rb = MapleDataTool.getPoint("rb", data, new Point(0, 0));
            this.ltRb = new Rect(lb, rb);
            this.objTag = MapleDataTool.getString("objTag", data, "");
            this.moveBoundY = MapleDataTool.getInt("moveBoundY", data, 0);
         }
      }
   }

   public static class TickTockLaserData {
      public static Map<Integer, BossPapulatus.TickTockLaserData.ObjInfoData> objInfo;
      public static Map<Integer, BossPapulatus.TickTockLaserData.PairInfoData> pairInfo;
      public static Map<Integer, BossPapulatus.TickTockLaserData.PhaseInfoData> phaseInfo;

      public TickTockLaserData(MapleData data) {
         MapleData objInfo = data.getChildByPath("objInfo");
         if (objInfo != null) {
            BossPapulatus.TickTockLaserData.objInfo = new HashMap<>();

            for (MapleData d : objInfo.getChildren()) {
               BossPapulatus.TickTockLaserData.objInfo.put(Integer.parseInt(d.getName()), new BossPapulatus.TickTockLaserData.ObjInfoData(d));
            }
         }

         MapleData pairInfo = data.getChildByPath("pairInfo");
         if (pairInfo != null) {
            BossPapulatus.TickTockLaserData.pairInfo = new HashMap<>();

            for (MapleData d : pairInfo.getChildren()) {
               BossPapulatus.TickTockLaserData.pairInfo.put(Integer.parseInt(d.getName()), new BossPapulatus.TickTockLaserData.PairInfoData(d));
            }
         }

         MapleData phaseInfo = data.getChildByPath("phaseInfo");
         if (phaseInfo != null) {
            BossPapulatus.TickTockLaserData.phaseInfo = new HashMap<>();

            for (MapleData d : phaseInfo.getChildren()) {
               BossPapulatus.TickTockLaserData.phaseInfo.put(Integer.parseInt(d.getName()), new BossPapulatus.TickTockLaserData.PhaseInfoData(d));
            }
         }
      }

      public class ObjInfoData {
         public int angleBase;
         public int anglePerSec;
         public String objTag;
         public int type;

         public ObjInfoData(MapleData data) {
            this.angleBase = MapleDataTool.getInt("angleBase", data, 0);
            this.anglePerSec = MapleDataTool.getInt("anglePerSec", data, 0);
            this.type = MapleDataTool.getInt("type", data, 0);
            this.objTag = MapleDataTool.getString("objTag", data, "");
         }
      }

      public class PairInfoData {
         public int duration;
         public int obj0;
         public int obj1;

         public PairInfoData(MapleData data) {
            this.duration = MapleDataTool.getInt("duration", data, 0);
            this.obj0 = MapleDataTool.getInt("obj0", data, 0);
            this.obj1 = MapleDataTool.getInt("obj1", data, 0);
         }
      }

      public class PhaseInfoData {
         public int cooltime;
         public int pair;

         public PhaseInfoData(MapleData data) {
            this.pair = MapleDataTool.getInt("pair", data, 0);
            this.cooltime = MapleDataTool.getInt("cooltime", data, 0);
         }
      }
   }
}
