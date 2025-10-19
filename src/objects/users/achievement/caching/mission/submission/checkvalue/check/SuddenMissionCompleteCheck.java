package objects.users.achievement.caching.mission.submission.checkvalue.check;

import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;

public class SuddenMissionCompleteCheck extends AchievementMissionCheck {
   int questID;
   int clearTimeSecond;

   public SuddenMissionCompleteCheck(int questID, int clearTimeSecond) {
      this.questID = questID;
      this.clearTimeSecond = clearTimeSecond;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      return checkValue.check(null, null, null, null, 0, 0, 0, this.questID, 0, 0, 0, false, 0, 0, 0, "", 0, "", this.clearTimeSecond, 0);
   }
}
