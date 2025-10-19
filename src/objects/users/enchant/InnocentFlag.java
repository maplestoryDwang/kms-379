package objects.users.enchant;

public enum InnocentFlag {
   NONE(0),
   HYPER(1),
   BONUS_STAT(2),
   TRADING(4);

   final int type;

   private InnocentFlag(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
