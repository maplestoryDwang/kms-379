package objects.users.achievement.caching;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class AchievementGradeInfo {
   private String gradeName;
   private String name;
   private int scoreMin;
   private int scoreMax;

   public AchievementGradeInfo(MapleData data) {
      this.setGradeName(data.getName());
      this.setName(MapleDataTool.getString("name", data, ""));
      this.setScoreMin(MapleDataTool.getInt("scoreMin", data, 0));
      this.setScoreMax(MapleDataTool.getInt("scoreMax", data, 0));
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getScoreMin() {
      return this.scoreMin;
   }

   public void setScoreMin(int scoreMin) {
      this.scoreMin = scoreMin;
   }

   public int getScoreMax() {
      return this.scoreMax;
   }

   public void setScoreMax(int scoreMax) {
      this.scoreMax = scoreMax;
   }

   public String getGradeName() {
      return this.gradeName;
   }

   public void setGradeName(String gradeName) {
      this.gradeName = gradeName;
   }
}
