package objects.users.achievement.caching.mission.submission.checkvalue.check;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.achievement.Achievement;
import objects.users.achievement.AchievementFactory;
import objects.users.achievement.AchievementSubMissionType;
import objects.users.achievement.caching.mission.Mission;
import objects.users.achievement.caching.mission.MissionEntry;
import objects.users.achievement.caching.mission.submission.SubMission;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.checkvalue.Mob;
import objects.users.achievement.caching.mission.submission.checkvalue.SkillUse;
import objects.users.achievement.caching.mission.submission.score.Score;

public class MobKillCheck extends AchievementMissionCheck {
   MapleCharacter player;
   MapleCharacter lastAttacker;
   MapleCharacter highestDamagedAttacker;
   List<MapleCharacter> players;
   MapleMonster mob;
   int skillID;
   boolean optimum;

   public MobKillCheck(
      MapleCharacter player,
      MapleCharacter lastAttacker,
      MapleCharacter highestDamagedAttacker,
      List<MapleCharacter> players,
      MapleMonster mob,
      int skillID,
      boolean optimum
   ) {
      this.player = player;
      this.lastAttacker = lastAttacker;
      this.highestDamagedAttacker = highestDamagedAttacker;
      this.players = players;
      this.skillID = skillID;
      this.mob = mob;
      this.optimum = optimum;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      return checkValue.check(
         this.player, this.lastAttacker, this.highestDamagedAttacker, this.players, this.skillID, 0, 0, 0, 0, 0, 0, false, 0, 0, 0, "", 0, "", 0, 0
      );
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
                           if (subMission != null) {
                              CheckValue checkValue = subMission.getCheckValue();
                              Score score = subMission.getScore();
                              if (checkValue != null && score != null) {
                                 Mob m = checkValue.getMob();
                                 SkillUse skillUse = checkValue.getSkillUse();
                                 if (checkValue.getFieldset() != null && player.getBossMode() == 1) {
                                    return;
                                 }

                                 if (m != null || skillUse != null) {
                                    boolean check = true;
                                    if (m != null) {
                                       check = m.check(this.mob, this.optimum);
                                    }

                                    if ((check || skillUse != null && skillUse.check(this.skillID)) && this.check(checkValue)) {
                                       Achievement achievement = player.getAchievement();
                                       this.updateList
                                          .add(AchievementFactory.checkMission(delta, player, achievement, type, mission, score, achievementID, subMissionID));
                                    }
                                 }
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
