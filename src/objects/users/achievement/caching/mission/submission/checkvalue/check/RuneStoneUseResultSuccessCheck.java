package objects.users.achievement.caching.mission.submission.checkvalue.check;

import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;

public class RuneStoneUseResultSuccessCheck extends AchievementMissionCheck {
   int runeNumber;

   public RuneStoneUseResultSuccessCheck(int runeNumber) {
      this.runeNumber = runeNumber;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      return checkValue.check(null, null, null, null, 0, 0, 0, 0, 0, 0, 0, false, 0, 0, 0, "", 0, "", 0, this.runeNumber);
   }
}
