package objects.fields.child.fritto;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class CourtshipCommand {
   private List<CourtshipCommandEntry> commands = new ArrayList<>();

   public void generateCommands() {
      for (int i = 0; i < 10; i++) {
         CourtshipCommandEntry entry = new CourtshipCommandEntry();
         entry.generateCommand(i);
         this.commands.add(entry);
      }
   }

   public List<CourtshipCommandEntry> getCommands() {
      return this.commands;
   }

   public void setCommands(List<CourtshipCommandEntry> commands) {
      this.commands = commands;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.commands.size());

      for (CourtshipCommandEntry command : this.commands) {
         command.encode(packet);
      }
   }
}
