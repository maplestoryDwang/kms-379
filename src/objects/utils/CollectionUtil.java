package objects.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import objects.fields.MapleMapObject;
import objects.fields.gameobject.lifes.MapleMonster;

public class CollectionUtil {
   private CollectionUtil() {
   }

   public static <T> List<T> copyFirst(List<T> list, int count) {
      List<T> ret = new ArrayList<>(list.size() < count ? list.size() : count);
      int i = 0;

      for (T elem : list) {
         ret.add(elem);
         if (i++ > count) {
            break;
         }
      }

      return ret;
   }

   public static void sortMonsterByBossHP(List<MapleMonster> mobs) {
      mobs.sort(new Comparator<MapleMonster>() {
         public int compare(MapleMonster mob1, MapleMonster mob2) {
            if (mob1.getStats() == null || mob2.getStats() == null) {
               if (mob1.getMobMaxHp() > mob2.getMobMaxHp()) {
                  return 1;
               } else {
                  return mob1.getMobMaxHp() == mob2.getMobMaxHp() ? 0 : -1;
               }
            } else if (mob1.getStats().isBoss() && !mob2.getStats().isBoss()) {
               return 1;
            } else if (!mob1.getStats().isBoss() && mob2.getStats().isBoss()) {
               return -1;
            } else if (mob1.getMobMaxHp() > mob2.getMobMaxHp()) {
               return 1;
            } else {
               return mob1.getMobMaxHp() == mob2.getMobMaxHp() ? 0 : -1;
            }
         }
      });
   }

   public static void sortMonsterByBossObjectHP(List<MapleMapObject> objects) {
      objects.sort(new Comparator<Object>() {
         @Override
         public int compare(Object o1, Object o2) {
            MapleMonster mob1 = (MapleMonster)o1;
            MapleMonster mob2 = (MapleMonster)o2;
            if (mob1.getStats() == null || mob2.getStats() == null) {
               if (mob1.getMobMaxHp() > mob2.getMobMaxHp()) {
                  return 1;
               } else {
                  return mob1.getMobMaxHp() == mob2.getMobMaxHp() ? 0 : -1;
               }
            } else if (mob1.getStats().isBoss() && !mob2.getStats().isBoss()) {
               return 1;
            } else if (!mob1.getStats().isBoss() && mob2.getStats().isBoss()) {
               return -1;
            } else if (mob1.getMobMaxHp() > mob2.getMobMaxHp()) {
               return 1;
            } else {
               return mob1.getMobMaxHp() == mob2.getMobMaxHp() ? 0 : -1;
            }
         }
      });
   }
}
