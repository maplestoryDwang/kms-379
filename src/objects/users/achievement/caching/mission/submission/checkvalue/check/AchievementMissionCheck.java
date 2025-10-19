package objects.users.achievement.caching.mission.submission.checkvalue.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import objects.users.MapleCharacter;
import objects.users.achievement.Achievement;
import objects.users.achievement.AchievementEntry;
import objects.users.achievement.AchievementFactory;
import objects.users.achievement.AchievementSubMissionType;
import objects.users.achievement.caching.mission.Mission;
import objects.users.achievement.caching.mission.MissionEntry;
import objects.users.achievement.caching.mission.submission.SubMission;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.score.Score;

public abstract class AchievementMissionCheck {
   protected List<AchievementEntry> updateList = new ArrayList<>();

   public abstract boolean check(CheckValue var1);

   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      AchievementFactory.achievementDatas.forEach((key, value) -> {
         int achievementID = key;
         Mission mission = value.getMission();
         if (mission != null) {
            Map<Integer, MissionEntry> missions = mission.getMissions();
            if (missions != null && !missions.isEmpty()) {
               for (Entry<Integer, MissionEntry> entryMap : missions.entrySet()) {
                  MissionEntry missionEntry = entryMap.getValue();
                  if (missionEntry != null) {
                     int subMissionID = entryMap.getKey();
                     SubMission subMission = missionEntry.getSubMission();
                     if (subMission != null && subMission.getSubMissionType() == type) {
                        CheckValue checkValue = subMission.getCheckValue();
                        Score score = subMission.getScore();
                        if (checkValue != null && score != null && this.check(checkValue)) {
                           Achievement achievement = player.getAchievement();
                           this.updateList.add(AchievementFactory.checkMission(delta, player, achievement, type, mission, score, achievementID, subMissionID));
                        }
                     }
                  }
               }
            }
         }
      });
      this.updateList.removeIf(Objects::isNull);
      player.getAchievement().updateAchievement(player, this.updateList);
      this.updateList.clear();
   }
}
