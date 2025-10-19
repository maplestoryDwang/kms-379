package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_TripleInt extends TeleportAttackData {
   private int data1 = 0;
   private int data2 = 0;
   private int data3 = 0;

   public TeleportAttackData_TripleInt(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      this.data1 = packet.readInt();
      this.data2 = packet.readInt();
      this.data3 = packet.readInt();
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.data1);
      packet.writeInt(this.data2);
      packet.writeInt(this.data3);
   }

   public int getData1() {
      return this.data1;
   }

   public void setData1(int data1) {
      this.data1 = data1;
   }

   public int getData2() {
      return this.data2;
   }

   public void setData2(int data2) {
      this.data2 = data2;
   }

   public int getData3() {
      return this.data3;
   }

   public void setData3(int data3) {
      this.data3 = data3;
   }
}
