package network.game.processors;

import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CWvsContext;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.stats.SecondaryStatFlag;

public class FlagRaceHandler {
   public static void Switching_Shoes(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         if (user.getMap().getFieldSetInstance() != null) {
            int unk = slea.readInt();
            int type = slea.readInt();
            int what2 = slea.readInt();
            int skillID = slea.readInt();
            int cooldown = slea.readInt();
            if (skillID == 80003061) {
               int state = 0;
               if (user.getBuffedValue(SecondaryStatFlag.FlagRace_CramponShoes) == null) {
                  state = 1;
                  user.temporaryStatSet(0, Integer.MAX_VALUE, SecondaryStatFlag.FlagRace_CramponShoes, 1);
               } else {
                  user.temporaryStatReset(SecondaryStatFlag.FlagRace_CramponShoes);
               }

               user.send(CWvsContext.enableActions(user));
               user.send(switching_shoes(type, skillID, cooldown, state));
            }
         }
      }
   }

   public static byte[] switching_shoes(int type, int skillID, int cooldown, int state) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BLACK_MAGE_TEMPORARY_SKILL.getValue());
      packet.writeInt(8);
      packet.writeInt(type);
      packet.writeInt(1);
      packet.writeInt(skillID);
      packet.writeInt(cooldown);
      packet.writeInt(cooldown);
      packet.writeInt(System.currentTimeMillis() % 10000000L);
      packet.writeInt(state);
      return packet.getPacket();
   }
}
