package objects.users.enchant;

public enum EnchantType {
   SCROLL_UPGRADE(0),
   HYPER_UPGRADE(1),
   TRANSMISSION(2),
   POTENTIAL_UPGRADE(3),
   ADDITIONAL_POTENTIAL_UPGRADE(4),
   EXTENDED_UPGRADE(5),
   SOUL_WEAPON_UPGRADE(6),
   NO_TYPES(7),
   DISPLAY_SCROLL_UPGRADE(50),
   DISPLAY_SCROLL_UPDATE(51),
   DISPLAY_HYPER_UPGRADE(52),
   DISPLAY_MINI_GAME(53),
   DISPLAY_SCROLL_UPGRADE_RESULT(100),
   DISPLAY_HYPER_UPGRADE_RESULT(101),
   DISPLAY_SCROLL_VESTIGE_COMPENSATION_RESULT(102),
   DISPLAY_TRANSMISSION_RESULT(103),
   DISPLAY_UNKNOWN_FAIL_RESULT(104),
   DISPLAY_BLOCK(105);

   final int type;

   private EnchantType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static EnchantType getEnchantType(int type) {
      for (EnchantType et : values()) {
         if (et.getType() == type) {
            return et;
         }
      }

      return null;
   }
}
