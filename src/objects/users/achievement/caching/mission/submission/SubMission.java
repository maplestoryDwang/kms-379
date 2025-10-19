package objects.users.achievement.caching.mission.submission;

import objects.users.achievement.AchievementSubMissionType;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.score.Score;
import objects.wz.provider.MapleData;

public class SubMission {
   private AchievementSubMissionType subMissionType;
   private Score score;
   private CheckValue checkValue;

   public SubMission(MapleData root, String missionType) {
      this.setSubMissionType(AchievementSubMissionType.getType(missionType));
      this.setScore(new Score(root.getChildByPath("score")));
      MapleData checkValue = root.getChildByPath("checkValue");
      if (checkValue != null) {
         this.setCheckValue(new CheckValue(checkValue, root.getName()));
      }
   }

   public AchievementSubMissionType getSubMissionType() {
      return this.subMissionType;
   }

   public void setSubMissionType(AchievementSubMissionType subMissionType) {
      this.subMissionType = subMissionType;
   }

   public Score getScore() {
      return this.score;
   }

   public void setScore(Score score) {
      this.score = score;
   }

   public CheckValue getCheckValue() {
      return this.checkValue;
   }

   public void setCheckValue(CheckValue checkValue) {
      this.checkValue = checkValue;
   }
}
