package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class ChairMovement extends AbstractLifeMovement {
   private byte ForcedStop_CS;
   private int unk;

   public ChairMovement(int type, Point position, int duration, int newstate, short FH, int unk) {
      super(type, position, duration, newstate, FH);
      this.unk = unk;
   }

   public void setForcedStop_CS(byte ForcedStop_CS) {
      this.ForcedStop_CS = ForcedStop_CS;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.getType());
      packet.encodePos(this.getPosition());
      packet.writeShort(this.getFootHolds());
      packet.writeInt(this.unk);
      packet.write(this.getNewstate());
      packet.writeShort(this.getDuration());
      packet.write(this.ForcedStop_CS);
   }
}
