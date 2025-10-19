package objects.fields;

public abstract class AnimatedMapleMapObject extends MapleMapObject {
   private int stance;

   public int getStance() {
      return this.stance;
   }

   public void setStance(int stance) {
      this.stance = stance;
   }

   public boolean isFacingLeft() {
      return this.getStance() % 2 != 0;
   }

   public int getFacingDirection() {
      return Math.abs(this.getStance() % 2);
   }
}
