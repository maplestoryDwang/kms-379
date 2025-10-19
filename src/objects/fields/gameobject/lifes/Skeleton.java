package objects.fields.gameobject.lifes;

import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;

public class Skeleton {
   public static void attackSkeletonImage(PacketDecoder lea, AttackInfo ret) {
      byte unk = lea.readByte();
      if (unk == 1) {
         lea.readMapleAsciiString();
         lea.readInt();
         int a = lea.readInt();
         if (a > 0) {
            for (int k = 0; k < a; k++) {
               lea.readMapleAsciiString();
            }
         } else {
            lea.readInt();
         }
      } else if (unk == 2) {
         lea.readMapleAsciiString();
         lea.readInt();
      }

      lea.readByte();
      ret.attackPosition = lea.readPos();
      ret.attackPosition2 = lea.readPos();
      ret.attackPosition3 = lea.readPos();
      lea.readByte();
      lea.readByte();
      lea.readInt();
      lea.readInt();
      lea.readInt();
      int a = lea.readInt();

      for (int i = 0; i < a; i++) {
         lea.readInt();
         lea.readInt();
      }
   }
}
