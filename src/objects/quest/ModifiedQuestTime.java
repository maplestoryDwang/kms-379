package objects.quest;

import network.encode.PacketEncoder;
import network.models.PacketHelper;

public class ModifiedQuestTime {
   private int questID;
   private long startTime;
   private long endTime;

   public ModifiedQuestTime(int questID, long startTime, long endTime) {
      this.questID = questID;
      this.startTime = startTime;
      this.endTime = endTime;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public long getEndTime() {
      return this.endTime;
   }

   public void setEndTime(long endTime) {
      this.endTime = endTime;
   }

   public int getQuestID() {
      return this.questID;
   }

   public void setQuestID(int questID) {
      this.questID = questID;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getQuestID());
      packet.writeLong(PacketHelper.getTime(this.getStartTime()));
      packet.writeLong(PacketHelper.getTime(this.getEndTime()));
   }
}
