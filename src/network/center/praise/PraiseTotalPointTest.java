package network.center.praise;

import database.DBConnection;
import database.DBEventManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PraiseTotalPointTest {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(4);

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE `praise_point` SET `total_point` = 0");
         ps.executeUpdate();
         ps.close();
         String sql = "*SELECT rank1 AS total_point, to_account_id\nFROM (\n  SELECT *\n  FROM\n  (\n    select COUNT(*) rank1, \n      to_account_id FROM praise_point_log WHERE praise_time > \"2022-06-01 00:00:00\" and praise_time < \"2022-07-01 00:00:00\" GROUP BY to_account_id\n  ) _to\n  \n) a ORDER BY total_point DESC";
         ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            int count = rs.getInt("total_point");
            int accountID = rs.getInt("to_account_id");
            int praiseCount = 0;
            PreparedStatement ps2 = con.prepareStatement("SELECT COUNT(*) AS t FROM `praise_point_log` WHERE from_account_id = ?");
            ps2.setInt(1, accountID);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
               praiseCount = rs2.getInt("t");
            }

            int totalPoint = praiseCount * 500 + count * 1000;
            rs2.close();
            ps2.close();
            PreparedStatement ps3 = con.prepareStatement("UPDATE `praise_point` SET `total_point` = ? WHERE `account_id` = ?");
            ps3.setInt(1, totalPoint);
            ps3.setInt(2, accountID);
            ps3.executeUpdate();
            ps3.close();
         }

         rs.close();
         ps.close();
      } catch (SQLException var15) {
      }
   }
}
