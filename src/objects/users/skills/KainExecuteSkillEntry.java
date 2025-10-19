package objects.users.skills;

import network.encode.PacketEncoder;

public class KainExecuteSkillEntry {
   private int targetObjectID;
   private int unk;
   private int delay;

   public KainExecuteSkillEntry(int targetObjectID, int unk, int delay) {
      this.targetObjectID = targetObjectID;
      this.unk = unk;
      this.delay = delay;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.targetObjectID);
      packet.writeInt(this.unk);
      packet.writeInt(this.delay);
   }
}
