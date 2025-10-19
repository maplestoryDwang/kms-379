package objects.context;

import network.encode.PacketEncoder;

public class ReportLogEntry {
   String playerName;
   String text;
   int playerID;

   public ReportLogEntry(String playerName, String text, int playerID) {
      this.playerName = playerName;
      this.text = text;
      this.playerID = playerID;
   }

   public void encode(PacketEncoder packet) {
      packet.writeMapleAsciiString(this.playerName);
      packet.writeMapleAsciiString(this.text);
      packet.writeLong(0L);
      packet.write(1);
      packet.writeInt(this.playerID);
      packet.writeInt(0);
      packet.writeMapleAsciiString(this.playerName);
      packet.writeInt(this.playerID);
   }
}
