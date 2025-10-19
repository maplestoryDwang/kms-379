package objects.users.skills;

import network.encode.PacketEncoder;

public class KainRemainIncenseEntry {
   private int wreckageObjectID;
   private int x;
   private int y;

   public KainRemainIncenseEntry(int wreckageObjectID, int x, int y) {
      this.wreckageObjectID = wreckageObjectID;
      this.x = x;
      this.y = y;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.wreckageObjectID);
      packet.writeInt(this.x);
      packet.writeInt(this.y);
   }
}
