package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class AranMovement extends AbstractLifeMovement {
   private byte ForcedStop;

   public AranMovement(int type, Point position, int duration, int newstate) {
      super(type, position, duration, newstate, (short)0);
   }

   public void setForcedStop(byte ForceStop) {
      this.ForcedStop = ForceStop;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.getType());
      packet.write(this.getNewstate());
      packet.writeShort(this.getDuration());
      packet.write(this.ForcedStop);
   }
}
