package objects.fields.child.union;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MapleUnionConstants {
   public static final long UNIT_DAMAGE_COIN = 100000000000L;
   public static final long MAX_UNION_TOTAL_DAMAGE = 100000000000000L;

   public static long fromUnionDateString(String str) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
      long ret = 0L;

      try {
         ret = sdf.parse(str).getTime();
      } catch (ParseException var5) {
      }

      return ret;
   }

   public static String toUnionDate() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      return sdf.format(CAL.getTime());
   }

   public static long getBattleScore(MapleUnionEntry entry) {
      return (long)(getLevelBattleScore(entry) + getStarForceBattleScore(entry));
   }

   private static double getLevelBattleScore(MapleUnionEntry entry) {
      int level = entry.level;
      if (level >= 240) {
         return 1.25 * Math.pow(level, 3.0) + 12500.0;
      } else if (level >= 230) {
         return 1.2 * Math.pow(level, 3.0) + 12500.0;
      } else if (level >= 220) {
         return 1.15 * Math.pow(level, 3.0) + 12500.0;
      } else if (level >= 210) {
         return 1.1 * Math.pow(level, 3.0) + 12500.0;
      } else if (level >= 200) {
         return Math.pow(level, 3.0) + 12500.0;
      } else if (level >= 180) {
         return 0.8 * Math.pow(level, 3.0) + 12500.0;
      } else if (level >= 140) {
         return 0.7 * Math.pow(level, 3.0) + 12500.0;
      } else if (level >= 100) {
         return 0.4 * Math.pow(level, 3.0) + 12500.0;
      } else {
         return level < 60 ? 0.0 : 0.5 * Math.pow(level, 3.0) + 12500.0;
      }
   }

   public static int getNeedLevelUpCoin(int rank) {
      switch (rank) {
         case 101:
            return 0;
         case 102:
            return 120;
         case 103:
            return 140;
         case 104:
            return 150;
         case 105:
            return 160;
         case 201:
            return 170;
         case 202:
            return 430;
         case 203:
            return 450;
         case 204:
            return 470;
         case 205:
            return 490;
         case 301:
            return 510;
         case 302:
            return 930;
         case 303:
            return 960;
         case 304:
            return 1000;
         case 305:
            return 1030;
         case 401:
            return 1060;
         case 402:
            return 2200;
         case 403:
            return 2300;
         case 404:
            return 2350;
         case 405:
            return 2400;
         case 501:
            return 3000;
         case 502:
            return 3400;
         case 503:
            return 3900;
         case 504:
            return 4400;
         case 505:
            return 5000;
         default:
            return -1;
      }
   }

   public static int getNextRank(int currentRank) {
      int rank = currentRank / 100;
      int tier = currentRank % 100;
      if (rank >= 1 && rank <= 5) {
         int max = 5;
         if (tier >= 1 && tier <= max) {
            if (tier != max) {
               tier++;
            } else {
               rank++;
               tier = 1;
            }
         }
      }

      return rank * 100 + tier;
   }

   private static double getStarForceBattleScore(MapleUnionEntry union) {
      int starForce = union.starForce;
      if (starForce >= 350) {
         return 0.18 * Math.pow(starForce, 3.0) + 27.0 * Math.pow(starForce, 2.0) + 1350 * starForce + 10000.0;
      } else if (starForce >= 320) {
         return 0.17 * Math.pow(starForce, 3.0) + 25.5 * Math.pow(starForce, 2.0) + 1275 * starForce + 8750.0;
      } else if (starForce >= 290) {
         return 0.16 * Math.pow(starForce, 3.0) + 24.0 * Math.pow(starForce, 2.0) + 1200 * starForce + 7500.0;
      } else if (starForce >= 260) {
         return 0.15 * Math.pow(starForce, 3.0) + 22.5 * Math.pow(starForce, 2.0) + 1125 * starForce + 6250.0;
      } else if (starForce >= 230) {
         return 0.14 * Math.pow(starForce, 3.0) + 21.0 * Math.pow(starForce, 2.0) + 1050 * starForce + 5000.0;
      } else if (starForce >= 180) {
         return 0.13 * Math.pow(starForce, 3.0) + 19.5 * Math.pow(starForce, 2.0) + 975 * starForce + 37500.0;
      } else if (starForce >= 120) {
         return 0.12 * Math.pow(starForce, 3.0) + 18.0 * Math.pow(starForce, 2.0) + 900 * starForce + 25000.0;
      } else if (starForce >= 60) {
         return 0.11 * Math.pow(starForce, 3.0) + 16.5 * Math.pow(starForce, 2.0) + 825 * starForce + 1250.0;
      } else {
         return starForce < 0 ? 0.0 : 0.1 * Math.pow(starForce, 3.0) + 15.0 * Math.pow(starForce, 2.0) + 750 * starForce;
      }
   }
}
