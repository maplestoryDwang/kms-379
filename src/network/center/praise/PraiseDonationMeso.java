package network.center.praise;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import objects.users.MapleCharacter;

public class PraiseDonationMeso {
   static long totalMeso = 0L;
   static List<PraiseDonationMesoLog> donationLogList = new ArrayList<>();

   public static void loadData() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `praise_donation_meso`");
         rs = ps.executeQuery();

         while (rs.next()) {
            totalMeso = rs.getLong("total_meso");
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT * FROM `praise_donation_meso_log`");
         rs = ps.executeQuery();

         while (rs.next()) {
            String name = rs.getString("name");
            int accountID = rs.getInt("account_id");
            long meso = rs.getLong("meso");
            long time = rs.getTimestamp("time").getTime();
            donationLogList.add(new PraiseDonationMesoLog(name, accountID, meso, time));
         }
      } catch (SQLException var21) {
         var21.printStackTrace();
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
            var18.printStackTrace();
         }
      }

      System.out.println("칭찬 뉴비 기부함 데이터를 로드하였습니다.");
   }

   public static void doDonationMeso(MapleCharacter player, int accountID, long meso) {
      String name = player.getName();
      PraiseDonationMeso.totalMeso += meso;
      donationLogList.add(new PraiseDonationMesoLog(name, accountID, meso, System.currentTimeMillis()));
      PraiseDonationMesoRankEntry entry = PraiseDonationMesoRank.getRank(accountID);
      PraiseDonationMesoRank.putRank(name, accountID, entry.getTotalMeso() + meso);
      PreparedStatement ps = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("INSERT INTO `praise_donation_meso_log` (`name`, `account_id`, `meso`, `time`) VALUES (?, ?, ?, ?)");
         ps.setString(1, name);
         ps.setInt(2, accountID);
         ps.setLong(3, meso);
         ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
         ps.executeQuery();
         ps.close();
      } catch (SQLException var22) {
         var22.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var24 = null;
            }
         } catch (SQLException var19) {
            var19.printStackTrace();
         }
      }

      long totalMeso = getTotalMeso(accountID);
      int d = (int)(totalMeso / 600000000L);
      player.updateOneInfo(1211345, "reward", String.valueOf(d));
   }

   public static long getTotalMeso(int accountID) {
      long totalMeso = 0L;

      for (PraiseDonationMesoLog e : new ArrayList<>(donationLogList)) {
         if (e.getAccountID() == accountID) {
            totalMeso += e.getMeso();
         }
      }

      return totalMeso;
   }

   public static void saveLogs() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("DELETE FROM `praise_donation_meso`");
         ps.executeQuery();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `praise_donation_meso` (`total_meso`) VALUES (?)");
         ps.setLong(1, totalMeso);
         ps.executeQuery();
         ps.close();
      } catch (SQLException var17) {
         var17.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var19 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var20 = null;
            }
         } catch (SQLException var14) {
            var14.printStackTrace();
         }
      }

      System.out.println("칭찬 뉴비 기부함 모든 기록이 저장되었습니다.");
   }

   public static long getTotalMeso() {
      return totalMeso;
   }

   public static void addTotalMeso(long meso) {
      totalMeso += meso;
   }
}
