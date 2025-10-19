package objects.context.party.boss;

import database.DBConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class BossParty {
   public static final HashMap<Integer, List<BossParty>> cachedBossParty = new HashMap<>();
   String info;
   int levelMin;
   int difficulty;
   int enterFieldID;

   public static void cachingBossParty() {
      if (!DBConfig.DB_PASSWORD.equals("J2vs@efh6@K6!2")) {
         System.setProperty("net.sf.odinms.wzpath", "wz");
      }

      MapleData BossPartyWZ = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Etc.wz")).getData("BossParty.img");

      for (MapleData order : BossPartyWZ.getChildByPath("order")) {
         int key = Integer.parseInt(order.getName());
         int infoKey = MapleDataTool.getInt(order);
         MapleData infos = BossPartyWZ.getChildByPath(String.valueOf(infoKey));
         String name = MapleDataTool.getString(infos.getChildByPath("info"));
         List<BossParty> bossParties = new ArrayList<>();

         for (MapleData info : infos) {
            if (!info.getName().equals("info") && !info.getName().equals("mob")) {
               int levelMin = MapleDataTool.getInt(info.getChildByPath("levelMin"), 0);
               int difficulty = MapleDataTool.getInt(info.getChildByPath("difficulty"));
               int enterFieldID = MapleDataTool.getInt(info.getChildByPath("enterFieldID"));
               bossParties.add(new BossParty(name, levelMin, difficulty, enterFieldID));
            }
         }

         cachedBossParty.put(infoKey, bossParties);
      }

      System.out.println("[보스파티] " + cachedBossParty.size() + "개 캐싱완료");
   }

   public BossParty(String info, int levelMin, int difficulty, int enterFieldID) {
      this.info = info;
      this.levelMin = levelMin;
      this.difficulty = difficulty;
      this.enterFieldID = enterFieldID;
   }

   public int getDifficulty() {
      return this.difficulty;
   }

   public int getEnterFieldID() {
      return this.enterFieldID;
   }

   public int getLevelMin() {
      return this.levelMin;
   }

   public String getInfo() {
      return this.info;
   }
}
