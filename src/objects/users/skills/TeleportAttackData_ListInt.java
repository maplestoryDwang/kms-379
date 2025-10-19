package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_ListInt extends TeleportAttackData {
   private List<Integer> intList = new ArrayList<>();

   public TeleportAttackData_ListInt(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      int size = packet.readInt();

      for (int i = 0; i < size; i++) {
         this.intList.add(packet.readInt());
      }
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.intList.size());
      this.intList.forEach(packet::writeInt);
   }

   public List<Integer> getIntList() {
      return this.intList;
   }
}
