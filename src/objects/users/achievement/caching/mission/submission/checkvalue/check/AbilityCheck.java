package objects.users.achievement.caching.mission.submission.checkvalue.check;

import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import objects.users.MapleCharacter;
import objects.users.achievement.Achievement;
import objects.users.achievement.AchievementFactory;
import objects.users.achievement.AchievementSubMissionType;
import objects.users.achievement.caching.mission.Mission;
import objects.users.achievement.caching.mission.MissionEntry;
import objects.users.achievement.caching.mission.submission.SubMission;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.score.Score;

public class AbilityCheck extends AchievementMissionCheck {
   int skillID;
   int skillLevel;
   int grade;

   public AbilityCheck(int skillID, int skillLevel, int grade) {
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.grade = grade;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      return checkValue.check(null, null, null, null, 0, 0, 0, 0, 0, 0, 0, false, this.skillID, this.skillLevel, this.grade, "", 0, "", 0, 0);
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      AchievementFactory.achievementDatas
         .entrySet()
         .forEach(
            entry -> {
               int achievementID = entry.getKey();
               Mission mission = entry.getValue().getMission();
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
                                 long deltaScore = 1L;
                                 if (score.getSetValue() != null) {
                                    deltaScore = delta;
                                 }

                                 Achievement achievement = player.getAchievement();
                                 this.updateList
                                    .add(AchievementFactory.checkMission(deltaScore, player, achievement, type, mission, score, achievementID, subMissionID));
                              }
                           }
                        }
                     }
                  }
               }
            }
         );
      this.updateList.removeIf(Objects::isNull);
      player.getAchievement().updateAchievement(player, this.updateList);
      this.updateList.clear();
   }
}
