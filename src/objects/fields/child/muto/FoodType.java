package objects.fields.child.muto;

public enum FoodType {
   AWK_FRIES(0, 4034959),
   AH_FRIES(1, 4034960),
   UH_OH_NOODLES(2, 4034961),
   OH_OH_POT_ROAST(3, 4034962),
   HAHA_ROLL(4, 4034963),
   HOHO_SOUP(5, 4034964),
   GAK_BROCHETTE(6, 4034965),
   ARGH_SALAD(7, 4034966),
   BAHAHA_FRIED_RICE(8, 4034967),
   TEEHEE_DUMPLING(9, 4034968),
   PHEW_PIZAA(10, 4034969),
   HA_BREAD(11, 4034970),
   ERR_PICKLE(12, 4034971),
   KK_PORRIDGE(13, 4034972),
   SOB_PUNCH(14, 4034973),
   WEEP_SAUSAGE(15, 4034974);

   private int type;
   private int itemID;

   private FoodType(int type, int itemID) {
      this.type = type;
      this.itemID = itemID;
   }

   public int getType() {
      return this.type;
   }

   public int getItemID() {
      return this.itemID;
   }

   public static FoodType getFoodType(int type) {
      for (FoodType f : values()) {
         if (f.getType() == type) {
            return f;
         }
      }

      return null;
   }
}
