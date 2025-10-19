package objects.users.stone;

public enum ImprintedStonePayType {
   Donation(0),
   Promotion(1),
   Meso(2),
   RoyalCash(4),
   RoyalRedBall(5),
   RoyalDPoint(6);

   private int type;

   private ImprintedStonePayType(int type) {
      this.type = type;
   }

   public ImprintedStonePayType getType() {
      for (ImprintedStonePayType g : values()) {
         if (g.type == this.type) {
            return g;
         }
      }

      return null;
   }

   public static ImprintedStonePayType getType(int type) {
      for (ImprintedStonePayType g : values()) {
         if (g.type == type) {
            return g;
         }
      }

      return null;
   }
}
