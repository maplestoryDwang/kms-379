package objects.users.achievement.caching;

import objects.users.achievement.caching.info.Info;
import objects.users.achievement.caching.mission.Mission;
import objects.wz.provider.MapleData;

public class AchievementData {
   private Info info;
   private Mission mission;

   public AchievementData(MapleData root) {
      this.info = new Info(root);
      this.mission = new Mission(root);
   }

   public Info getInfo() {
      return this.info;
   }

   public Mission getMission() {
      return this.mission;
   }
}
