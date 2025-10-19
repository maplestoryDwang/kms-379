package objects.fields.child.karing.TempPacket;

import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Karing2PhasePacket {
   public static byte[] KaringHandler(int type, MapleMonster mob, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1796);
      packet.writeInt(type);
      switch (type) {
         case 0:
            packet.writeInt(5000);
            break;
         case 1:
            packet.writeInt(3000);
            break;
         case 2:
            packet.writeInt(mob.getId());
            packet.writeInt(0);
            packet.encodePos4Byte(mob.getPosition());
            packet.writeMapleAsciiString("사흉을 모시깽이 합니다.");
            break;
         case 3:
            packet.writeInt(3);
            break;
         case 4:
            packet.writeInt(mob.getId());
         case 5:
         case 7:
         default:
            break;
         case 6:
            int size = 2;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(size);
               packet.writeInt(1);
               packet.encodePos4Byte(chr.getPosition());
            }
            break;
         case 8:
            packet.encodePos4Byte(mob.getPosition());
            packet.encodePos4Byte(chr.getTruePosition());
      }

      return packet.getPacket();
   }
}
