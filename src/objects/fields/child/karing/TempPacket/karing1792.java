package objects.fields.child.karing.TempPacket;

import java.awt.Rectangle;
import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class karing1792 {
   public static byte[] KaringHandler(int type, MapleMonster mob, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1792);
      packet.writeInt(type);
      switch (type) {
         case 1001:
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.encodeRect(new Rectangle(0, 0, 0, 0));
            packet.encodeRect(new Rectangle(0, 0, 0, 0));
            packet.encodeRect(new Rectangle(0, 0, 0, 0));
            packet.encodeRect(new Rectangle(0, 0, 0, 0));
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            int size = 0;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(0);
               packet.writeLong(0L);
               packet.writeInt(0);
               packet.writeInt(0);
            }
            break;
         case 1002:
            size = 1;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(chr.getMapId());
               packet.writeInt(3);
            }
            break;
         case 1003:
            packet.writeInt(276);
            packet.writeInt(1);
            break;
         case 1004:
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
      }

      return packet.getPacket();
   }
}
