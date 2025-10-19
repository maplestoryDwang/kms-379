package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class AchievementChangeInfo {
   public static List<Integer> allAchievementIDList = new ArrayList<>();
   private int achievementID;
   private int achievementState;

   public AchievementChangeInfo(MapleData root) {
      this.setAchievementID(MapleDataTool.getInt("achievement_id", root, 0));
      this.setAchievementState(MapleDataTool.getInt("achievement_state", root, 0));
      if (!allAchievementIDList.contains(this.getAchievementID())) {
         allAchievementIDList.add(this.getAchievementID());
      }
   }

   public boolean check(int achievementID, int achievementState) {
      return this.achievementID != achievementID ? false : this.achievementState == achievementState;
   }

   public int getAchievementID() {
      return this.achievementID;
   }

   public void setAchievementID(int achievementID) {
      this.achievementID = achievementID;
   }

   public int getAchievementState() {
      return this.achievementState;
   }

   public void setAchievementState(int achievementState) {
      this.achievementState = achievementState;
   }
}
