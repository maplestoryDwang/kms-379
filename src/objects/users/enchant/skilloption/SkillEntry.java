package objects.users.enchant.skilloption;

import java.util.HashMap;
import java.util.Map;
import objects.utils.Randomizer;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class SkillEntry {
   private int incTableID = 0;
   private int reqLevel = 0;
   private int skillID = 0;
   private Map<Integer, TempOptionEntry> tempOptions;

   public SkillEntry(MapleData data) {
      this.incTableID = MapleDataTool.getInt("incTableID", data, 0);
      this.reqLevel = MapleDataTool.getInt("reqLevel", data, 0);
      this.skillID = MapleDataTool.getInt("skillId", data, 0);
      MapleData tempOption = data.getChildByPath("tempOption");
      this.tempOptions = new HashMap<>();

      for (MapleData root : tempOption.getChildren()) {
         int name = Integer.parseInt(root.getName());
         TempOptionEntry entry = new TempOptionEntry(root);
         this.tempOptions.put(name, entry);
      }
   }

   public TempOptionEntry getTempOptionEntry(Integer idx) {
      return this.tempOptions.get(idx);
   }

   public TempOptionEntry getOnePickTempOptionEntry() {
      return this.tempOptions.get(Randomizer.rand(0, this.tempOptions.size() - 1));
   }

   public int getIncTableID() {
      return this.incTableID;
   }

   public void setIncTableID(int incTableID) {
      this.incTableID = incTableID;
   }

   public int getReqLevel() {
      return this.reqLevel;
   }

   public void setReqLevel(int reqLevel) {
      this.reqLevel = reqLevel;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }
}
