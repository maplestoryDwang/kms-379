package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class AbsoluteLifeMovement extends AbstractLifeMovement {
   private Point pixelsPerSecond;
   private Point offset;
   private int nAttr;
   private byte ForcedStop_CS;
   private short unk;

   public AbsoluteLifeMovement(int type, Point position, int duration, int newstate, short FH) {
      super(type, position, duration, newstate, FH);
   }

   public void setPixelsPerSecond(Point wobble) {
      this.pixelsPerSecond = wobble;
   }

   public void setOffset(Point wobble) {
      this.offset = wobble;
   }

   public void setForcedStop_CS(byte ForcedStop_CS) {
      this.ForcedStop_CS = ForcedStop_CS;
   }

   public void setnAttr(int nAttr) {
      this.nAttr = nAttr;
   }

   public void setUnk(short unk) {
      this.unk = unk;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.getType());
      packet.encodePos(this.getPosition());
      packet.encodePos(this.pixelsPerSecond);
      packet.writeShort(this.getFootHolds());
      if (this.getType() == 15 || this.getType() == 17) {
         packet.writeShort(this.nAttr);
      }

      packet.encodePos(this.offset);
      packet.writeShort(this.unk);
      packet.write(this.getNewstate());
      packet.writeShort(this.getDuration());
      packet.write(this.ForcedStop_CS);
   }
}
