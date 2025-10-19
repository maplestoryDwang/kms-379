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
import objects.users.achievement.caching.mission.submission.score.Score;

public class MobKillOptimumCheck extends AchievementMissionCheck {
   int userLv;
   int mobLv;

   public MobKillOptimumCheck(int userLv, int mobLv) {
      this.userLv = userLv;
      this.mobLv = mobLv;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      int delta = Math.abs(this.userLv - this.mobLv);
      return delta <= 20 ? true : this.mobLv >= 275;
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      int[] ids = new int[]{547, 548, 549, 550, 551, 558};

      for (int id : ids) {
         if (!player.getAchievement().checkCompleteAchievement(id)) {
            AchievementData entry = AchievementFactory.achievementDatas.get(id);
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
                              Achievement achievement = player.getAchievement();
                              this.updateList.add(AchievementFactory.checkMission(delta, player, achievement, type, mission, score, id, subMissionID));
                           }
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
