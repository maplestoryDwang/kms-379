package objects.fields;

import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;

public class DimensionalMirror {
   private List<DimensionalMirrorEntry> entry = new ArrayList<>();

   public void addEntry(DimensionalMirrorEntry mirror) {
      this.entry.add(mirror);
   }

   public void encode(PacketEncoder packet) {
      packet.writeShort(SendPacketOpcode.UI_DIMENSIONAL_MIRROR.getValue());
      packet.writeInt(this.entry.size());
      this.entry.stream().forEach(mirror -> mirror.encode(packet));
   }
}
