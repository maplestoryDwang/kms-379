package objects.users.achievement.caching.mission;

import objects.users.achievement.caching.mission.submission.SubMission;
import objects.wz.provider.MapleData;

public class MissionEntry {
   private String name;
   private SubMission subMission;

   public MissionEntry(MapleData root, String name, String missionType) {
      this.setName(name);
      this.setSubMission(new SubMission(root, missionType));
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public SubMission getSubMission() {
      return this.subMission;
   }

   public void setSubMission(SubMission subMission) {
      this.subMission = subMission;
   }
}
