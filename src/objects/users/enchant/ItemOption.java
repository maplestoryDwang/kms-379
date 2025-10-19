package objects.users.enchant;

import java.util.Map;
import objects.item.MapleItemInformationProvider;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class ItemOption {
   public int id;
   public ItemOptionLevelData[] level = new ItemOptionLevelData[21];
   public int optionType;
   public int reqLevel;
   public String string;
   public int uniqueOption;

   public ItemOption(MapleData data, Map<String, Integer> types) {
      this.id = Integer.parseInt(data.getName());
      MapleData info = data.getChildByPath("info");
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (info != null) {
         this.optionType = MapleDataTool.getInt("optionType", info, 0);
         this.string = MapleDataTool.getString("string", info, "");
         if (this.string == null || this.string.isEmpty()) {
            return;
         }

         Integer value = types.get(this.string);
         if (value == null) {
            this.uniqueOption = types.size();
            types.put(this.string, this.uniqueOption);
         } else {
            this.uniqueOption = value;
         }

         this.reqLevel = MapleDataTool.getInt("reqLevel", info, 0);
         int id = this.id;
         MapleData level = data.getChildByPath("level");
         if (level != null) {
            for (int i = 0; i < 20; i++) {
               this.level[i] = ii.getPotentialInfo(id).get(i);
            }
         }
      }
   }
}
