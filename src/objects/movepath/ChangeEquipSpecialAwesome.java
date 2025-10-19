package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class ChangeEquipSpecialAwesome implements LifeMovementFragment {
   private final int type;
   private final int wui;

   public ChangeEquipSpecialAwesome(int type, int wui) {
      this.type = type;
      this.wui = wui;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.type);
      packet.write(this.wui);
   }

   @Override
   public Point getPosition() {
      return new Point(0, 0);
   }
}
