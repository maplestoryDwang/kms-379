package objects.item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.utils.Pair;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class ItemMakerFactory {
   private static final ItemMakerFactory instance = new ItemMakerFactory();
   protected Map<Integer, ItemMakerFactory.ItemMakerCreateEntry> createCache = new HashMap<>();
   protected Map<Integer, ItemMakerFactory.GemCreateEntry> gemCache = new HashMap<>();

   public static ItemMakerFactory getInstance() {
      return instance;
   }

   protected ItemMakerFactory() {
      MapleData info = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz")).getData("ItemMake.img");

      for (MapleData dataType : info.getChildren()) {
         int type = Integer.parseInt(dataType.getName());
         switch (type) {
            case 0:
               for (MapleData itemFolder : dataType.getChildren()) {
                  int reqLevel = MapleDataTool.getInt("reqLevel", itemFolder, 0);
                  byte reqMakerLevel = (byte)MapleDataTool.getInt("reqSkillLevel", itemFolder, 0);
                  int cost = MapleDataTool.getInt("meso", itemFolder, 0);
                  int quantity = MapleDataTool.getInt("itemNum", itemFolder, 0);
                  ItemMakerFactory.GemCreateEntry ret = new ItemMakerFactory.GemCreateEntry(cost, reqLevel, reqMakerLevel, quantity);

                  for (MapleData rewardNRecipe : itemFolder.getChildren()) {
                     for (MapleData ind : rewardNRecipe.getChildren()) {
                        if (rewardNRecipe.getName().equals("randomReward")) {
                           ret.addRandomReward(MapleDataTool.getInt("item", ind, 0), MapleDataTool.getInt("prob", ind, 0));
                        } else if (rewardNRecipe.getName().equals("recipe")) {
                           ret.addReqRecipe(MapleDataTool.getInt("item", ind, 0), MapleDataTool.getInt("count", ind, 0));
                        }
                     }
                  }

                  this.gemCache.put(Integer.parseInt(itemFolder.getName()), ret);
               }
               break;
            case 1:
            case 2:
            case 4:
            case 8:
            case 16:
               for (MapleData itemFolder : dataType.getChildren()) {
                  int reqLevel = MapleDataTool.getInt("reqLevel", itemFolder, 0);
                  byte reqMakerLevel = (byte)MapleDataTool.getInt("reqSkillLevel", itemFolder, 0);
                  int cost = MapleDataTool.getInt("meso", itemFolder, 0);
                  int quantity = MapleDataTool.getInt("itemNum", itemFolder, 0);
                  byte totalupgrades = (byte)MapleDataTool.getInt("tuc", itemFolder, 0);
                  int stimulator = MapleDataTool.getInt("catalyst", itemFolder, 0);
                  ItemMakerFactory.ItemMakerCreateEntry imt = new ItemMakerFactory.ItemMakerCreateEntry(
                     cost, reqLevel, reqMakerLevel, quantity, totalupgrades, stimulator
                  );

                  for (MapleData Recipe : itemFolder.getChildren()) {
                     for (MapleData indx : Recipe.getChildren()) {
                        if (Recipe.getName().equals("recipe")) {
                           imt.addReqItem(MapleDataTool.getInt("item", indx, 0), MapleDataTool.getInt("count", indx, 0));
                        }
                     }
                  }

                  this.createCache.put(Integer.parseInt(itemFolder.getName()), imt);
               }
            case 3:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
         }
      }
   }

   public ItemMakerFactory.GemCreateEntry getGemInfo(int itemid) {
      return this.gemCache.get(itemid);
   }

   public ItemMakerFactory.ItemMakerCreateEntry getCreateInfo(int itemid) {
      return this.createCache.get(itemid);
   }

   public static class GemCreateEntry {
      private int reqLevel;
      private int reqMakerLevel;
      private int cost;
      private int quantity;
      private List<Pair<Integer, Integer>> randomReward = new ArrayList<>();
      private List<Pair<Integer, Integer>> reqRecipe = new ArrayList<>();

      public GemCreateEntry(int cost, int reqLevel, int reqMakerLevel, int quantity) {
         this.cost = cost;
         this.reqLevel = reqLevel;
         this.reqMakerLevel = reqMakerLevel;
         this.quantity = quantity;
      }

      public int getRewardAmount() {
         return this.quantity;
      }

      public List<Pair<Integer, Integer>> getRandomReward() {
         return this.randomReward;
      }

      public List<Pair<Integer, Integer>> getReqRecipes() {
         return this.reqRecipe;
      }

      public int getReqLevel() {
         return this.reqLevel;
      }

      public int getReqSkillLevel() {
         return this.reqMakerLevel;
      }

      public int getCost() {
         return this.cost;
      }

      protected void addRandomReward(int itemId, int prob) {
         this.randomReward.add(new Pair<>(itemId, prob));
      }

      protected void addReqRecipe(int itemId, int count) {
         this.reqRecipe.add(new Pair<>(itemId, count));
      }
   }

   public static class ItemMakerCreateEntry {
      private int reqLevel;
      private int cost;
      private int quantity;
      private int stimulator;
      private byte tuc;
      private byte reqMakerLevel;
      private List<Pair<Integer, Integer>> reqItems = new ArrayList<>();
      private List<Integer> reqEquips = new ArrayList<>();

      public ItemMakerCreateEntry(int cost, int reqLevel, byte reqMakerLevel, int quantity, byte tuc, int stimulator) {
         this.cost = cost;
         this.tuc = tuc;
         this.reqLevel = reqLevel;
         this.reqMakerLevel = reqMakerLevel;
         this.quantity = quantity;
         this.stimulator = stimulator;
      }

      public byte getTUC() {
         return this.tuc;
      }

      public int getRewardAmount() {
         return this.quantity;
      }

      public List<Pair<Integer, Integer>> getReqItems() {
         return this.reqItems;
      }

      public List<Integer> getReqEquips() {
         return this.reqEquips;
      }

      public int getReqLevel() {
         return this.reqLevel;
      }

      public byte getReqSkillLevel() {
         return this.reqMakerLevel;
      }

      public int getCost() {
         return this.cost;
      }

      public int getStimulator() {
         return this.stimulator;
      }

      protected void addReqItem(int itemId, int amount) {
         this.reqItems.add(new Pair<>(itemId, amount));
      }
   }
}
