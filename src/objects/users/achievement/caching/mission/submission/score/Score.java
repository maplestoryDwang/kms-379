package objects.users.achievement.caching.mission.submission.score;

import objects.users.achievement.AchievementCompareType;
import objects.users.achievement.AchievementCountingType;
import objects.users.achievement.AchievementScoreOptionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Score {
   private AchievementCompareType compareType;
   private AchievementCountingType countingType;
   private AchievementScoreOptionType optionType;
   private long targetScore;
   private SetValueType setValue;

   public Score(MapleData root) {
      long targetScore = MapleDataTool.getLongConvert("targetScore", root, 0);
      String compare = MapleDataTool.getString("compare", root, "");
      String type = MapleDataTool.getString("type", root, "");
      String option = MapleDataTool.getString("option", root, "");
      this.setTargetScore(targetScore);
      this.setCompareType(AchievementCompareType.getType(compare));
      this.setCountingType(AchievementCountingType.getType(type));
      this.setTargetScore(targetScore);
      String value = MapleDataTool.getString("setValue", root, "");
      if (!value.isEmpty()) {
         this.setSetValue(SetValueType.getType(value));
      }

      if (!option.isEmpty()) {
         this.setOptionType(AchievementScoreOptionType.getType(option));
      }
   }

   public AchievementCompareType getCompareType() {
      return this.compareType;
   }

   public void setCompareType(AchievementCompareType compareType) {
      this.compareType = compareType;
   }

   public AchievementCountingType getCountingType() {
      return this.countingType;
   }

   public void setCountingType(AchievementCountingType countingType) {
      this.countingType = countingType;
   }

   public long getTargetScore() {
      return this.targetScore;
   }

   public void setTargetScore(long targetScore) {
      this.targetScore = targetScore;
   }

   public AchievementScoreOptionType getOptionType() {
      return this.optionType;
   }

   public void setOptionType(AchievementScoreOptionType optionType) {
      this.optionType = optionType;
   }

   public SetValueType getSetValue() {
      return this.setValue;
   }

   public void setSetValue(SetValueType setValue) {
      this.setValue = setValue;
   }
}
