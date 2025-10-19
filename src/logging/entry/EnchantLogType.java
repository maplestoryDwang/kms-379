package logging.entry;

public enum EnchantLogType {
   SelectChooseCube(0),
   InGameCube(1),
   GoldenHammer(2),
   FlagScroll(3),
   EquipScroll(4),
   SelectBlackFlame(5),
   AdditionalScroll(6),
   EquipEnchant(7),
   HyperUpgrade(8),
   TranscendenceEnchant(9),
   MesoEnchant(10),
   MesoEnchantReset(11),
   RedBeadEnchant(12),
   CashEnchant(13),
   ExtraAbility(14),
   HexaStat(15);

   private int type;

   private EnchantLogType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
