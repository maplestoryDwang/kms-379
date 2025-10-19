package objects.users.achievement.caching.mission.submission.checkvalue.check;

import objects.fields.fieldset.FieldSetInstance;
import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.checkvalue.Field;
import objects.users.achievement.caching.mission.submission.checkvalue.Fieldset;

public class FieldCheck extends AchievementMissionCheck {
   private int fieldID;
   private FieldSetInstance fieldSetInstance;

   public FieldCheck(int fieldID, FieldSetInstance fieldSetInstance) {
      this.fieldID = fieldID;
      this.fieldSetInstance = fieldSetInstance;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      Field field = checkValue.getField();
      if (field != null) {
         return field.check(this.fieldID);
      } else {
         Fieldset fieldset = checkValue.getFieldset();
         return fieldset != null ? fieldset.check(this.fieldSetInstance) : true;
      }
   }
}
