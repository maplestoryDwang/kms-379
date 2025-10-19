package constants;

import database.DBConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class OfficialRandomOption {
   public static int chaosScroll() {
      int selected = 0;
      List<Pair<String, Double>> p = new ArrayList<>(
         Arrays.asList(
            new Pair<>("5", 0.99),
            new Pair<>("4", 1.98),
            new Pair<>("3", 10.21),
            new Pair<>("2", 15.87),
            new Pair<>("1", 19.31),
            new Pair<>("0", 18.38),
            new Pair<>("-1", 13.7),
            new Pair<>("-2", 8.0),
            new Pair<>("-3", 3.65),
            new Pair<>("-4", 2.97),
            new Pair<>("-5", 4.94)
         )
      );
      int[] pe = new int[10000];
      int g = (int)(p.get(0).getRight() * 100.0);

      for (int i = 0; i < 10000; i++) {
         pe[i] = Integer.parseInt(p.get(selected).getLeft());
         if (g < i) {
            g += (int)(p.get(++selected).getRight() * 100.0);
         }
      }

      Collections.shuffle(Arrays.asList(pe));
      return pe[Randomizer.nextInt(10000)];
   }

   public static int incredibleChaosScroll() {
      int selected = 0;
      List<Pair<String, Double>> p = new ArrayList<>(
         Arrays.asList(
            new Pair<>("6", 0.99),
            new Pair<>("4", 1.98),
            new Pair<>("3", 10.21),
            new Pair<>("2", 15.87),
            new Pair<>("1", 19.31),
            new Pair<>("0", 18.38),
            new Pair<>("-1", 13.7),
            new Pair<>("-2", 8.0),
            new Pair<>("-3", 3.65),
            new Pair<>("-4", 2.97),
            new Pair<>("-6", 4.94)
         )
      );
      int[] pe = new int[10000];
      int g = (int)(p.get(0).getRight() * 100.0);

      for (int i = 0; i < 10000; i++) {
         pe[i] = Integer.parseInt(p.get(selected).getLeft());
         if (g < i) {
            g += (int)(p.get(++selected).getRight() * 100.0);
         }
      }

      Collections.shuffle(Arrays.asList(pe));
      return pe[Randomizer.nextInt(10000)];
   }

   public static int chaosScrollOfGoodNess() {
      int selected = 0;
      List<Pair<String, Double>> p = new ArrayList<>(
         Arrays.asList(
            new Pair<>("5", 5.93), new Pair<>("4", 4.94), new Pair<>("3", 13.87), new Pair<>("2", 23.87), new Pair<>("1", 33.01), new Pair<>("0", 18.38)
         )
      );
      int[] pe = new int[10000];
      int g = (int)(p.get(0).getRight() * 100.0);

      for (int i = 0; i < 10000; i++) {
         pe[i] = Integer.parseInt(p.get(selected).getLeft());
         if (g < i) {
            g += (int)(p.get(++selected).getRight() * 100.0);
         }
      }

      Collections.shuffle(Arrays.asList(pe));
      return pe[Randomizer.nextInt(10000)];
   }

   public static int incredibleChaosScrollOfGoodNess() {
      int selected = 0;
      List<Pair<String, Double>> p = null;
      if (DBConfig.isGanglim) {
         return Randomizer.rand(4, 6);
      } else {
         List<Pair<String, Double>> var5 = new ArrayList<>(
            Arrays.asList(
               new Pair<>("6", 5.93), new Pair<>("4", 4.94), new Pair<>("3", 13.87), new Pair<>("2", 23.87), new Pair<>("1", 33.01), new Pair<>("0", 18.38)
            )
         );
         int[] pe = new int[10000];
         int g = (int)((Double)((Pair<String, Double>)var5.get(0)).getRight() * 100.0);

         for (int i = 0; i < 10000; i++) {
            pe[i] = Integer.parseInt((String)((Pair<String, Double>)var5.get(selected)).getLeft());
            if (g < i) {
               g += (int)(((Pair<String, Double>)var5.get(++selected)).getRight() * 100.0);
            }
         }

         Collections.shuffle(Arrays.asList(pe));
         return pe[Randomizer.nextInt(10000)];
      }
   }
}
