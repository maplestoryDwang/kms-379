package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Ability {
   private Ability.AbilitySkill abilitySkill;
   private int abilityGradeMin;
   private int abilityGradeMax;

   public Ability(MapleData root) {
      MapleData abilitySkill = root.getChildByPath("ability_skill");
      if (abilitySkill != null) {
         this.abilitySkill = new Ability.AbilitySkill(abilitySkill);
      }

      MapleData abilityGrade = root.getChildByPath("ability_grade");
      if (abilityGrade != null) {
         this.abilityGradeMin = MapleDataTool.getInt("min", abilityGrade, 0);
         this.abilityGradeMax = MapleDataTool.getInt("max", abilityGrade, 0);
      }
   }

   public boolean check(int grade, int skillID, int skillLevel) {
      if (this.abilityGradeMin <= 0 || this.abilityGradeMin <= grade && this.abilityGradeMax >= grade) {
         boolean abilitySkillCheck = true;
         if (this.abilitySkill != null) {
            abilitySkillCheck = this.abilitySkill.check(skillID, skillLevel);
         }

         return abilitySkillCheck;
      } else {
         return false;
      }
   }

   private class AbilitySkill {
      private AchievementConditionType condition;
      private List<Ability.AbilitySkill.AbilitySkillEntry> abilitySkillList;

      private AbilitySkill(MapleData root) {
         this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or"));
         this.abilitySkillList = new ArrayList<>();
         MapleData values = root.getChildByPath("values");
         if (values != null) {
            int index = 0;

            while (true) {
               MapleData value = values.getChildByPath(String.valueOf(index++));
               if (value == null) {
                  break;
               }

               this.abilitySkillList.add(new Ability.AbilitySkill.AbilitySkillEntry(value));
            }
         }
      }

      public boolean check(int skillID, int skillLevel) {
         switch (this.condition) {
            case or:
               for (Ability.AbilitySkill.AbilitySkillEntry entry : this.abilitySkillList) {
                  if (entry.check(skillID, skillLevel)) {
                     return true;
                  }
               }

               return false;
            case and:
               for (Ability.AbilitySkill.AbilitySkillEntry entryx : this.abilitySkillList) {
                  if (!entryx.check(skillID, skillLevel)) {
                     return false;
                  }
               }
            default:
               return true;
         }
      }

      private class AbilitySkillEntry {
         private int skillID;
         private int skillLevelMin;
         private int skillLevelMax;

         private AbilitySkillEntry(MapleData root) {
            this.skillID = MapleDataTool.getInt("skill_id", root, 0);
            this.skillLevelMin = MapleDataTool.getInt("min", root.getChildByPath("skill_level"), 0);
            this.skillLevelMax = MapleDataTool.getInt("max", root.getChildByPath("skill_level"), 0);
         }

         public boolean check(int skillID, int skillLevel) {
            if (this.skillID > 0 && this.skillID != skillID) {
               return false;
            } else {
               return this.skillLevelMin > 0 && this.skillLevelMin > skillLevel ? false : this.skillLevelMax <= 0 || this.skillLevelMax >= skillLevel;
            }
         }
      }
   }
}
