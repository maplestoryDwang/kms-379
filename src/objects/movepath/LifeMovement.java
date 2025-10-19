package objects.movepath;

import java.awt.Point;

public interface LifeMovement extends LifeMovementFragment {
   @Override
   Point getPosition();

   int getNewstate();

   int getDuration();

   int getType();

   short getFootHolds();
}
