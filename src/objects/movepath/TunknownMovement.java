package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class TunknownMovement extends AbstractLifeMovement {
   private Point offset;
   private byte force;

   public TunknownMovement(int type, Point position, int duration, int newstate) {
      super(type, position, duration, newstate, (short)0);
   }

   public void setOffset(Point wobble) {
      this.offset = wobble;
   }

   public void setForce(byte force) {
      this.force = force;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.getType());
      packet.encodePos(this.getPosition());
      packet.encodePos(this.offset);
      packet.write(this.getNewstate());
      packet.writeShort(this.getDuration());
      packet.write(this.force);
   }
}
