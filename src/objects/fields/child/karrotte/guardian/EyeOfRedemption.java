package objects.fields.child.karrotte.guardian;

import java.awt.Point;
import network.encode.PacketEncoder;

public class EyeOfRedemption extends GuardianEntry {
   public EyeOfRedemption(int index, Point position, byte unk, GuardianType type, int refMobID) {
      super(index, position, unk, type, refMobID);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getRefMobID());
      packet.writeInt(this.getType().getType());
      packet.writeInt(this.getUnk2());
      packet.writeInt(this.getAngle());
      packet.writeInt(this.getAttackInterval());
      packet.writeInt(this.getDeactiveHitCount());
   }
}
