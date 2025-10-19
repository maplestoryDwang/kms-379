package objects.fields.child.demian;

import java.util.List;
import network.encode.PacketEncoder;

public class DemianFlyingSword {
   private static int objid;
   public int attackIdx;
   public List<DemianObjectNodeData> lastNode;
   public int mobTemplateID;
   public int objectID;
   public int objectType;
   public int startX;
   public int startY;
   public int target;

   public DemianFlyingSword() {
      this.objectID = objid++;
   }

   public void encode(PacketEncoder packet) {
      packet.write(this.objectType);
      packet.write(this.attackIdx);
      packet.writeInt(this.mobTemplateID);
      packet.writeInt(this.startX);
      packet.writeInt(this.startY);
   }
}
