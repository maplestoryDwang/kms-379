package objects.fields;

import java.util.concurrent.ConcurrentHashMap;
import objects.fields.gameobject.lifes.MapleNPC;

public class GameObjects {
   private static final ConcurrentHashMap<Integer, MapleNPC> objects = new ConcurrentHashMap<>();

   public static void add(int oid, MapleNPC npc) {
      objects.put(oid, npc);
   }

   public static MapleNPC get(int oid) {
      return objects.get(oid);
   }

   public static void remove(int oid) {
      objects.remove(oid);
   }
}
