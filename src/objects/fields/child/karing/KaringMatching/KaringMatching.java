package objects.fields.child.karing.KaringMatching;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.users.looks.AvatarLook;

public class KaringMatching {
   private static int MatchingMapID = 410007100;

   public void encode(PacketEncoder packet) {
      packet.writeShort(SendPacketOpcode.KARING_MATCHING.getValue());
   }

   public void sendPacket(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      this.encode(packet);
      player.send(packet.getPacket());
   }

   public void broadcastPacket(Field field) {
      PacketEncoder packet = new PacketEncoder();
      this.encode(packet);
      field.broadcastMessage(packet.getPacket());
   }

   public static byte[] karingMatchingLoad(Party party, MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.KARING_MATCHING.getValue());
      packet.writeInt(KaringMatchType.Send.LoadParty.getType());
      packet.writeInt(party.getPartyMemberList().size());

      for (PartyMemberEntry pchr : player.getParty().getPartyMemberList()) {
         MapleCharacter chr = player.getMap().getCharacterById(pchr.getId());
         packet.writeInt(pchr.getId());
         packet.writeInt(-1);
         packet.writeInt(-1);
         packet.write(0);
         AvatarLook avatar = new AvatarLook(chr, false, false);
         byte[] packedAvatarLook = avatar.packedTo();
         packet.encodeBuffer(packedAvatarLook);
         packet.writeMapleAsciiString(pchr.getName());
         packet.writeInt(pchr.getJobId());
      }

      int time = 1800000;
      packet.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis() + time));
      return packet.getPacket();
   }

   public static class InitPacket extends KaringMatching {
      public static class Ready extends KaringMatching.InitPacket {
         final KaringMatchType.Send type = KaringMatchType.Send.Ready;
         MapleCharacter chr;
         Party party;
         boolean ready;

         public Ready(MapleCharacter chr, Party party, boolean ready) {
            this.chr = chr;
            this.party = party;
            this.ready = ready;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.chr.getId());
            packet.write(this.ready);
         }
      }

      public static class Select extends KaringMatching.InitPacket {
         final KaringMatchType.Send type = KaringMatchType.Send.Select;
         MapleCharacter chr;
         Party party;
         int postion;
         int types;

         public Select(MapleCharacter chr, Party party, int types, int postion) {
            this.chr = chr;
            this.party = party;
            this.types = types;
            this.postion = postion;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.chr.getId());
            packet.writeInt(this.types);
            packet.writeInt(this.postion);
         }
      }
   }
}
