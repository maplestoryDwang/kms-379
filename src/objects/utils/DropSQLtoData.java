package objects.utils;

import database.DBConnection;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import objects.fields.gameobject.lifes.MonsterDropEntry;
import objects.fields.gameobject.lifes.MonsterGlobalDropEntry;

public class DropSQLtoData {
   public static void main(String[] args) {
      DBConnection.init();
      Map<Integer, List<MonsterDropEntry>> ret = new HashMap<>();
      PreparedStatement ps = null;
      ResultSet rs = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM drop_data");
         rs = ps.executeQuery();

         while (rs.next()) {
            int dropperID = rs.getInt("dropperid");
            int itemid = rs.getInt("itemid");
            int chance = rs.getInt("chance");
            List<MonsterDropEntry> list = ret.getOrDefault(dropperID, new ArrayList<>());
            list.add(
               new MonsterDropEntry(
                  itemid, chance, rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getInt("questid"), dropperID, rs.getInt("individual") > 0
               )
            );
            ret.put(dropperID, list);
         }
      } catch (SQLException var60) {
         var60.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var62 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var65 = null;
            }
         } catch (SQLException var54) {
            return;
         }
      }

      for (Entry<Integer, List<MonsterDropEntry>> entry : ret.entrySet()) {
         try {
            Table newTable = new Table(String.valueOf(entry.getKey()));
            int i = 0;

            for (MonsterDropEntry item : entry.getValue()) {
               Table table = new Table(String.valueOf(i++));
               table.put("ItemID", String.valueOf(item.itemId));
               table.put("Chance", String.valueOf(item.chance));
               table.put("QuestID", String.valueOf(item.questid));
               table.put("Min", String.valueOf(item.Minimum));
               table.put("Max", String.valueOf(item.Maximum));
               table.put("Individual", String.valueOf(item.individual ? 1 : 0));
               newTable.putChild(table);
            }

            try {
               newTable.save(Paths.get("./data/Jin/Drops/" + entry.getKey() + ".data"));
            } catch (Exception var51) {
            }
         } catch (NumberFormatException var58) {
         }
      }

      ps = null;
      rs = null;
      List<MonsterGlobalDropEntry> list = new ArrayList<>();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM drop_data_global");
         rs = ps.executeQuery();

         while (rs.next()) {
            list.add(
               new MonsterGlobalDropEntry(
                  rs.getInt("itemid"),
                  rs.getInt("chance"),
                  rs.getInt("continent"),
                  rs.getByte("dropType"),
                  rs.getInt("minimum_quantity"),
                  rs.getInt("maximum_quantity"),
                  rs.getInt("questid"),
                  rs.getInt("individual") > 0
               )
            );
         }
      } catch (SQLException var56) {
         var56.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var64 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var67 = null;
            }
         } catch (SQLException var53) {
            return;
         }
      }

      int i = 0;
      Table newTable = new Table("Global");

      for (MonsterGlobalDropEntry item : list) {
         try {
            Table table = new Table(String.valueOf(i++));
            table.put("ItemID", String.valueOf(item.itemId));
            table.put("Chance", String.valueOf(item.chance));
            table.put("QuestID", String.valueOf(item.questid));
            table.put("Min", String.valueOf(item.Minimum));
            table.put("Max", String.valueOf(item.Maximum));
            table.put("DropType", String.valueOf(item.dropType));
            table.put("Continent", String.valueOf(item.continent));
            table.put("Individual", String.valueOf(item.individual ? 1 : 0));
            newTable.putChild(table);
         } catch (NumberFormatException var52) {
         }
      }

      try {
         newTable.save(Paths.get("./data/Jin/Drops/Global/Global.data"));
      } catch (Exception var48) {
      }
   }
}
