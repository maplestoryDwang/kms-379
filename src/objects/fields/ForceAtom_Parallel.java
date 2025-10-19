package objects.fields;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class ForceAtom_Parallel {
   public List<ForceAtom_Parallel_Entry> entries = new ArrayList<>();
   public int fromID;
   public int skillID;

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.fromID);
      packet.writeInt(this.skillID);
      packet.writeInt(this.entries.size());

      for (ForceAtom_Parallel_Entry e : this.entries) {
         e.encode(packet);
      }
   }
}
