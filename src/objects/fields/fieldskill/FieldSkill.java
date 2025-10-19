package objects.fields.fieldskill;

import java.util.HashMap;
import java.util.Map;
import objects.wz.provider.MapleData;

public class FieldSkill {
   private Map<Integer, FieldSkillEntry> entrys = new HashMap<>();

   public FieldSkill(MapleData data) {
      for (MapleData level : data.getChildren()) {
         int lv = Integer.parseInt(level.getName());
         FieldSkillEntry info = new FieldSkillEntry(level);
         this.getEntrys().put(lv, info);
      }
   }

   public Map<Integer, FieldSkillEntry> getEntrys() {
      return this.entrys;
   }

   public void setEntrys(Map<Integer, FieldSkillEntry> entrys) {
      this.entrys = entrys;
   }

   public FieldSkillEntry getFieldSkillEntry(int skillLevel) {
      return this.entrys.get(skillLevel);
   }
}
