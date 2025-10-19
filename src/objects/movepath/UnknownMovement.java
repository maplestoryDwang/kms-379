package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class UnknownMovement extends AbstractLifeMovement {
   private Point pixelsPerSecond;
   private byte force;

   public UnknownMovement(int type, Point position, int duration, int newstate, short FH, byte force) {
      super(type, position, duration, newstate, FH);
      this.force = force;
   }

   public Point getPixelsPerSecond() {
      return this.pixelsPerSecond;
   }

   public void setPixelsPerSecond(Point wobble) {
      this.pixelsPerSecond = wobble;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.getType());
      packet.encodePos(this.getPosition());
      packet.encodePos(this.pixelsPerSecond);
      packet.writeShort(this.getFootHolds());
      packet.write(this.getNewstate());
      packet.writeShort(this.getDuration());
      packet.write(this.force);
   }
}
