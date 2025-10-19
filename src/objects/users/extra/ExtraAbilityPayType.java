package objects.users.extra;

public enum ExtraAbilityPayType {
   Meso(0),
   Promotion(1),
   Donation(2);

   private int type;

   private ExtraAbilityPayType(int type) {
      this.type = type;
   }

   public ExtraAbilityPayType getGrade() {
      for (ExtraAbilityPayType g : values()) {
         if (g.type == this.type) {
            return g;
         }
      }

      return null;
   }

   public ExtraAbilityPayType getGrade(int type) {
      for (ExtraAbilityPayType g : values()) {
         if (g.type == type) {
            return g;
         }
      }

      return null;
   }
}
