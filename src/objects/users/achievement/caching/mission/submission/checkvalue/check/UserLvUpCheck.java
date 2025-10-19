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
import objects.users.achievement.caching.mission.submission.checkvalue.Character;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.checkvalue.DateInfo;
import objects.users.achievement.caching.mission.submission.checkvalue.Field;
import objects.users.achievement.caching.mission.submission.score.Score;

public class UserLvUpCheck extends AchievementMissionCheck {
   private MapleCharacter player;
   private int fieldID;

   public UserLvUpCheck(MapleCharacter player, int fieldID) {
      this.player = player;
      this.fieldID = fieldID;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      boolean fieldCheck = true;
      Field field = checkValue.getField();
      if (field != null) {
         fieldCheck = field.check(this.fieldID);
      }

      boolean characterCheck = true;
      Character character = checkValue.getCharacter();
      if (character != null) {
         characterCheck = character.check(this.player);
      }

      boolean dateCheck = true;
      DateInfo dateInfo = checkValue.getDate();
      if (dateInfo != null) {
         dateCheck = dateInfo.check();
      }

      return fieldCheck && characterCheck && dateCheck;
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
                                 long deltaScore = delta;
                                 if (score.getSetValue() == null) {
                                    deltaScore = 1L;
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
