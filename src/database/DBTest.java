package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBTest {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(4);
      int i = 0;

      while (true) {
         System.out.println(++i);

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name='설단'");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
               String name = rs.getString("name");
               PreparedStatement s = con.prepareStatement("UPDATE characters SET name='설단' WHERE name='설단'");
               s.executeUpdate();
            }
         } catch (SQLException var9) {
         }
      }
   }
}
