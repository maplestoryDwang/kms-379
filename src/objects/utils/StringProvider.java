package objects.utils;

import java.util.HashMap;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class StringProvider {
   static final MapleData mobName = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("String.wz")).getData("Mob.img");
   static final MapleData npcName = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("String.wz")).getData("Npc.img");
   static final Map<Integer, String> npcNamesDics = new HashMap<>();
   static final Map<Integer, String> mobNamesDics = new HashMap<>();

   public static void load() {
      int i = 0;

      for (MapleData data : mobName) {
         String name = MapleDataTool.getString(data.getChildByPath("name"), "");
         if (!name.isEmpty()) {
            mobNamesDics.put(i++, StringUtil.replaceNonHangul(name));
         }
      }

      System.out.println("총 " + i + "개의 한글 단어 몬스터 이름이 캐싱되었습니다.");
      i = 0;

      for (MapleData datax : npcName) {
         String name = MapleDataTool.getString(datax.getChildByPath("name"), "");
         if (!name.isEmpty()) {
            npcNamesDics.put(i++, StringUtil.replaceNonHangul(name));
         }
      }

      System.out.println("총 " + i + "개의 한글 단어 엔피씨 이름이 캐싱되었습니다.");
   }

   public static String getRandomNpcWord() {
      return npcNamesDics.get(Randomizer.nextInt(npcNamesDics.size()));
   }

   public static String getRandomMobWord() {
      return mobNamesDics.get(Randomizer.nextInt(mobNamesDics.size()));
   }
}
