package objects.fields.child.karing.TempPacket;

import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.looks.AvatarLook;

public class KaringMatchingPacket {
   public static byte[] Handler(int type, MapleMonster mob, MapleCharacter player) {
      return Handler(type, null, mob, player);
   }

   public static byte[] Handler(int type, Party party, MapleMonster mob, MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(1793);
      packet.writeInt(type);
      switch (type) {
         case 0:
            packet.writeInt(party.getPartyMemberList().size());

            for (PartyMemberEntry pchr : player.getParty().getPartyMemberList()) {
               MapleCharacter chr = player.getMap().getCharacterById(pchr.getId());
               packet.writeInt(pchr.getId());
               packet.writeInt(-1);
               packet.writeInt(-1);
               packet.write(0);
               AvatarLook avatarx = new AvatarLook(chr, false, false);
               byte[] packedAvatarLookx = avatarx.packedTo();
               packet.encodeBuffer(packedAvatarLookx);
               packet.writeMapleAsciiString(pchr.getName());
               packet.writeInt(pchr.getJobId());
            }

            int time = 1800000;
            packet.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis() + time));
            break;
         case 1:
            packet.writeInt(0);
            packet.writeInt(player.getId());
            packet.writeInt(-1);
            packet.write(0);
            AvatarLook avatar = new AvatarLook(player, false, false);
            byte[] packedAvatarLook = avatar.packedTo();
            packet.encodeBuffer(packedAvatarLook);
            packet.writeMapleAsciiString(player.getName());
            packet.writeInt(player.getJob());
         case 2:
         case 6:
         default:
            break;
         case 3:
            int types = 0;
            int postion = 0;
            packet.writeInt(player.getId());
            packet.writeInt(types);
            packet.writeInt(postion);
            break;
         case 4:
            packet.writeInt(player.getId());
            packet.write(false);
            break;
         case 5:
            packet.writeInt(player.getId());
      }

      return packet.getPacket();
   }
}
