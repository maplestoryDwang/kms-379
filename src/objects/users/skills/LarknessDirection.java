package objects.users.skills;

public enum LarknessDirection {
   None(0),
   DarkToLight(1),
   LightToDark(2);

   final int direction;

   private LarknessDirection(int direction) {
      this.direction = direction;
   }

   public int getDirection() {
      return this.direction;
   }
}
