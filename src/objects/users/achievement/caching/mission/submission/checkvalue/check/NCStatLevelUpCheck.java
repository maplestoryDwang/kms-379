package objects.users.achievement.caching.mission.submission.checkvalue.check;

import objects.users.MapleTrait;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.checkvalue.NCStatType;

public class NCStatLevelUpCheck extends AchievementMissionCheck {
   MapleTrait trait;

   public NCStatLevelUpCheck(MapleTrait trait) {
      this.trait = trait;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      NCStatType ncStatLevelUp = checkValue.getNcStatType();
      return ncStatLevelUp == null || ncStatLevelUp.check(this.trait.getType());
   }
}
