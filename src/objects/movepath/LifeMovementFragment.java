package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public interface LifeMovementFragment {
   void serialize(PacketEncoder var1);

   Point getPosition();
}
