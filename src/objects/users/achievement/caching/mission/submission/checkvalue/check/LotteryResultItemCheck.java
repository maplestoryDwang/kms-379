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
import objects.users.achievement.caching.mission.submission.checkvalue.LotteryItemID;
import objects.users.achievement.caching.mission.submission.checkvalue.LotteryResultItemID;
import objects.users.achievement.caching.mission.submission.checkvalue.LotteryResultItemLevel;
import objects.users.achievement.caching.mission.submission.score.Score;

public class LotteryResultItemCheck extends AchievementMissionCheck {
   int itemID;
   int resultItemID;
   int resultItemLevel;

   public LotteryResultItemCheck(int itemID, int resultItemID, int resultItemLevel) {
      this.itemID = itemID;
      this.resultItemID = resultItemID;
      this.resultItemLevel = resultItemLevel;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      boolean itemCheck = true;
      LotteryItemID iCheck = checkValue.getLotteryItemID();
      if (iCheck != null) {
         itemCheck = iCheck.check(this.itemID);
      }

      boolean resultIDCheck = true;
      LotteryResultItemID resultItemID = checkValue.getLotteryResultItemID();
      if (resultItemID != null) {
         resultIDCheck = resultItemID.check(this.resultItemID);
      }

      boolean resultLvCheck = true;
      LotteryResultItemLevel resultItemLv = checkValue.getLotteryResultItemLevel();
      if (resultItemLv != null) {
         resultLvCheck = resultItemLv.check(this.resultItemLevel);
      }

      return itemCheck && resultIDCheck && resultLvCheck;
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      int[] ids = new int[]{1426, 1427, 1428, 1429};

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
