package objects.utils;

import database.DBConnection;
import database.DBEventManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuckingPetUniqueID {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(5);
      DBConnection db = new DBConnection();
      PreparedStatement ps = null;
      ResultSet rs = null;
      HashMap<Long, List<Long>> pets = new HashMap<>();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `inventoryitems` WHERE itemid > 4999999 && itemid < 5100000");
         rs = ps.executeQuery();

         while (rs.next()) {
            long uniqueID = rs.getLong("uniqueid");
            long inventoryitemid = rs.getLong("inventoryitemid");
            pets.putIfAbsent(uniqueID, new ArrayList<>());
            pets.get(uniqueID).add(inventoryitemid);
         }
      } catch (Throwable var48) {
         System.out.println("PetUnique Err");
         var48.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }

            if (rs != null) {
               rs.close();
            }
         } catch (Exception var41) {
            System.out.println("PetUnique2 Err");
            var41.printStackTrace();
         }
      }

      for (Long a : pets.keySet()) {
         if (pets.get(a).size() >= 2) {
            for (int i = 0; i < pets.get(a).size(); i++) {
               if (i != 0) {
                  try (Connection con = DBConnection.getConnection()) {
                     ps = con.prepareStatement("DELETE FROM inventoryitems WHERE inventoryitemid = ?");
                     ps.setLong(1, pets.get(a).get(i));
                     rs = ps.executeQuery();
                  } catch (Throwable var45) {
                     System.out.println("PetUnique3 Err");
                     var45.printStackTrace();
                  } finally {
                     try {
                        if (ps != null) {
                           ps.close();
                        }

                        if (rs != null) {
                           rs.close();
                        }
                     } catch (Exception var40) {
                        System.out.println("PetUnique4 Err");
                        var40.printStackTrace();
                     }
                  }
               }
            }
         }
      }

      System.out.println("완료됐당께");
   }
}
