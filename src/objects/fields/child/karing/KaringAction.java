package objects.fields.child.karing;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class KaringAction {
   public void encode(PacketEncoder packet) {
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
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
}
