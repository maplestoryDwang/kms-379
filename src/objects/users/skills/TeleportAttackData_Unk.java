package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_Unk extends TeleportAttackData {
   public int arrowCount;
   public int unk2;

   public TeleportAttackData_Unk(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      this.arrowCount = packet.readInt();
      this.unk2 = packet.readInt();
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.arrowCount);
      packet.writeInt(this.unk2);
   }
}
