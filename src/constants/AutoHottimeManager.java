package constants;

import database.DBConfig;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import objects.utils.Properties;
import objects.utils.Table;

public class AutoHottimeManager {
   public static List<AutoHottimeEntry> entryList = new ArrayList<>();

   public static void loadAutoHottime() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "HottimeEventSchedule.data");
      int count = 0;
      entryList.clear();

      for (Table children : table.list()) {
         String start = children.getProperty("Start");
         String end = children.getProperty("End");
         String type = children.getProperty("Type");
         SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd/HH/mm");

         try {
            long startTime = sdf.parse(start).getTime();
            long endTime = sdf.parse(end).getTime();
            AutoHottimeEntry.EventType eventType = null;
            if (type.equals("Exp")) {
               eventType = AutoHottimeEntry.EventType.Exp;
            } else if (type.equals("Meso")) {
               eventType = AutoHottimeEntry.EventType.Meso;
            } else if (type.equals("Drop")) {
               eventType = AutoHottimeEntry.EventType.Drop;
            } else if (type.equals("Give")) {
               eventType = AutoHottimeEntry.EventType.Give;
            }

            if (eventType == AutoHottimeEntry.EventType.Give) {
               int itemID = children.getProperty("ItemID", 0);
               int itemCount = children.getProperty("Count", 0);
               if (System.currentTimeMillis() < endTime) {
                  entryList.add(new AutoHottimeEntry(eventType, startTime, endTime, itemID, itemCount, false));
               }
            } else {
               String rate = children.getProperty("Rate");
               double eventRate = Double.parseDouble(rate);
               if (System.currentTimeMillis() < endTime) {
                  entryList.add(new AutoHottimeEntry(eventType, startTime, endTime, eventRate, false));
               }
            }

            count++;
         } catch (Exception var16) {
         }
      }

      System.out.println("총 " + count + "개의 자동 핫타임 스케쥴 데이터를 불러왔습니다.");
   }
}
