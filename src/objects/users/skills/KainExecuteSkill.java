package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class KainExecuteSkill {
   private int skillID = 0;
   private List<KainExecuteSkillEntry> entrys = new ArrayList<>();

   public KainExecuteSkill(int skillID) {
      this.skillID = skillID;
   }

   public void addEntry(int targetObjectID, int delay) {
      KainExecuteSkillEntry entry = new KainExecuteSkillEntry(targetObjectID, 1, delay);
      this.entrys.add(entry);
   }

   public int getEntryCount() {
      return this.entrys.size();
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
      packet.writeInt(0);
      packet.writeInt(this.entrys.size());
      this.entrys.forEach(entry -> entry.encode(packet));
   }
}
