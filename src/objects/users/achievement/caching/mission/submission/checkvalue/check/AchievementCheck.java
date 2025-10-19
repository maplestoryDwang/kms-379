package objects.users.achievement.caching.mission.submission.checkvalue.check;

import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;

public class AchievementCheck extends AchievementMissionCheck {
   int achievementID;
   int achievementState;

   public AchievementCheck(int achievementID, int achievementState) {
      this.achievementID = achievementID;
      this.achievementState = achievementState;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      return checkValue.check(null, null, null, null, 0, this.achievementID, this.achievementState, 0, 0, 0, 0, false, 0, 0, 0, "", 0, "", 0, 0);
   }
}
