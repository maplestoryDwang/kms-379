package network.game.processors.job;

import network.decode.PacketDecoder;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;

public class PinkBeanHandler {
   public static void roll_upgrade(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (player.getJob() == 13100) {
            player.temporaryStatSet(0, 2100000000, SecondaryStatFlag.PinkbeanRollingGrade, 1);
         }
      }
   }

   public static void yoyoStackUpRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (player.getJob() == 13100) {
            Integer value = player.getBuffedValue(SecondaryStatFlag.PinkbeanYoYoStack);
            if (player.getPinkBeanYoyoTill() == 0L || player.getPinkBeanYoyoTill() + 7000L <= System.currentTimeMillis()) {
               if (value == null) {
                  player.setPinkBeanYoyoTill(System.currentTimeMillis());
                  player.temporaryStatSet(131001010, 2100000000, SecondaryStatFlag.PinkbeanYoYoStack, 0);
               } else {
                  player.setPinkBeanYoyoTill(System.currentTimeMillis());
                  SecondaryStatManager statManager = new SecondaryStatManager(client, player.getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.PinkbeanYoYoStack, 131001010, value + 1);
                  statManager.temporaryStatSet();
               }
            }
         }
      }
   }
}
