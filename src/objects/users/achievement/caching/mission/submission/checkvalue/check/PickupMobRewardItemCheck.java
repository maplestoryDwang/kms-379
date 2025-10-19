package objects.users.achievement.caching.mission.submission.checkvalue.check;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import objects.item.Item;
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

public class PickupMobRewardItemCheck extends AchievementMissionCheck {
   List<Item> itemList;

   public PickupMobRewardItemCheck(List<Item> itemList) {
      this.itemList = itemList;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      ItemCheck check = checkValue.getItem();
      return check != null ? check.check(this.itemList) : false;
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      int[] ids = new int[]{
         837,
         838,
         839,
         840,
         841,
         842,
         843,
         844,
         850,
         851,
         852,
         853,
         854,
         855,
         856,
         857,
         858,
         859,
         860,
         861,
         866,
         867,
         869,
         870,
         872,
         873,
         874,
         875,
         876,
         877,
         878
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
