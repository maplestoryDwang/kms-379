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
import objects.users.achievement.caching.mission.submission.checkvalue.MakingskillResult;
import objects.users.achievement.caching.mission.submission.checkvalue.SkillID;
import objects.users.achievement.caching.mission.submission.score.Score;

public class MakingskillMakeCheck extends AchievementMissionCheck {
   boolean success;
   int skillID;

   public MakingskillMakeCheck(boolean success, int skillID) {
      this.success = success;
      this.skillID = skillID;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      boolean skillCheck = true;
      SkillID skillIDCheck = checkValue.getSkillID();
      if (skillIDCheck != null) {
         skillCheck = skillIDCheck.check(this.skillID);
      }

      boolean makingSkillCheck = true;
      MakingskillResult msrCheck = checkValue.getMakingskillResult();
      if (msrCheck != null) {
         makingSkillCheck = msrCheck.check(this.success, null);
      }

      return skillCheck && makingSkillCheck;
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      int[] ids = new int[]{
         165,
         166,
         167,
         168,
         169,
         170,
         171,
         172,
         173,
         174,
         175,
         176,
         177,
         178,
         179,
         180,
         181,
         182,
         201,
         202,
         203,
         204,
         205,
         206,
         207,
         208,
         209,
         210,
         211,
         212,
         213,
         214,
         215
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
                           Achievement achievement = player.getAchievement();
                           this.updateList.add(AchievementFactory.checkMission(delta, player, achievement, type, mission, score, achievementID, subMissionID));
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
