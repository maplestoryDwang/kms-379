package objects.users;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MailBox {
   public static int getCid(String name) {
      int ret = 0;

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name = ?");
         ps.setString(1, name);
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            ret = rs.getInt("id");
         }

         ps.close();
         rs.close();
      } catch (SQLException var7) {
         var7.printStackTrace();
      }

      return ret;
   }

   public static void removeItemOff(int chr) {
      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM offline WHERE chrid = ?");
         ps.setInt(1, chr);
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            ps = con.prepareStatement("UPDATE offline SET `status` = 1 WHERE chrid = ?");
            ps.setInt(1, chr);
            ps.execute();
         }

         ps.close();
         rs.close();
      } catch (SQLException var6) {
         var6.printStackTrace();
      }
   }

   public static boolean pickUpItemOff(MapleCharacter chr) {
      try {
         boolean var4;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM offline WHERE chrid = ? AND `status` = 0");
            ps.setInt(1, chr.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
               do {
                  chr.gainItem(rs.getInt("item"), rs.getInt("qua"));
               } while (rs.next());

               ps.close();
               rs.close();
               return true;
            }

            ps.close();
            rs.close();
            con.close();
            var4 = false;
         }

         return var4;
      } catch (SQLException var7) {
         var7.printStackTrace();
         return true;
      }
   }

   public static boolean isItemOff(MapleCharacter chr) {
      try {
         boolean var4;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM offline WHERE chrid = ? AND `status` = 0");
            ps.setInt(1, chr.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
               ps.close();
               rs.close();
               return true;
            }

            ps.close();
            rs.close();
            con.close();
            var4 = false;
         }

         return var4;
      } catch (SQLException var7) {
         var7.printStackTrace();
         return true;
      }
   }

   public static void sendItemOff(int chr, int item, int qua) {
      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement pse = con.prepareStatement("INSERT INTO offline (chrid, item, qua) VALUES (?, ?, ?)");
         pse.setInt(1, chr);
         pse.setInt(2, item);
         pse.setInt(3, qua);
         pse.executeUpdate();
         pse.close();
      } catch (SQLException var8) {
         var8.printStackTrace();
      }
   }
}
