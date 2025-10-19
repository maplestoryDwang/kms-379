package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.utils.FileoutputUtil;

public class TeleportAttackData_TriArray extends TeleportAttackData {
   public TeleportAttackData_TriArray_Elem[] data;

   public TeleportAttackData_TriArray(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      int size = packet.readInt();
      if (size > 1000) {
         String msg = "TeleportAttackData_TriArray 오류 (데미지 파싱 잘못됨)\r\n" + packet.toString(true) + "\r\n";
         FileoutputUtil.log("./ErrorLog/Log_DamageParseError.rtf", msg);
      } else {
         this.data = new TeleportAttackData_TriArray_Elem[size];

         for (int i = 0; i < this.data.length; i++) {
            this.data[i] = new TeleportAttackData_TriArray_Elem();
            this.data[i].objectID = packet.readInt();
            this.data[i].x = packet.readInt();
            this.data[i].y = packet.readInt();
         }
      }
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.data.length);

      for (int i = 0; i < this.data.length; i++) {
         packet.writeInt(this.data[i].objectID);
         packet.writeInt(this.data[i].x);
         packet.writeInt(this.data[i].y);
      }
   }
}
