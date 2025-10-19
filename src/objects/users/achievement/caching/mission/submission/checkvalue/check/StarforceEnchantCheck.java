package objects.users.achievement.caching.mission.submission.checkvalue.check;

import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import objects.users.MapleCharacter;
import objects.users.achievement.Achievement;
import objects.users.achievement.AchievementFactory;
import objects.users.achievement.AchievementSubMissionType;
import objects.users.achievement.caching.AchievementData;
import objects.users.achievement.caching.mission.Mission;
import objects.users.achievement.caching.mission.MissionEntry;
import objects.users.achievement.caching.mission.submission.SubMission;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.checkvalue.StarforceEnchant;
import objects.users.achievement.caching.mission.submission.score.Score;

public class StarforceEnchantCheck extends AchievementMissionCheck {
   String result;
   int starForce;
   String catchResult;
   int meso;

   public StarforceEnchantCheck(String result, int starForce, String catchResult, int meso) {
      this.result = result;
      this.starForce = starForce;
      this.catchResult = catchResult;
      this.meso = meso;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      return checkValue.check(null, null, null, null, 0, 0, 0, 0, 0, 0, 0, false, 0, 0, 0, this.result, this.starForce, this.catchResult, 0, 0);
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      int[] ids = new int[]{
         942, 943, 944, 945, 946, 947, 948, 949, 950, 951, 952, 953, 954, 955, 956, 957, 958, 959, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 970, 982
      };

      for (int id : ids) {
         AchievementData entry = AchievementFactory.achievementDatas.get(id);
         int achievementID = id;
         Mission mission = entry.getMission();
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
                           long deltaScore = delta;
                           StarforceEnchant enchant = checkValue.getStarforceEnchant();
                           if (enchant == null) {
                              deltaScore = this.meso;
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

      this.updateList.removeIf(Objects::isNull);
      player.getAchievement().updateAchievement(player, this.updateList);
      this.updateList.clear();
   }
}
