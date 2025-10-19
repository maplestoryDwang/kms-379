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
import objects.users.achievement.caching.mission.submission.checkvalue.ItemCheck;
import objects.users.achievement.caching.mission.submission.score.Score;

public class ComboKillGetMarbleCheck extends AchievementMissionCheck {
   private int itemID;

   public ComboKillGetMarbleCheck(int itemID) {
      this.itemID = itemID;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      boolean itemCheck = true;
      ItemCheck item = checkValue.getItem();
      if (item != null) {
         itemCheck = item.check(this.itemID);
      }

      return itemCheck;
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      int[] ids = new int[]{1378, 1379, 1380, 1381, 616, 617, 618, 619};

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
