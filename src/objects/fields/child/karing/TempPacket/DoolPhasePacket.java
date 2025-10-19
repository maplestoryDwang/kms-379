package objects.fields.child.karing.TempPacket;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class DoolPhasePacket {
   public static byte[] KaringHandler(int type, MapleMonster mob, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1794);
      packet.writeInt(type);
      switch (type) {
         case 0:
            packet.writeInt(mob.getObjectId());
            int size = 5;
            packet.writeInt(size);
            List<Point> pos = new ArrayList<>();
            pos.clear();
            pos.add(new Point(1226, -45));
            pos.add(new Point(-123, -45));
            pos.add(new Point(-773, 405));
            pos.add(new Point(556, 405));
            pos.add(new Point(1913, 405));

            for (int a = 0; a < size; a++) {
               packet.encodePos4Byte(pos.get(a));
               packet.writeInt(1);
               packet.write(false);
            }

            int size2 = 2;
            packet.writeInt(size2);

            for (int a = 0; a < size2; a++) {
               packet.encodePos4Byte(chr.getTruePosition());
               packet.writeInt(1);
            }

            int size3 = 1;
            packet.writeInt(size3);

            for (int a = 0; a < size3; a++) {
               packet.writeInt(1000);
               packet.writeInt(1000);
               packet.writeInt(1000);
               packet.write(true);
            }

            packet.writeInt(mob.getId());
            int size4 = 0;
            packet.writeInt(size4);

            for (int a = 0; a < size4; a++) {
               packet.writeInt(410007180);
               packet.encodePos4Byte(new Point(chr.getTruePosition().x + 200, chr.getTruePosition().y + 200));
               packet.write(false);
            }
            break;
         case 1:
            packet.write(0);
            size = 0;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(0);
            }
            break;
         case 2:
            boolean unk = true;
            packet.write(true);
            packet.writeInt(mob.getId());
            if (unk) {
               packet.write(true);
            }
            break;
         case 3:
            packet.writeInt(chr.getId());
            packet.write(true);
            break;
         case 4:
            packet.write(true);
            break;
         case 5:
            packet.writeInt(0);
            size = 0;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(0);
            }
            break;
         case 6:
            packet.writeInt(1);
            break;
         case 7:
            packet.writeInt(mob.getId());
         case 8:
         case 12:
         default:
            break;
         case 9:
            packet.writeInt(0);
            size = 0;
            packet.writeInt(size);

            for (int a = 0; a < size; a++) {
               packet.writeInt(0);
               packet.writeInt(0);
               packet.encodePos4Byte(new Point(0, 0));
            }
            break;
         case 10:
            boolean bool = true;
            packet.write(bool);
            if (!bool) {
               size = 0;
               packet.writeInt(size);

               for (int a = 0; a < size; a++) {
                  packet.writeInt(1000);
               }
            }
            break;
         case 11:
            packet.writeInt(mob.getId());
            break;
         case 13:
            packet.write(true);
      }

      return packet.getPacket();
   }
}
