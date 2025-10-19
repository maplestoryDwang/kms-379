package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class ExceedInfo {
   private int defaultTime;
   private int max;
   private List<ExceedInfoEntry> list;

   public ExceedInfo(MapleData data) {
      this.defaultTime = MapleDataTool.getInt("defaultTime", data, 0);
      this.list = new ArrayList<>();
      MapleData l = data.getChildByPath("list");
      if (l != null) {
         int i = 0;

         while (true) {
            MapleData ex = l.getChildByPath(String.valueOf(i));
            if (ex == null) {
               break;
            }

            this.getList().add(new ExceedInfoEntry(ex));
            this.max = i++;
         }
      }
   }

   public int getDefaultTime() {
      return this.defaultTime;
   }

   public int getMax() {
      return this.max;
   }

   public List<ExceedInfoEntry> getList() {
      return this.list;
   }
}
