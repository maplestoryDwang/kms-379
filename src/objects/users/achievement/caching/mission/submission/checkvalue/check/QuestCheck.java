package objects.users.achievement.caching.mission.submission.checkvalue.check;

import objects.users.MapleCharacter;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;

public class QuestCheck extends AchievementMissionCheck {
   MapleCharacter player;
   int questID;
   int questState;

   public QuestCheck(MapleCharacter player, int questID, int questState) {
      this.player = player;
      this.questID = questID;
      this.questState = questState;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      return checkValue.check(this.player, null, null, null, 0, 0, 0, this.questID, this.questState, 0, 0, false, 0, 0, 0, "", 0, "", 0, 0);
   }
}
