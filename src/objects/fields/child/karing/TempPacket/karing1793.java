package objects.fields.child.karing.TempPacket;

import network.encode.PacketEncoder;

public class karing1793 {
   public static byte[] KaringHandler(int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1793);
      packet.writeInt(type);
      if (type == 0) {
         packet.writeInt(60000);
      }

      return packet.getPacket();
   }
}
