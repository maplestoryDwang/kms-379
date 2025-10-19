package objects.shop;

import network.encode.PacketEncoder;
import network.models.PacketHelper;

public class BuyLimitEntry {
   private int count;
   private int itemID;
   private int itemIndex;
   private int npcID;
   private long time;

   public BuyLimitEntry(int count, int itemID, int itemIndex, int npcID, long time) {
      this.count = count;
      this.itemID = itemID;
      this.itemIndex = itemIndex;
      this.npcID = npcID;
      this.time = time;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.npcID);
      packet.writeShort(this.itemIndex);
      packet.writeInt(this.itemID);
      packet.writeShort(this.count);
      packet.writeLong(PacketHelper.getKoreanTimestamp(this.time));
   }
}
