package objects.users.skills;

public enum AttackOption {
   FinalAttackAfterSlashBlast(1),
   SoulArrow(2),
   MortalBlow(4),
   ShadowPartner(8),
   SwitchAttack(16),
   ChainAttack(32),
   SpiritJavelin(64),
   Spark(128);

   private int type;

   private AttackOption(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static AttackOption getOption(int type) {
      for (AttackOption option : values()) {
         if (option.getType() == type) {
            return option;
         }
      }

      return null;
   }
}
