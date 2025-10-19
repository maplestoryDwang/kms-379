package network.login;

import constants.GameConstants;
import constants.ServerConstants;
import constants.devtempConstants.DummyCharacterName;
import database.DBConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import objects.utils.Triple;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class LoginInformationProvider {
   private static final LoginInformationProvider instance = new LoginInformationProvider();
   protected final List<String> ForbiddenName = new ArrayList<>();
   protected final Map<Triple<Integer, String, Integer>, List<Integer>> makeCharInfo = new HashMap<>();

   public static LoginInformationProvider getInstance() {
      return instance;
   }

   protected LoginInformationProvider() {
      String WZpath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz"));
      MapleData nameData = prov.getData("ForbiddenName.img");

      for (MapleData data : nameData.getChildren()) {
         this.ForbiddenName.add(MapleDataTool.getString(data));
      }

      nameData = prov.getData("Curse.img");

      for (MapleData data : nameData.getChildren()) {
         this.ForbiddenName.add(MapleDataTool.getString(data).split(",")[0]);
      }

      MapleData infoData = prov.getData("MakeCharInfo.img");

      label135:
      for (MapleData dat : infoData) {
         try {
            int type;
            if (dat.getName().equals("000_1")) {
               type = LoginInformationProvider.JobType.DualBlade.type;
            } else if (dat.getName().equals("000_3")) {
               type = LoginInformationProvider.JobType.PathFinder.type;
            } else {
               type = LoginInformationProvider.JobType.getById(Integer.parseInt(dat.getName())).type;
            }

            Iterator key = dat.iterator();

            while (true) {
               MapleData d;
               int val;
               while (true) {
                  if (!key.hasNext()) {
                     continue label135;
                  }

                  d = (MapleData)key.next();
                  if (d.getName().contains("female")) {
                     val = 1;
                     break;
                  }

                  if (d.getName().contains("male")) {
                     val = 0;
                     break;
                  }
               }

               for (MapleData da : d) {
                  Triple<Integer, String, Integer> keyx = new Triple<>(val, MapleDataTool.getString(da.getChildByPath("name"), ""), type);
                  List<Integer> our = this.makeCharInfo.get(keyx);
                  if (our == null) {
                     our = new ArrayList<>();
                     this.makeCharInfo.put(keyx, our);
                  }

                  for (MapleData dd : da) {
                     if (dd.getName().equalsIgnoreCase("color")) {
                        for (MapleData dda : dd) {
                           for (MapleData ddd : dda) {
                              our.add(MapleDataTool.getInt(ddd, -1));
                           }
                        }
                     } else {
                        try {
                           our.add(MapleDataTool.getInt(dd, -1));
                        } catch (Exception var22) {
                           for (MapleData dda : dd) {
                              for (MapleData ddd : dda) {
                                 our.add(MapleDataTool.getInt(ddd, -1));
                              }
                           }
                        }
                     }
                  }
               }
            }
         } catch (NullPointerException | NumberFormatException var23) {
         }
      }

      for (MapleData dat : infoData.getChildByPath("UltimateAdventurer")) {
         Triple<Integer, String, Integer> key = new Triple<>(
            -1, MapleDataTool.getString(dat.getChildByPath("name"), "궁모"), LoginInformationProvider.JobType.UltimateAdventurer.type
         );
         List<Integer> our = this.makeCharInfo.get(key);
         if (our == null) {
            our = new ArrayList<>();
            this.makeCharInfo.put(key, our);
         }

         for (MapleData dx : dat) {
            our.add(MapleDataTool.getInt(dx, -1));
         }
      }

      if (DBConfig.isGanglim) {
         this.ForbiddenName.add("제니아");
         this.ForbiddenName.add("강림");
         this.ForbiddenName.add("강림메이플");
         this.ForbiddenName.add("Royal");
         this.ForbiddenName.add("Royalmaple");
      }
   }

   public static boolean isExtendedSpJob(int jobId) {
      return GameConstants.isSeparatedSp(jobId);
   }

   public final boolean isForbiddenName(String in) {
      for (String name : this.ForbiddenName) {
         if (in.toLowerCase().contains(name.toLowerCase())) {
            return true;
         }
      }

      for (String namex : new ArrayList<>(DummyCharacterName.dummyList)) {
         if (namex.equals(in)) {
            return true;
         }
      }

      return false;
   }

   public final boolean isEligibleItem(int gender, String val, int job, int item) {
      if (item < 0) {
         return false;
      } else {
         Triple<Integer, String, Integer> key = new Triple<>(gender, val, job);
         List<Integer> our = this.makeCharInfo.get(key);
         return our == null ? false : our.contains(item);
      }
   }

   public static enum JobType {
      UltimateAdventurer(-1, 0, 100000000, false, false, true, false),
      Resistance(0, 3000, 931000000, false, false, false, false),
      Adventurer(1, 0, 4000011, false, false, false, false),
      Cygnus(2, 1000, 130030000, false, false, false, true),
      Aran(3, 2000, 914000000, false, false, true, false),
      Evan(4, 2001, 900010000, false, false, true, false),
      Mercedes(5, 2002, 910150000, false, false, false, false),
      Demon(6, 3001, 931050310, true, false, false, false),
      Phantom(7, 2003, 915000000, false, false, false, true),
      DualBlade(8, 0, 103050900, false, false, false, false),
      Mihile(9, 5000, 913070000, false, false, true, false),
      Luminous(10, 2004, 101000000, false, false, false, true),
      Kaiser(11, 6000, 0, false, false, false, false),
      AngelicBuster(12, 6001, 940011000, false, false, false, false),
      Cannoneer(13, 0, 0, false, false, true, false),
      Xenon(14, 3002, 931060089, true, false, false, false),
      Zero(15, 10112, 100000000, false, false, false, true),
      EunWol(16, 2005, 552000050, false, false, true, true),
      PinkBean(17, 13100, 100000000, false, false, false, false),
      Kinesis(18, 14000, 331001110, false, false, false, false),
      Kadena(19, 6002, 940200500, false, false, false, false),
      Illium(20, 15000, 0, false, false, false, false),
      Ark(21, 15001, 402090000, true, false, false, false),
      PathFinder(22, 0, 0, false, true, false, false),
      Hoyoung(23, 16000, 0, true, false, false, true),
      Adele(24, 15002, 0, false, false, false, false),
      Kain(25, 6003, 0, false, false, false, false),
      Yeti(26, 13500, 100000000, false, false, false, false),
      Lara(27, 16001, 100000000, false, false, false, false),
      Khali(28, 15003, 100000000, false, true, false, false);

      public int type;
      public int id;
      public int map;
      public boolean hairColor;
      public boolean skinColor;
      public boolean faceMark;
      public boolean hat;
      public boolean bottom;
      public boolean cape;

      private JobType(int type, int id, int map, boolean faceMark, boolean hat, boolean bottom, boolean cape) {
         this.type = type;
         this.id = id;
         this.map = ServerConstants.StartMap;
         this.faceMark = faceMark;
         this.hat = hat;
         this.bottom = bottom;
         this.cape = cape;
      }

      public static LoginInformationProvider.JobType getByType(int g) {
         if (g == Cannoneer.type) {
            return Adventurer;
         } else {
            for (LoginInformationProvider.JobType e : values()) {
               if (e.type == g) {
                  return e;
               }
            }

            return null;
         }
      }

      public static LoginInformationProvider.JobType getById(int g) {
         if (g == Adventurer.id) {
            return Adventurer;
         } else {
            for (LoginInformationProvider.JobType e : values()) {
               if (e.id == g) {
                  return e;
               }
            }

            return null;
         }
      }
   }
}
