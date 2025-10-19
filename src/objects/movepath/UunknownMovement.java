package objects.movepath;

import network.encode.PacketEncoder;

public class UunknownMovement extends AbstractLifeMovement {
   private int nAttr;
   private byte ForcedStop;

   public UunknownMovement(int type, int attr, int newstate, int dur, byte force) {
      super(type, null, dur, newstate, (short)0);
      this.nAttr = attr;
      this.ForcedStop = force;
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
      packet.writeInt(this.nAttr);
      packet.write(this.getNewstate());
      packet.writeShort(this.getDuration());
      packet.write(this.ForcedStop);
   }
}
