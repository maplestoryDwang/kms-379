package objects.utils;

import database.DBConnection;
import database.DBEventManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DonationRanker {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(6);
      Map<String, Integer> checkList = new HashMap<>();
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `donation_log`");
         rs = ps.executeQuery();
         String accountName = "";

         while (rs.next()) {
            accountName = rs.getString("account");
            Integer v = checkList.get(accountName);
            int value = 0;
            if (v != null) {
               value = v;
            }

            String p = rs.getString("price").replace(",", "");
            value += Integer.parseInt(p);
            checkList.put(accountName, value);
         }

         AtomicInteger ai = new AtomicInteger(1);
         AtomicLong al = new AtomicLong(0L);
         checkList.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).forEach(a -> {
            String name = "";
            int accID = 0;

            try (Connection con_ = DBConnection.getConnection()) {
               PreparedStatement ps2 = con_.prepareStatement("SELECT `id` FROM `accounts` WHERE `name` = ? ");
               ps2.setString(1, a.getKey());
               ResultSet rs2 = ps2.executeQuery();

               while (rs2.next()) {
                  accID = rs2.getInt("id");
               }

               rs2.close();
               ps2.close();
               PreparedStatement ps3 = con_.prepareStatement("SELECT `name` FROM `characters` WHERE `accountid` = ? ORDER BY `level` DESC");
               ps3.setInt(1, accID);
               ResultSet rs3 = ps3.executeQuery();
               if (rs3.next()) {
                  name = rs3.getString("name");
               }

               rs3.close();
               ps3.close();
            } catch (SQLException var12) {
            }

            System.out.println(ai.get() + "위, " + a.getKey() + "(" + accID + ") : " + a.getValue() + " 대표 캐릭터 : " + name);
            al.set(al.get() + a.getValue().intValue());
            ai.set(ai.get() + 1);
         });
         System.out.println("총 금액 : " + al.get());
      } catch (SQLException var21) {
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var23 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var24 = null;
            }
         } catch (SQLException var18) {
         }
      }
   }
}
