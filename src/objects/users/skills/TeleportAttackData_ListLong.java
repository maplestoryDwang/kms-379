package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_ListLong extends TeleportAttackData {
   private List<Long> longList = new ArrayList<>();

   public TeleportAttackData_ListLong(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      int size = packet.readInt();

      for (int i = 0; i < size; i++) {
         this.longList.add(packet.readLong());
      }
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.longList.size());
      this.longList.forEach(packet::writeLong);
   }

   public List<Long> getLongList() {
      return this.longList;
   }
}
