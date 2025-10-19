package objects.fields;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class ForceAtom_Parallel_Bullet {
   public List<ForceAtom.AtomInfo> atomInfos = new ArrayList<>();
   public int targetID;

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.targetID);
      int next = 0;

      for (ForceAtom.AtomInfo fi : this.atomInfos) {
         packet.write(1);
         fi.encode(packet, null, next++);
      }

      packet.write(0);
   }
}
