package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.fields.fieldset.FieldSetInstance;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Fieldset {
   private int fieldsetElapsedTimeMin = 0;
   private int fieldsetElapsedTimeMax = 0;
   private String fieldsetName;
   private int fieldsetIndex;

   public Fieldset(MapleData root) {
      MapleData data = root.getChildByPath("fieldset_elapsed_time");
      if (data != null) {
         this.setFieldsetElapsedTimeMin(MapleDataTool.getInt("min", data, 0));
         this.setFieldsetElapsedTimeMax(MapleDataTool.getInt("max", data, 0));
      }

      this.setFieldsetName(MapleDataTool.getString("fieldset_name", root, ""));
      this.setFieldsetIndex(MapleDataTool.getInt("fieldset_index", root, 0));
   }

   public boolean check(FieldSetInstance fielset) {
      if (fielset == null) {
         return false;
      } else {
         long delta = System.currentTimeMillis() - fielset.getFieldSetStartTime();
         if (delta < this.fieldsetElapsedTimeMin || delta > this.fieldsetElapsedTimeMax) {
            return false;
         } else {
            return !this.fieldsetName.isEmpty() ? false : this.fieldsetIndex <= 0;
         }
      }
   }

   public int getFieldsetElapsedTimeMin() {
      return this.fieldsetElapsedTimeMin;
   }

   public void setFieldsetElapsedTimeMin(int fieldsetElapsedTimeMin) {
      this.fieldsetElapsedTimeMin = fieldsetElapsedTimeMin;
   }

   public int getFieldsetElapsedTimeMax() {
      return this.fieldsetElapsedTimeMax;
   }

   public void setFieldsetElapsedTimeMax(int fieldsetElapsedTimeMax) {
      this.fieldsetElapsedTimeMax = fieldsetElapsedTimeMax;
   }

   public String getFieldsetName() {
      return this.fieldsetName;
   }

   public void setFieldsetName(String fieldsetName) {
      this.fieldsetName = fieldsetName;
   }

   public int getFieldsetIndex() {
      return this.fieldsetIndex;
   }

   public void setFieldsetIndex(int fieldsetIndex) {
      this.fieldsetIndex = fieldsetIndex;
   }
}
