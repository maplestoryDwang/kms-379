package objects.fields.child.karrotte.guardian;

public enum GuardianType {
   EyeOfRedemption(2),
   EyeOfTheAbyss(3),
   FighterPlane(4),
   SphereOfOdium(5);

   private int type;

   private GuardianType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
