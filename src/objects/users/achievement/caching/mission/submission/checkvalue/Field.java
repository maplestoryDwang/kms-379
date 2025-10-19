package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Field {
   public static List<Integer> allFieldIDList = new ArrayList<>();
   private int fieldID;

   public Field(MapleData root) {
      this.fieldID = MapleDataTool.getInt("field_id", root, -1);
      if (this.fieldID != -1 && !allFieldIDList.contains(this.fieldID)) {
         allFieldIDList.add(this.fieldID);
      }
   }

   public boolean check(int fieldID) {
      return this.fieldID == fieldID;
   }
}
