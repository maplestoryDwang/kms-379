package objects.context;

import database.DBConfig;
import java.util.ArrayList;
import java.util.List;
import objects.utils.Properties;
import objects.utils.Table;

public class GoldenChariot {
   public static final List<GoldenChariot> goldenChariotList = new ArrayList<>();
   int day;
   int itemID;
   int itemQty;

   public static void cachingGCList() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "GoldenChariot.data");

      for (Table child : table.list()) {
         int day = Integer.parseInt(child.getProperty("day"));
         int itemID = Integer.parseInt(child.getProperty("itemID"));
         int itemQty = Integer.parseInt(child.getProperty("itemQuantity"));
         GoldenChariot eL = new GoldenChariot(day, itemID, itemQty);
         goldenChariotList.add(eL);
      }

      System.out.println("[황금마차] " + goldenChariotList.size() + "개 캐싱완료");
   }

   public GoldenChariot(int day, int itemID, int itemQty) {
      this.day = day;
      this.itemID = itemID;
      this.itemQty = itemQty;
   }

   public int getDay() {
      return this.day;
   }

   public int getItemID() {
      return this.itemID;
   }

   public int getItemQty() {
      return this.itemQty;
   }
}
