package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class RelativeLifeMovement extends AbstractLifeMovement {
   private int nAttr;
   private byte ForcedStop;
   private short n1;
   private short n2;

   public RelativeLifeMovement(int type, Point position, int duration, int newstate) {
      super(type, position, duration, newstate, (short)0);
   }

   public void setN1(short n1) {
      this.n1 = n1;
   }

   public void setN2(short n2) {
      this.n2 = n2;
   }

   public void setAttr(int nAttr) {
      this.nAttr = nAttr;
   }

   public void setForcedStop(byte ForcedStop) {
      this.ForcedStop = ForcedStop;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.getType());
      packet.encodePos(this.getPosition());
      if (this.getType() == 21 || this.getType() == 22) {
         packet.writeShort(this.nAttr);
      }

      if (this.getType() == 60) {
         packet.writeShort(this.n1);
         packet.writeShort(this.n2);
      }

      packet.write(this.getNewstate());
      packet.writeShort(this.getDuration());
      packet.write(this.ForcedStop);
   }
}
