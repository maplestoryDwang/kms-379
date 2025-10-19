package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class SunknownMovement extends AbstractLifeMovement {
   private int nAttr;
   private byte force;

   public SunknownMovement(int type, Point position, int duration, int newstate) {
      super(type, position, duration, newstate, (short)0);
   }

   public void setAttr(int nAttr) {
      this.nAttr = nAttr;
   }

   public void setForce(byte force) {
      this.force = force;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.getType());
      packet.encodePos(this.getPosition());
      packet.writeShort(this.nAttr);
      packet.write(this.getNewstate());
      packet.writeShort(this.getDuration());
      packet.write(this.force);
   }
}
