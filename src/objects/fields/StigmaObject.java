package objects.fields;

import network.encode.PacketEncoder;

public class StigmaObject {
   public int castingTime;
   public long endTime;
   public int footholdSN;
   public String objectUOL;
   public int x;
   public int y;

   public void encode(PacketEncoder packet, boolean immediatelyStart) {
      packet.writeInt(this.x);
      packet.writeInt(this.y);
      packet.writeInt(this.castingTime);
      packet.writeInt(this.footholdSN);
      packet.writeMapleAsciiString(this.objectUOL);
      packet.write(immediatelyStart);
   }
}
