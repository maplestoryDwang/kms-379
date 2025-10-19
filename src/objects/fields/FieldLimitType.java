package objects.fields;

public enum FieldLimitType {
   Jump(1),
   MovementSkills(2),
   SummoningBag(4),
   MysticDoor(8),
   ChannelSwitch(16),
   RegularExpLoss(32),
   VipRock(64),
   Minigames(128),
   SpecificPortalScrollLimit(256),
   Mount(512),
   PotionUse(2048),
   Event(8192),
   Pet(32768),
   Event2(65536),
   DropDown(131072),
   NO_EXP_DECREASE(524288);

   private final int i;

   private FieldLimitType(int i) {
      this.i = i;
   }

   public final int getValue() {
      return this.i;
   }

   public final boolean check(int fieldlimit) {
      return (fieldlimit & this.i) == this.i;
   }
}
