package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_Octa extends TeleportAttackData {
   public int data1;
   public int data2;
   public int data3;
   public int data4;
   public int data5;
   public int data6;
   public int data7;
   public int data8;

   public TeleportAttackData_Octa(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      this.data1 = packet.readInt();
      this.data2 = packet.readInt();
      this.data3 = packet.readInt();
      this.data4 = packet.readInt();
      this.data5 = packet.readInt();
      this.data6 = packet.readInt();
      this.data7 = packet.readInt();
      this.data8 = packet.readInt();
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.data1);
      packet.writeInt(this.data2);
      packet.writeInt(this.data3);
      packet.writeInt(this.data4);
      packet.writeInt(this.data5);
      packet.writeInt(this.data6);
      packet.writeInt(this.data7);
      packet.writeInt(this.data8);
   }
}
