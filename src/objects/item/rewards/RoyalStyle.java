package objects.item.rewards;

import database.DBConfig;
import database.DBConnection;
import database.DBEventManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.utils.Properties;
import objects.utils.Randomizer;
import objects.utils.Table;

public class RoyalStyle {
   public static Map<Integer, int[][]> specialBerry = new HashMap<>();
   public static int[][] blackBerry = new int[][]{
      {2438145, 1},
      {2046076, 1},
      {2046077, 1},
      {2046150, 1},
      {2046340, 1},
      {2046341, 1},
      {2048047, 1},
      {2048048, 1},
      {4310266, 200},
      {4310266, 250},
      {4310266, 300},
      {4310237, 900},
      {4310229, 500},
      {4310229, 300},
      {4310229, 200},
      {4310229, 500},
      {4310229, 500},
      {4310229, 500},
      {4310237, 700},
      {4310237, 1000},
      {4031227, 200},
      {4031227, 300},
      {4031227, 100},
      {2430041, 1},
      {2430042, 1},
      {2430043, 1},
      {2430044, 1},
      {5062005, 2},
      {5062005, 3},
      {5062005, 5},
      {2046991, 10},
      {2046992, 10},
      {2047814, 10},
      {2048753, 30},
      {2630127, 1},
      {2630127, 1},
      {2431940, 4},
      {2048753, 20},
      {2048753, 30},
      {4001716, 10},
      {4001716, 4},
      {4001716, 5},
      {4009005, 200},
      {4021031, 1500},
      {4021031, 2000},
      {4001716, 10}
   };
   public static int[][] rainbowBerry = new int[][]{
      {4031227, 2000}, {2049376, 1}, {2049377, 1}, {2049380, 1}, {2430044, 1}, {2430045, 1}, {2430046, 1}, {2430047, 1}
   };
   public static RandomRewards[] wonderBerry = new RandomRewards[]{
      new RandomRewards(5068300, 1, 5002200, 0, true),
      new RandomRewards(5068300, 1, 5002201, 0, true),
      new RandomRewards(5068300, 1, 5002202, 0, true),
      new RandomRewards(5068300, 1, 5000966, 0, false),
      new RandomRewards(5068300, 1, 5000967, 0, false),
      new RandomRewards(5068300, 1, 5000968, 0, false),
      new RandomRewards(5068300, 1, 5002203, 0, false),
      new RandomRewards(5068300, 1, 5002204, 0, false)
   };
   public static RandomRewards[] lunaCrystal_Jin = new RandomRewards[]{
      new RandomRewards(5069100, 255, 1802653, 45, true),
      new RandomRewards(5069100, 255, 1802699, 45, true),
      new RandomRewards(5069100, 386, 5002561, 90, true),
      new RandomRewards(5069100, 386, 5002562, 90, true),
      new RandomRewards(5069100, 386, 5002563, 90, true),
      new RandomRewards(5069100, 960, 5002200, 300, false),
      new RandomRewards(5069100, 960, 5002201, 300, false),
      new RandomRewards(5069100, 960, 5002202, 300, false),
      new RandomRewards(5069100, 960, 5002137, 300, false),
      new RandomRewards(5069100, 960, 5002138, 300, false),
      new RandomRewards(5069100, 960, 5002139, 300, false),
      new RandomRewards(5069100, 960, 5002161, 300, false),
      new RandomRewards(5069100, 960, 5002162, 300, false),
      new RandomRewards(5069100, 960, 5002163, 300, false)
   };
   public static RandomRewards[] lunaCrystal_Royal = new RandomRewards[]{
      new RandomRewards(5069100, 488, 5002561, 90, true),
      new RandomRewards(5069100, 488, 5002562, 90, true),
      new RandomRewards(5069100, 488, 5002563, 90, true),
      new RandomRewards(5069100, 960, 5002200, 300, false),
      new RandomRewards(5069100, 960, 5002201, 300, false),
      new RandomRewards(5069100, 960, 5002202, 300, false),
      new RandomRewards(5069100, 960, 5002137, 300, false),
      new RandomRewards(5069100, 960, 5002138, 300, false),
      new RandomRewards(5069100, 960, 5002139, 300, false),
      new RandomRewards(5069100, 960, 5002161, 300, false),
      new RandomRewards(5069100, 960, 5002162, 300, false),
      new RandomRewards(5069100, 960, 5002163, 300, false)
   };
   public static RandomRewards[] goldApple = new RandomRewards[0];

   public static void resetGoldApple() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "GoldApple.data");
      List<RandomRewards> randomRewards = new ArrayList<>();

      for (Table child : table.list()) {
         int itemID = Integer.parseInt(child.getProperty("itemID"));
         int weight = Integer.parseInt(child.getProperty("weight"));
         int maleItemID = Integer.parseInt(child.getProperty("maleItemID"));
         int bonusWeight = Integer.parseInt(child.getProperty("bonusWeight"));
         boolean announce = child.getProperty("announce", false);
         randomRewards.add(new RandomRewards(itemID, weight, maleItemID, bonusWeight, announce));
      }

      goldApple = randomRewards.toArray(RandomRewards[]::new);
   }

   public static RandomRewards getRandomItem(RandomRewards[] rewards) {
      int maxWeight = 0;
      RandomRewards[] newReward = Arrays.copyOf(rewards, rewards.length);
      List<RandomRewards> list = Arrays.asList(newReward);
      Collections.shuffle(list);

      for (RandomRewards r : list) {
         maxWeight += r.getWeight();
      }

      int rand = Randomizer.rand(1, maxWeight);
      int v = 0;

      for (RandomRewards r : list) {
         v += r.getWeight();
         if (rand <= v) {
            return r;
         }
      }

      return null;
   }

   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(3);
      int[] a = new int[goldApple.length];

      for (int i = 0; i < 10000; i++) {
         RandomRewards reward = getRandomItem(goldApple);
         int index = 0;

         for (RandomRewards r : goldApple) {
            if (r.getMaleItemID() == reward.getMaleItemID()) {
               a[index]++;
               break;
            }

            index++;
         }
      }

      for (int i = 0; i < a.length; i++) {
         System.out.println(i + " " + a[i]);
      }
   }

   static {
      specialBerry.put(5068305, blackBerry);
      specialBerry.put(5068306, rainbowBerry);
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "GoldApple.data");
      List<RandomRewards> randomRewards = new ArrayList<>();

      for (Table child : table.list()) {
         int itemID = Integer.parseInt(child.getProperty("itemID"));
         int weight = Integer.parseInt(child.getProperty("weight"));
         int maleItemID = Integer.parseInt(child.getProperty("maleItemID"));
         int bonusWeight = Integer.parseInt(child.getProperty("bonusWeight"));
         boolean announce = child.getProperty("announce", false);
         int itemLength = 1;
         if (DBConfig.isGanglim) {
            itemLength = Integer.parseInt(child.getProperty("itemLength"));
         }

         randomRewards.add(new RandomRewards(itemID, weight, maleItemID, maleItemID, bonusWeight, itemLength, announce));
      }

      goldApple = randomRewards.toArray(RandomRewards[]::new);
   }
}
