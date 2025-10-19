package objects.users.achievement.caching.mission.submission.checkvalue.check;

import objects.users.MapleTrait;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.checkvalue.NCStatType;

public class NCStatExpUpCheck extends AchievementMissionCheck {
   MapleTrait trait;

   public NCStatExpUpCheck(MapleTrait trait) {
      this.trait = trait;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      NCStatType ncStatType = checkValue.getNcStatType();
      return ncStatType == null || ncStatType.check(this.trait.getType());
   }
}
