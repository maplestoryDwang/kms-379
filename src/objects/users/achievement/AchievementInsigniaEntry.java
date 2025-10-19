package objects.users.achievement;

import network.encode.PacketEncoder;
import network.models.PacketHelper;

public class AchievementInsigniaEntry {
   private AchievementGrade grade;
   private int status;
   private long achieveTime;

   public AchievementInsigniaEntry(AchievementGrade grade, int status, long achieveTime) {
      this.setGrade(grade);
      this.setStatus(status);
      this.setAchieveTime(achieveTime);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.grade.getGrade());
      packet.write(this.status);
      packet.writeLong(PacketHelper.getKoreanTimestamp(this.achieveTime));
   }

   public AchievementGrade getGrade() {
      return this.grade;
   }

   public void setGrade(AchievementGrade grade) {
      this.grade = grade;
   }

   public int getStatus() {
      return this.status;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public long getAchieveTime() {
      return this.achieveTime;
   }

   public void setAchieveTime(long achieveTime) {
      this.achieveTime = achieveTime;
   }
}
