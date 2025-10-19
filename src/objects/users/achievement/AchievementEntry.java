package objects.users.achievement;

import java.util.HashMap;
import java.util.Map;
import network.encode.PacketEncoder;
import network.models.PacketHelper;

public class AchievementEntry {
   private int achievementID;
   private int mission;
   private AchievementMissionStatus status;
   private long time;
   private String subMission;

   public AchievementEntry(int achievementID, int mission, AchievementMissionStatus status, long time, String subMission) {
      this.setAchievementID(achievementID);
      this.setMission(mission);
      this.setStatus(status);
      this.setTime(time);
      this.setSubMission(subMission);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getAchievementID());
      packet.write(this.getMission());
      packet.write(this.getStatus().getStatus());
      if (this.getStatus() == AchievementMissionStatus.NotStart) {
         packet.writeLong(PacketHelper.getTime(-2L));
      } else {
         packet.writeLong(PacketHelper.getKoreanTimestamp(this.getTime()));
      }

      int unknown = 42;
      packet.writeInt(unknown);
      if (unknown == 42) {
         packet.writeMapleAsciiString(this.getSubMission());
      } else {
         packet.writeLong(0L);
      }
   }

   public int getAchievementID() {
      return this.achievementID;
   }

   public void setAchievementID(int achievementID) {
      this.achievementID = achievementID;
   }

   public int getMission() {
      return this.mission;
   }

   public void setMission(int mission) {
      this.mission = mission;
   }

   public AchievementMissionStatus getStatus() {
      return this.status;
   }

   public void setStatus(AchievementMissionStatus status) {
      this.status = status;
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public String getSubMission() {
      return this.subMission;
   }

   public String getSubMission(String key) {
      if (this.subMission != null && !this.subMission.isEmpty()) {
         Map<String, String> values = new HashMap<>();
         String[] keyvalues = this.subMission.split(";");

         for (String keyvalue : keyvalues) {
            String[] keyandvalue = keyvalue.split("=");
            values.put(keyandvalue[0], keyandvalue.length == 1 ? "" : keyandvalue[1]);
         }

         if (values.containsKey(key)) {
            return values.get(key);
         }
      }

      return "";
   }

   public void setSubMission(String subMission) {
      this.subMission = subMission;
   }

   public void setSubMission(String key, String value) {
      this.subMission = key + "=" + value;
   }
}
