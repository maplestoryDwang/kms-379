package objects.utils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.stats.CTS;
import objects.users.stats.SecondaryStatFlag;

public class CFlagOperator {
   public static Map<Integer, SecondaryStatFlag> buffStats = new HashMap<>();
   public static Map<Integer, MobTemporaryStatFlag> mobStats = new HashMap<>();

   public static <E extends CTS> boolean hasSecondaryStat(Map<E, Integer> statups, int bit) {
      int result = 0;
      int[] mask = new int[31];

      for (E statup : statups.keySet()) {
         if ((mask[statup.getPosition() - 1] & statup.getValue()) == 0L) {
            int var10001 = statup.getPosition() - 1;
            mask[var10001] = (int)(mask[var10001] | statup.getValue());
         }
      }

      if (bit < 992) {
         int position = 31 - bit / 32 - 1;
         int bitPos = 31 - bit % 32;
         result = mask[position] >> bitPos & 1;
         if (result > 0) {
         }
      }

      return result > 0;
   }

   public static boolean hasSecondaryStat(SecondaryStatFlag statup, int bit) {
      int result = 0;
      int[] mask = new int[31];
      if ((mask[statup.getPosition() - 1] & statup.getValue()) == 0L) {
         int var10001 = statup.getPosition() - 1;
         mask[var10001] = (int)(mask[var10001] | statup.getValue());
      }

      if (bit < 992) {
         int position = 30 - bit / 32;
         int bitPos = 31 - bit % 32;
         result = mask[position] >> bitPos & 1;
      }

      return result > 0;
   }

   public static <E extends CTS> SecondaryStatFlag getBuffStat(Map<E, Integer> statups, int bit) {
      if (hasSecondaryStat(statups, bit)) {
         SecondaryStatFlag flag = buffStats.get(bit);
         if (flag != null) {
            return flag;
         }

         Map<SecondaryStatFlag, Integer> stats = new EnumMap<>(SecondaryStatFlag.class);

         for (SecondaryStatFlag stat : SecondaryStatFlag.values()) {
            stats.clear();
            stats.put(stat, 1);
            if (hasSecondaryStat(stats, bit)) {
               buffStats.put(bit, stat);
               return stat;
            }
         }
      }

      return null;
   }

   public static <E extends CTS> Map<MobTemporaryStatFlag, MobTemporaryStatEffect> hasMobTemporaryStat(
      Map<E, MobTemporaryStatEffect> mobStats, MobTemporaryStatFlag flag
   ) {
      Map<MobTemporaryStatFlag, MobTemporaryStatEffect> ret = new HashMap<>();

      for (Entry<E, MobTemporaryStatEffect> status : mobStats.entrySet()) {
         if (flag.getBit() == status.getKey().getBit()) {
            ret.put((MobTemporaryStatFlag)status.getKey(), status.getValue());
            return ret;
         }
      }

      return null;
   }

   public static <E extends CTS> boolean hasMobTemporaryStat(Map<E, MobTemporaryStatEffect> statups, int bit) {
      int result = 0;
      int[] mask = new int[4];

      for (E statup : statups.keySet()) {
         if ((mask[statup.getPosition() - 1] & statup.getValue()) == 0L) {
            int var10001 = statup.getPosition() - 1;
            mask[var10001] = (int)(mask[var10001] | statup.getValue());
         }
      }

      if (bit < 128) {
         int position = 4 - bit / 32 - 1;
         int bitPos = 31 - bit % 32;
         result = mask[position] >> bitPos & 1;
         if (result > 0) {
         }
      }

      return result > 0;
   }

   public static boolean hasMobTemporaryStat(MobTemporaryStatFlag statup, int bit) {
      int result = 0;
      int[] mask = new int[4];
      if ((mask[statup.getPosition() - 1] & statup.getValue()) == 0L) {
         int var10001 = statup.getPosition() - 1;
         mask[var10001] = (int)(mask[var10001] | statup.getValue());
      }

      if (bit < 128) {
         int position = 3 - bit / 32;
         int bitPos = 31 - bit % 32;
         result = mask[position] >> bitPos & 1;
      }

      return result > 0;
   }

   public static <E extends CTS> MobTemporaryStatFlag getMobBuffStat(Map<E, MobTemporaryStatEffect> statups, int bit) {
      if (hasMobTemporaryStat(statups, bit)) {
         MobTemporaryStatFlag flag = mobStats.get(bit);
         if (flag != null) {
            return flag;
         }

         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new EnumMap<>(MobTemporaryStatFlag.class);

         for (MobTemporaryStatFlag stat : MobTemporaryStatFlag.values()) {
            stats.clear();
            stats.put(stat, null);
            if (hasMobTemporaryStat(stats, bit)) {
               mobStats.put(bit, stat);
               return stat;
            }
         }
      }

      return null;
   }

   public static void main(String[] args) {
      EnumMap<SecondaryStatFlag, Integer> statups = new EnumMap<>(SecondaryStatFlag.class);

      for (SecondaryStatFlag stat : SecondaryStatFlag.values()) {
         statups.put(stat, 1);
      }

      for (int i = 0; i < 544; i++) {
         boolean has = hasSecondaryStat(statups, i);
         if (has) {
            getBuffStat(statups, i);
         }
      }
   }
}
