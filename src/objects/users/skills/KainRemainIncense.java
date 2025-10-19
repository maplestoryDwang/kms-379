package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class KainRemainIncense {
   private int playerID;
   private List<KainRemainIncenseEntry> entrys = new ArrayList<>();

   public KainRemainIncense(int playerID) {
      this.playerID = playerID;
   }

   public void addEntry(int objectID, int x, int y) {
      this.entrys.add(new KainRemainIncenseEntry(objectID, x, y));
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.playerID);
      packet.writeInt(this.entrys.size());
      this.entrys.forEach(entry -> entry.encode(packet));
   }
}
