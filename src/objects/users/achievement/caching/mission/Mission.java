package objects.users.achievement.caching.mission;

import java.util.HashMap;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Mission {
   private Map<Integer, MissionEntry> missions;

   public Mission(MapleData root) {
      this.setMissions(new HashMap<>());
      MapleData mission = root.getChildByPath("mission");
      if (mission != null) {
         for (MapleData d : mission.getChildren()) {
            int subMissionID = Integer.parseInt(d.getName());
            String name = MapleDataTool.getString("name", d, "");
            MapleData subMission = d.getChildByPath("subMission");

            for (MapleData r : subMission.getChildren()) {
               String subMissionType = r.getName();
               int acvID = Integer.parseInt(root.getName().replace(".img", ""));
               if ((acvID < 216 || acvID > 220 || !subMissionType.equals("day_change"))
                  && (acvID != 558 || !subMissionType.equals("user_hit") && !subMissionType.equals("field_leave"))) {
                  MissionEntry missionEntry = new MissionEntry(r, name, subMissionType);
                  this.getMissions().put(subMissionID, missionEntry);
               }
            }
         }
      }
   }

   public Map<Integer, MissionEntry> getMissions() {
      return this.missions;
   }

   public void setMissions(Map<Integer, MissionEntry> missions) {
      this.missions = missions;
   }
}
