package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_PointWithDirection extends TeleportAttackData {
   public int direction;
   public int x;
   public int y;
   public byte unk_0;
   public byte unk_1;
   public byte unk_2;
   public byte unk_3;

   public TeleportAttackData_PointWithDirection(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      this.x = packet.readInt();
      this.y = packet.readInt();
      this.direction = packet.readByte();
      this.unk_0 = packet.readByte();
      this.unk_1 = packet.readByte();
      this.unk_2 = packet.readByte();
      this.unk_3 = packet.readByte();
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.x);
      packet.writeInt(this.y);
      packet.write(this.direction);
      packet.write(this.unk_0);
      packet.write(this.unk_1);
      packet.write(this.unk_2);
      packet.write(this.unk_3);
   }
}
