package network.center;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WeeklyItemManager {
   private static List<WeeklyItemEntry> weeklyItemList = new ArrayList<>();

   public static void loadWeeklyItems() {
      weeklyItemList.clear();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM `weekly_items`");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            weeklyItemList.add(
               new WeeklyItemEntry(
                  WeeklyItemEntry.ItemType.get(rs.getInt("item_type")),
                  rs.getLong("SN"),
                  rs.getInt("item_id"),
                  rs.getInt("item_count"),
                  rs.getInt("remain_count"),
                  rs.getTimestamp("start_time").getTime(),
                  rs.getTimestamp("end_time").getTime()
               )
            );
         }

         rs.close();
         ps.close();
         System.out.println("페어리 브로의 황금상자 정보를 로드하였습니다.");
      } catch (SQLException var5) {
      }
   }

   public static synchronized void saveWeeklyItems() {
      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("DELETE FROM `weekly_items`");
         ps.executeUpdate();
         ps.close();

         for (WeeklyItemEntry entry : weeklyItemList) {
            ps = con.prepareStatement(
               "INSERT INTO `weekly_items` (`SN`, `item_type`, `item_id`, `item_count`, `remain_count`, `start_time`, `end_time`) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setLong(1, entry.getSN());
            ps.setInt(2, entry.getItemType().getType());
            ps.setInt(3, entry.getItemID());
            ps.setInt(4, entry.getItemQuantity());
            ps.setInt(5, entry.getRemainCount());
            ps.setTimestamp(6, new Timestamp(entry.getStartTime()));
            ps.setTimestamp(7, new Timestamp(entry.getEndTime()));
            ps.executeUpdate();
            ps.close();
         }
      } catch (SQLException var6) {
      }
   }

   public static List<WeeklyItemEntry> getBonusItems() {
      List<WeeklyItemEntry> ret = new ArrayList<>();
      long now = System.currentTimeMillis();

      for (WeeklyItemEntry entry : weeklyItemList) {
         if (now >= entry.getStartTime() && now <= entry.getEndTime() && entry.getItemType() == WeeklyItemEntry.ItemType.BonusItem) {
            ret.add(entry);
         }
      }

      return ret;
   }

   public static List<WeeklyItemEntry> getExtremeItems() {
      List<WeeklyItemEntry> ret = new ArrayList<>();
      long now = System.currentTimeMillis();

      for (WeeklyItemEntry entry : weeklyItemList) {
         if (now >= entry.getStartTime() && now <= entry.getEndTime() && entry.getItemType() == WeeklyItemEntry.ItemType.ExtremeItem) {
            ret.add(entry);
         }
      }

      return ret;
   }

   public static boolean canBuyWeeklyItems() {
      long now = System.currentTimeMillis();
      int count = 0;

      for (WeeklyItemEntry entry : weeklyItemList) {
         if (now >= entry.getStartTime() && now <= entry.getEndTime() && entry.getRemainCount() > 0) {
            count++;
         }
      }

      return count > 0;
   }
}
