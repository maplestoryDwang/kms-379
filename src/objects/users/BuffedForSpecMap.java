package objects.users;

import network.encode.PacketEncoder;

public class BuffedForSpecMap {
   private int itemID;
   private boolean enable;

   public BuffedForSpecMap(int itemID, boolean enable) {
      this.itemID = itemID;
      this.enable = enable;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.itemID);
      packet.write(this.enable);
   }
}
