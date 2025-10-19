package objects.item;

public enum MapleWeaponType {
   NOT_A_WEAPON(1.43F, 20),
   BOW(1.2F, 15),
   CLAW(1.75F, 15),
   CANE(1.3F, 15),
   DAGGER(1.3F, 20),
   CROSSBOW(1.35F, 15),
   AXE1H(1.2F, 20),
   SWORD1H(1.2F, 20),
   BLUNT1H(1.2F, 20),
   AXE2H(1.32F, 20),
   SWORD2H(1.32F, 20),
   BLUNT2H(1.32F, 20),
   POLE_ARM(1.49F, 20),
   SPEAR(1.49F, 20),
   STAFF(1.0F, 25),
   WAND(1.0F, 25),
   KNUCKLE(1.7F, 20),
   GUN(1.5F, 15),
   DUAL_BOW(2.0F, 15),
   MAGIC_ARROW(2.0F, 15),
   KATARA(1.3F, 20),
   BIG_SWORD(1.3F, 15),
   LONG_SWORD(1.3F, 15),
   HANDCANNON(1.5F, 15),
   ENERGYSWORD(1.5F, 15),
   SWORD(1.56F, 20),
   PLANE(1.21F, 15),
   DESPERADO(1.23F, 20),
   SOULSHOOTER(1.22F, 15),
   TEDO(1.57F, 20),
   LIMITER(0.0F, 0),
   CHAIN(1.3F, 0),
   GUNTLIT(0.0F, 0),
   ANCIENT_BOW(0.0F, 0),
   MAGIC_GUNTLIT(0.0F, 0);

   private final float damageMultiplier;
   private final int baseMastery;

   private MapleWeaponType(float maxDamageMultiplier, int baseMastery) {
      this.damageMultiplier = maxDamageMultiplier;
      this.baseMastery = baseMastery;
   }

   public final float getMaxDamageMultiplier() {
      return this.damageMultiplier;
   }

   public final int getBaseMastery() {
      return this.baseMastery;
   }
}
