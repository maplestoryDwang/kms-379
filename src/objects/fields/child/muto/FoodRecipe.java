package objects.fields.child.muto;

public enum FoodRecipe {
   AWK_FIRES(0, new int[][]{{2435856, 5}, {2435860, 10}}),
   AH_FRIES(1, new int[][]{{2435858, 5}, {2435862, 10}}),
   UH_OH_NOODLES(2, new int[][]{{2435856, 5}, {2435860, 5}, {2435864, 10}}),
   OH_OH_POT_ROAST(3, new int[][]{{2435858, 5}, {2435862, 5}, {2435866, 10}}),
   HAHA_ROLL(4, new int[][]{{2435860, 5}, {2435864, 5}, {2435868, 10}}),
   HOHO_SOUP(5, new int[][]{{2435864, 5}, {2435866, 5}, {2435870, 10}}),
   HAK_BROCHETTE(6, new int[][]{{2435872, 1}, {2435858, 5}, {2435862, 5}, {2435868, 10}}),
   ARGH_SALAD(7, new int[][]{{2435872, 1}, {2435860, 5}, {2435866, 5}, {2435870, 10}}),
   BAHAHA_FRIED_RICE(8, new int[][]{{2435857, 5}, {2435861, 10}}),
   TEEHEE_DUMPLING(9, new int[][]{{2435859, 5}, {2435863, 10}}),
   PHEW_PIZZA(10, new int[][]{{2435857, 5}, {2435861, 5}, {2435865, 10}}),
   HA_BREAD(11, new int[][]{{2435859, 5}, {2435863, 5}, {2435867, 10}}),
   ERR_PICKLE(12, new int[][]{{2435861, 5}, {2435865, 5}, {2435869, 10}}),
   KK_PORRIDGE(13, new int[][]{{2435865, 5}, {2435867, 5}, {2435871, 10}}),
   SOB_PUNCH(14, new int[][]{{2435872, 1}, {2435859, 5}, {2435863, 5}, {2435869, 10}}),
   WEEP_SAUSAGE(15, new int[][]{{2435872, 1}, {2435861, 5}, {2435867, 5}, {2435871, 10}});

   int type;
   int[][] recipes;

   private FoodRecipe(int type, int[][] recipes) {
      this.type = type;
      this.recipes = recipes;
   }

   public int[][] getRecipe() {
      return this.recipes;
   }

   public int getType() {
      return this.type;
   }

   public static int[][] getRecipe(int type) {
      for (FoodRecipe fr : values()) {
         if (fr.getType() == type) {
            return fr.getRecipe();
         }
      }

      return null;
   }
}
