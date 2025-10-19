package constants;

import database.DBConfig;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import objects.utils.Properties;
import objects.utils.Table;

public class HottimeItemManager {
   public static List<HottimeItemEntry> entryList = new ArrayList<>();

   public static void loadHottimeItem() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "HottimeItemSchedule.data");
      int count = 0;
      entryList.clear();

      for (Table children : table.list()) {
         String time_ = children.getProperty("Time");
         int itemID = children.getProperty("Item", 0);
         int quantity = children.getProperty("Quantity", 0);
         SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd/HH/mm");

         try {
            long time = sdf.parse(time_).getTime();
            if (time > System.currentTimeMillis()) {
               entryList.add(new HottimeItemEntry(time, itemID, quantity));
               count++;
            }
         } catch (Exception var10) {
         }
      }

      System.out.println("총 " + count + "개의 자동 핫타임 아이템 스케쥴 데이터를 불러왔습니다.");
   }
}
