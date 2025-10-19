package objects.users;

import java.util.ArrayList;
import java.util.List;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class WorldLvChairInfo {
   public int totalLevel;
   public int unk2;
   public int unk;
   List<WorldLvChairInfoCharacter> characters = new ArrayList<>();

   public void decode(PacketDecoder packet) {
      this.totalLevel = packet.readInt();
      this.unk2 = packet.readInt();
      int count = packet.readInt();

      for (int i = 0; i < count; i++) {
         WorldLvChairInfoCharacter c = new WorldLvChairInfoCharacter();
         c.decode(packet);
         this.characters.add(c);
      }

      this.unk = packet.readInt();
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.totalLevel);
      packet.writeInt(this.unk2);
      packet.writeInt(this.characters.size());

      for (WorldLvChairInfoCharacter c : this.characters) {
         c.encode(packet);
      }

      packet.writeInt(this.unk);
   }
}
