package objects.users.skills;

public enum AttackOption2 {
   UNK(1),
   Buckshot(2),
   ManaBurn(4),
   PassiveAddAttackCount(8);

   private int type;

   private AttackOption2(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static AttackOption2 getOption(int type) {
      for (AttackOption2 option : values()) {
         if (option.getType() == type) {
            return option;
         }
      }

      return null;
   }
}
