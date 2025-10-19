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
import objects.users.achievement.caching.mission.submission.checkvalue.MultiKillCount;
import objects.users.achievement.caching.mission.submission.score.Score;

public class MultiKillCountCheck extends AchievementMissionCheck {
   private int multiKill;

   public MultiKillCountCheck(int multiKill) {
      this.multiKill = multiKill;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      boolean multiKillCountCheck = true;
      MultiKillCount multiKillCount = checkValue.getMultiKillCount();
      if (multiKillCount != null) {
         multiKillCountCheck = multiKillCount.check(this.multiKill);
      }

      return multiKillCountCheck;
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      int[] ids = new int[]{1384, 620, 621, 622, 623, 624};

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

      this.updateList.removeIf(Objects::isNull);
      player.getAchievement().updateAchievement(player, this.updateList);
      this.updateList.clear();
   }
}
