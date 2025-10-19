package objects.fields;

import java.util.List;
import network.encode.PacketEncoder;

public class DimensionalMirrorEntry {
   private String title = null;
   private String desc = null;
   private int minLevel = 0;
   private int type = 0;
   private List<Integer> rewards;

   public DimensionalMirrorEntry(String title, String desc, int minLevel, int type, List<Integer> rewards) {
      this.title = title;
      this.desc = desc;
      this.minLevel = minLevel;
      this.type = type;
      this.rewards = rewards;
   }

   public void encode(PacketEncoder packet) {
      packet.writeMapleAsciiString(this.title);
      packet.writeMapleAsciiString(this.desc);
      packet.writeInt(this.minLevel);
      packet.writeInt(0);
      packet.writeInt(this.type);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeMapleAsciiString("");
      packet.write(0);
      packet.writeInt(0);
      packet.writeInt(this.rewards.size());

      for (Integer itemID : this.rewards) {
         packet.writeInt(itemID);
      }
   }
}
