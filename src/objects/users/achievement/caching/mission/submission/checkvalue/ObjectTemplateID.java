package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class ObjectTemplateID {
   int objectTemplateID;

   public ObjectTemplateID(MapleData root) {
      this.objectTemplateID = MapleDataTool.getInt(root.getChildByPath("object_template_id"), 0);
   }

   public boolean check(int ojt) {
      boolean ret = true;
      if (this.objectTemplateID > 0 && this.objectTemplateID != ojt) {
         ret = false;
      }

      return ret;
   }
}
