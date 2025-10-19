package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_Skill extends TeleportAttackData {
   public int skillID;
   public int unk;

   public TeleportAttackData_Skill(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      this.unk = packet.readByte();
      this.skillID = packet.readInt();
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.unk);
      packet.writeInt(this.skillID);
   }
}
