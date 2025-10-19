package objects.users.enchant;

public enum EquipSpecialAttribute {
   NOT_DESTROY(1),
   GRADE_UPGRADE(2),
   ENCHANT_SUCCESS(4),
   EXTENDED(8),
   ONE_MESO(16),
   MAKING_SKILL_MEISTER(32),
   MAKING_SKILL_MASTER(64),
   VESTIGE(128);

   private int type;

   private EquipSpecialAttribute(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
