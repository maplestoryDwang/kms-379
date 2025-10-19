package objects.users.achievement.caching.mission.submission.checkvalue.check;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import objects.item.Equip;
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
import objects.users.achievement.caching.mission.submission.checkvalue.MakingskillResult;
import objects.users.achievement.caching.mission.submission.score.Score;

public class MakingskillSynthesizeCheck extends AchievementMissionCheck {
   boolean success;
   Equip item1;
   Equip item2;
   Equip result;

   public MakingskillSynthesizeCheck(boolean success, Equip item1, Equip item2, Equip result) {
      this.success = success;
      this.item1 = item1;
      this.item2 = item2;
      this.result = result;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      boolean iCheck = true;
      ItemCheck itemCheck = checkValue.getItem();
      if (itemCheck != null) {
         iCheck = itemCheck.check(Arrays.asList(this.item1, this.item2));
      }

      boolean msrCheck = true;
      MakingskillResult makingskillResultCheck = checkValue.getMakingskillResult();
      if (makingskillResultCheck != null) {
         msrCheck = makingskillResultCheck.check(this.success, this.result);
      }

      return iCheck && msrCheck;
   }

   @Override
   public void checkMission(MapleCharacter player, AchievementSubMissionType type, long delta) {
      int[] ids = new int[]{195, 196, 197, 198, 199, 200};

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
