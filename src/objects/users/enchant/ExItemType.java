package objects.users.enchant;

import objects.utils.Randomizer;

public enum ExItemType {
   Str(0),
   Dex(1),
   Int(2),
   Luk(3),
   StrDex(4),
   StrInt(5),
   StrLuk(6),
   DexInt(7),
   DexLuk(8),
   IntLuk(9),
   MaxHP(10),
   MaxMP(11),
   ReqLevel(12),
   Pdd(13),
   Mdd(14),
   Acc(15),
   Eva(16),
   Pad(17),
   Mad(18),
   Speed(19),
   Jump(20),
   BdR(21),
   IMdR(22),
   DamR(23),
   StatR(24);

   private int type;

   private ExItemType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static ExItemType getRandom() {
      while (true) {
         for (ExItemType type : values()) {
            if (Randomizer.nextInt(values().length) == type.getType()) {
               return type;
            }
         }
      }
   }

   public static ExItemType getItemType(int type_) {
      for (ExItemType type : values()) {
         if (type.getType() == type_) {
            return type;
         }
      }

      return null;
   }
}
