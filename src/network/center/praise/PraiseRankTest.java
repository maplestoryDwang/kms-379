package network.center.praise;

import database.DBConnection;
import database.DBEventManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class PraiseRankTest {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(4);

      try (Connection con = DBConnection.getConnection()) {
         Map<String, Long> list = new HashMap<>();
         String sql = "SELECT `name`, `meso` FROM `praise_donation_meso_log` WHERE time > \"2023-02-01 00:00:00\" and time < \"2023-03-01 00:00:00\"";
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            String name = rs.getString("name");
            long meso = rs.getLong("meso");
            if (list.containsKey(name)) {
               meso += list.getOrDefault(name, 0L);
            }

            list.put(name, meso);
         }

         list = list.entrySet()
            .stream()
            .sorted(Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
         int rank = 1;

         for (Entry<String, Long> a : list.entrySet()) {
            String name = a.getKey();
            long meso = a.getValue();
            PreparedStatement ps2 = con.prepareStatement("SELECT `accountid` FROM `characters` WHERE `name` = ?");
            ps2.setString(1, name);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
               int accountID = rs2.getInt("accountid");
               System.out.println(rank++ + "위 " + name + "(accountID : " + accountID + ") " + meso);
            }
         }
      } catch (SQLException var17) {
      }
   }
}
