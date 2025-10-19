package objects.fields.child.fritto;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class CourtshipCommandEntry {
   private List<CourtshipCommandType> commands = new ArrayList<>();

   public void generateCommand(int stage) {
      int size = 0;
      switch (stage) {
         case 0:
            size = 4;
            break;
         case 1:
            size = 6;
            break;
         case 2:
         case 3:
         case 4:
            size = 7;
            break;
         case 5:
         case 6:
         case 7:
            size = 8;
            break;
         case 8:
         case 9:
            size = 10;
      }

      for (int i = 0; i < size; i++) {
         this.getCommands().add(CourtshipCommandType.random());
      }
   }

   public List<CourtshipCommandType> getCommands() {
      return this.commands;
   }

   public void setCommands(List<CourtshipCommandType> commands) {
      this.commands = commands;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.commands.size());

      for (CourtshipCommandType command : this.commands) {
         packet.writeInt(command.getType());
      }
   }
}
