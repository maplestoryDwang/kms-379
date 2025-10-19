package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.utils.FileoutputUtil;

public class TeleportAttackData_DoubleArray extends TeleportAttackData {
   public TeleportAttackData_DoubleArray_Elem[] data;

   public TeleportAttackData_DoubleArray(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      int size = packet.readInt();
      if (size > 1000) {
         String msg = "TeleportAttackData_DoubleArray 오류 (데미지 파싱 잘못됨)\r\n" + packet.toString(true) + "\r\n";
         FileoutputUtil.log("./ErrorLog/Log_DamageParseError.rtf", msg);
      } else {
         this.data = new TeleportAttackData_DoubleArray_Elem[size];

         for (int i = 0; i < size; i++) {
            this.data[i] = new TeleportAttackData_DoubleArray_Elem();
            this.data[i].data1 = packet.readInt();
            this.data[i].data2 = packet.readInt();
         }
      }
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         packet.writeInt(this.data[i].data1);
         packet.writeInt(this.data[i].data2);
      }
   }
}
