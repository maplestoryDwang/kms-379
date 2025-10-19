package objects.movepath;

import java.awt.Point;

public abstract class AbstractLifeMovement implements LifeMovement {
   private final Point position;
   private final int duration;
   private final int newstate;
   private final int type;
   private final short foodholds;

   public AbstractLifeMovement(int type, Point position, int duration, int newstate, short FH) {
      this.type = type;
      this.position = position;
      this.duration = duration;
      this.newstate = newstate;
      this.foodholds = FH;
   }

   @Override
   public int getType() {
      return this.type;
   }

   @Override
   public int getDuration() {
      return this.duration;
   }

   @Override
   public int getNewstate() {
      return this.newstate;
   }

   @Override
   public Point getPosition() {
      return this.position;
   }

   @Override
   public short getFootHolds() {
      return this.foodholds;
   }
}
