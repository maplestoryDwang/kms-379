package objects.users.achievement.caching.mission.submission.checkvalue;

import java.text.SimpleDateFormat;
import java.util.Date;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class DateInfo {
   AchievementConditionType condition;
   int date_mm;
   int date_mmddhhmmss_start;
   int date_mmddhhmmss_end;
   int date_mmddhhmmss_start_t;
   int date_mmddhhmmss_end_t;

   public DateInfo(MapleData root) {
      this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or"));
      MapleData date_mm = root.getChildByPath("date_mm");
      if (date_mm != null) {
         this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", date_mm, "or"));
         MapleData values = date_mm.getChildByPath("values");
         if (values != null) {
            for (MapleData data : values) {
               int month = MapleDataTool.getInt(data, -1);
               if (month != -1) {
                  this.date_mm = month;
               }
            }
         }
      }

      MapleData date_mmddhhmmss = root.getChildByPath("date_mmddhhmmss");
      if (date_mmddhhmmss != null) {
         this.date_mmddhhmmss_start = MapleDataTool.getInt("start", date_mmddhhmmss, -1);
         this.date_mmddhhmmss_end = MapleDataTool.getInt("end", date_mmddhhmmss, -1);
         this.date_mmddhhmmss_start_t = MapleDataTool.getInt("start_t", date_mmddhhmmss, -1);
         this.date_mmddhhmmss_end_t = MapleDataTool.getInt("end_t", date_mmddhhmmss, -1);
         if (this.date_mmddhhmmss_start == -1 || this.date_mmddhhmmss_end == -1 || this.date_mmddhhmmss_start_t == -1 || this.date_mmddhhmmss_end_t == -1) {
            System.out.println("버그발생 버그발생");
         }
      }

      if (this.condition == AchievementConditionType.and) {
         System.out.println("AND가 있다고!? Date");
         System.out.println(root.getParent().getParent().getParent().getParent().getParent().getParent().getName());
      }
   }

   public boolean check() {
      switch (this.condition) {
         case or:
            if (this.date_mm > 0 && this.date_mm == new Date().getMonth() + 1) {
               return true;
            } else if (this.date_mmddhhmmss_start > 0) {
               SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmmss");
               int dat = Integer.parseInt(sdf.format(new Date()));
               if (this.date_mmddhhmmss_start <= dat && this.date_mmddhhmmss_end >= dat) {
                  return true;
               }
            }
         default:
            return false;
      }
   }
}
