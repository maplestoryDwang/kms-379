package objects.users.achievement;

public enum AchievementMissionStatus {
   Test(-1),
   NotStart(0),
   Start(1),
   Complete(2);

   private int status;

   private AchievementMissionStatus(int status) {
      this.status = status;
   }

   public int getStatus() {
      return this.status;
   }

   public static AchievementMissionStatus getStatus(int status) {
      for (AchievementMissionStatus g : values()) {
         if (g.getStatus() == status) {
            return g;
         }
      }

      return null;
   }
}
