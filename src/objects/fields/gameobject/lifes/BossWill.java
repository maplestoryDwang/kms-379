package objects.fields.gameobject.lifes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class BossWill {
   public static BossWill.BeholderData beholder;

   public static void load() {
      MapleData data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz")).getData("BossWill.img");
      beholder = new BossWill.BeholderData(data.getChildByPath("Beholder"));
      System.out.println("보스 윌 데이터가 캐싱되었습니다.");
   }

   public static class BeholderData {
      public BossWill.BeholderData.InfoData easyData;
      public BossWill.BeholderData.InfoData normalData;
      public BossWill.BeholderData.InfoData hardData;

      public BeholderData(MapleData data) {
         MapleData config = data.getChildByPath("Config");
         this.easyData = new BossWill.BeholderData.InfoData(config.getChildByPath("0"));
         this.normalData = new BossWill.BeholderData.InfoData(config.getChildByPath("1"));
         this.hardData = new BossWill.BeholderData.InfoData(config.getChildByPath("2"));
      }

      public class InfoData {
         public BossWill.BeholderData.InfoData.Entry phase1;
         public BossWill.BeholderData.InfoData.Entry phase2;
         public BossWill.BeholderData.InfoData.Entry phase3;

         public InfoData(MapleData data) {
            this.phase1 = new BossWill.BeholderData.InfoData.Entry(data.getChildByPath("Phase1"));
            this.phase2 = new BossWill.BeholderData.InfoData.Entry(data.getChildByPath("Phase2"));
            this.phase3 = new BossWill.BeholderData.InfoData.Entry(data.getChildByPath("Phase3"));
         }

         public class Entry {
            private List<BossWill.BeholderData.InfoData.Gen> gen = new ArrayList<>();
            private List<BossWill.BeholderData.InfoData.Straight> straight;

            public Entry(MapleData data) {
               for (MapleData d : data.getChildByPath("Gen")) {
                  this.gen.add(InfoData.this.new Gen(d));
               }

               this.straight = new ArrayList<>();

               for (MapleData d : data.getChildByPath("Straight")) {
                  this.straight.add(InfoData.this.new Straight(d));
               }
            }

            public List<BossWill.BeholderData.InfoData.Gen> getGen() {
               return this.gen;
            }

            public List<BossWill.BeholderData.InfoData.Straight> getStraight() {
               return this.straight;
            }
         }

         public class Gen {
            private List<BossWill.BeholderData.InfoData.Gen.GenEntry> entry = new ArrayList<>();

            public Gen(MapleData data) {
               for (MapleData d : data) {
                  BossWill.BeholderData.InfoData.Gen.GenEntry gen = new BossWill.BeholderData.InfoData.Gen.GenEntry(d);
                  this.entry.add(gen);
               }
            }

            public List<BossWill.BeholderData.InfoData.Gen.GenEntry> getEntry() {
               return this.entry;
            }

            public class GenEntry {
               private int x;
               private int ry;

               public GenEntry(MapleData data) {
                  this.x = MapleDataTool.getInt("x", data, 0);
                  this.ry = MapleDataTool.getInt("ry", data, 0);
               }

               public int getX() {
                  return this.x;
               }

               public void setX(int x) {
                  this.x = x;
               }

               public int getRy() {
                  return this.ry;
               }

               public void setRy(int ry) {
                  this.ry = ry;
               }
            }
         }

         public class Straight {
            private BossWill.BeholderData.InfoData.Straight.StraightEntry entry;

            public Straight(MapleData data) {
               this.entry = new BossWill.BeholderData.InfoData.Straight.StraightEntry(data);
            }

            public BossWill.BeholderData.InfoData.Straight.StraightEntry getEntry() {
               return this.entry;
            }

            public class StraightEntry {
               private int shootAngleRate;
               private int shootSpeed;
               private int shootCount;
               private int shootInterval;

               public StraightEntry(MapleData data) {
                  this.shootAngleRate = MapleDataTool.getInt("shootAngleRate", data, 0);
                  this.shootSpeed = MapleDataTool.getInt("shootSpeed", data, 0);
                  this.shootCount = MapleDataTool.getInt("shootCount", data, 0);
                  this.shootInterval = MapleDataTool.getInt("shootInterval", data, 0);
               }

               public int getShootAngle() {
                  return this.shootAngleRate;
               }

               public void setShootAngle(int shootAngle) {
                  this.shootAngleRate = shootAngle;
               }

               public int getShootSpeed() {
                  return this.shootSpeed;
               }

               public void setShootSpeed(int shootSpeed) {
                  this.shootSpeed = shootSpeed;
               }

               public int getShootCount() {
                  return this.shootCount;
               }

               public void setShootCount(int shootCount) {
                  this.shootCount = shootCount;
               }

               public int getShootInterval() {
                  return this.shootInterval;
               }

               public void setShootInterval(int shootInterval) {
                  this.shootInterval = shootInterval;
               }
            }
         }
      }
   }
}
