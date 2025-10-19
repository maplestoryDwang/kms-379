package network.game.processors.job;

import network.decode.PacketDecoder;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.stats.SecondaryStatFlag;

public class YetiHandler {
   public static void yetiBoosterRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         int skillID = packet.readInt();
         if (skillID == 135001007 && player.getBuffedValue(SecondaryStatFlag.AutoChargeStack) == null) {
            player.setYetiBoosterTill(System.currentTimeMillis());
            player.temporaryStatSet(skillID, 2100000000, SecondaryStatFlag.AutoChargeStack, 0);
         }
      }
   }
}
