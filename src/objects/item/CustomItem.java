package objects.item;

import database.DBConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Properties;
import objects.utils.Table;

public class CustomItem {
   public static Map<Integer, String> oldList;
   private int id;
   private CustomItem.CustomItemType type;
   private String name;
   private List<Pair<CustomItem.CustomItemEffect, Integer>> effects;

   public CustomItem(int id, CustomItem.CustomItemType type, String name) {
      this.id = id;
      this.type = type;
      this.name = name;
      this.effects = new ArrayList<>();
   }

   public int getId() {
      return this.id;
   }

   public CustomItem.CustomItemType getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public List<Pair<CustomItem.CustomItemEffect, Integer>> getEffects() {
      return this.effects;
   }

   public void addEffects(CustomItem.CustomItemEffect effect, Integer value) {
      this.effects.add(new Pair<>(effect, value));
   }

   public static void changeData(MapleCharacter chr) {
      if (oldList.containsKey(chr.getId())) {
         if (chr.getKeyValue("changeOldToNew") == null) {
            String data = oldList.get(chr.getId());
            Map<String, String> values = new HashMap<>();
            String[] keyvalues = data.split(";");

            for (String keyvalue : keyvalues) {
               String[] keyandvalue = keyvalue.split("=");
               if (keyandvalue.length > 0) {
                  values.put(keyandvalue[0], keyandvalue.length == 1 ? "" : keyandvalue[1]);
               }
            }

            chr.setKeyValue("changeOldToNew", "1");

            for (int i = 0; i <= 12; i++) {
               String value = null;
               if (values.containsKey(String.valueOf(i))) {
                  value = values.get(String.valueOf(i));
               }

               int oldData = value != null ? Integer.parseInt(value) : 0;
               int royalData = chr.getOneInfoQuestInteger(454545, String.valueOf(i));
               if (royalData > oldData) {
                  data = data.replaceFirst(i + "=" + oldData, i + "=" + royalData);
               }
            }

            chr.updateInfoQuest(454545, data);
            chr.dropMessage(5, "제니아에서 초월석 데이터를 성공적으로 가져왔습니다. (기존 데이터도 유지됩니다.)");
         }
      }
   }

   static {
      if (DBConfig.isGanglim) {
         oldList = new HashMap<>();
         Table table = Properties.loadTable("data/Ganglim", "CustomItem.data");
         if (table != null) {
            for (Table children : table.list()) {
               int userID = Integer.parseInt(children.getProperty("userID"));
               String customData = children.getProperty("customData");
               oldList.put(userID, customData);
            }
         }

         System.out.println("총 " + oldList.size() + "개의 과거 커스텀 아이템을 불러왔습니다. ");
      }
   }

   public static enum CustomItemEffect {
      BdR,
      CrD,
      AllStatR,
      MesoR,
      DropR,
      AllStat;
   }

   public static enum CustomItemType {
      None,
      보조장비,
      각인석;
   }
}
