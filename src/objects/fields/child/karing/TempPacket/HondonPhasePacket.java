package objects.fields.child.karing.TempPacket;

import java.awt.Point;
import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class HondonPhasePacket {
   public static byte[] KaringHandler(int type, MapleMonster mob, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1795);
      packet.writeInt(type);
      switch (type) {
         case 1:
            packet.writeInt(1000);
            break;
         case 2:
            boolean unkx = true;
            packet.write(unkx);
            if (unkx) {
               packet.encodePos4Byte(mob.getTruePosition());
            }
         case 3:
         default:
            break;
         case 4:
            boolean unk = false;
            packet.write(false);
            if (unk) {
               int a = 0;
               packet.writeInt(a);
               if (a > 0) {
                  packet.writeMapleAsciiString("");
                  packet.writeMapleAsciiString("");
                  packet.writeMapleAsciiString("");
                  packet.writeMapleAsciiString("");
                  packet.writeMapleAsciiString("");
                  packet.writeInt(0);
                  packet.writeInt(0);
                  packet.encodePos4Byte(new Point(0, 0));
               }
            }
            break;
         case 5:
            packet.writeInt(5000);
            break;
         case 6:
            packet.encodePos4Byte(chr.getTruePosition());
            break;
         case 7:
            packet.writeInt(0);
      }

      return packet.getPacket();
   }
}
