package objects.fields.child.karing.TempPacket;

import java.awt.Rectangle;
import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Karing3PhasePacket {
   public static byte[] KaringHandler(int type, MapleMonster mob, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1798);
      packet.writeInt(type);
      switch (type) {
         case 0:
         default:
            break;
         case 1:
            int size = 0;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(0);
               int sizea = 0;
               packet.writeInt(0);

               for (int ab = 0; ab < sizea; ab++) {
                  packet.writeInt(0);
               }
            }

            int size2 = 0;
            packet.writeInt(size2);

            for (int a = 0; a < size2; a++) {
               packet.writeInt(0);
               packet.writeInt(0);
            }

            int size3 = 0;
            packet.writeInt(size3);

            for (int a = 0; a < size3; a++) {
               packet.writeInt(0);
               packet.encodeRect(new Rectangle(0, 0, 0, 0));
            }
            break;
         case 2:
            packet.writeInt(277);
            packet.writeInt(1);
            break;
         case 3:
            packet.writeInt(3);
            break;
         case 4:
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
            break;
         case 5:
            packet.writeInt(0);
            break;
         case 6:
            size = 0;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(0);
               packet.writeInt(0);
            }
      }

      return packet.getPacket();
   }
}
