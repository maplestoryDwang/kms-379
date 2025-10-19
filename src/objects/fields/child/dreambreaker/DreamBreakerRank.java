package objects.fields.child.dreambreaker;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DreamBreakerRank {
   public static Map<String, Integer> rank = new HashMap<>();

   public static void loadRank() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `dream_breaker_rank`");
         rs = ps.executeQuery();

         while (rs.next()) {
            String Name = rs.getString("name");
            int rankPoint = rs.getInt("best_stage") * 1000 + (180 - rs.getInt("best_time"));
            rank.put(Name, rankPoint);
         }

         rank = sortByValue(rank);
         System.out.println("드림브레이커 랭킹이 로드되었습니다.");
      } catch (SQLException var17) {
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
         }
      }
   }

   public static void clearRank() {
      rank.clear();
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("DELETE FROM `dream_breaker_rank`");
         ps.executeQuery();
      } catch (SQLException var17) {
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
         }
      }
   }

   public static void saveRank() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         for (Entry<String, Integer> entry : rank.entrySet()) {
            ps = con.prepareStatement("SELECT * FROM `dream_breaker_rank` WHERE `name` = ?");
            int stage = entry.getValue() / 1000;
            int time = 180 - entry.getValue() % 1000;
            ps.setString(1, entry.getKey());
            rs = ps.executeQuery();
            if (rs.next()) {
               ps = con.prepareStatement("UPDATE `dream_breaker_rank` SET `best_stage` = ?, `best_time` = ? WHERE `name` = ?");
               ps.setInt(1, stage);
               ps.setInt(2, time);
               ps.setString(3, entry.getKey());
               ps.executeUpdate();
               ps.close();
            } else {
               saveNewRecord(con, ps, entry.getKey(), stage, time);
            }
         }
      } catch (SQLException var19) {
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var21 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var22 = null;
            }
         } catch (SQLException var16) {
         }
      }
   }

   public static void saveNewRecord(Connection con, PreparedStatement ps, String name, int stage, int time) {
      try {
         ps = con.prepareStatement("INSERT INTO `dream_breaker_rank` (`name`, best_stage, best_time) VALUES (?, ?, ?)");
         ps.setString(1, name);
         ps.setInt(2, stage);
         ps.setInt(3, time);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var6) {
         var6.printStackTrace();
      }
   }

   public static void editRecord(String name, long stage, long time) {
      rank.put(name, (int)stage * 1000 + (180 - (int)time));
      rank = sortByValue(rank);
      saveRank();
   }

   public static int getRank(String name) {
      int index = 1;
      if (!rank.containsKey(name)) {
         return 0;
      } else {
         for (Entry<String, Integer> info : rank.entrySet()) {
            if (info.getKey().equals(name)) {
               break;
            }

            index++;
         }

         return index;
      }
   }

   public static Map<String, Integer> sortByValue(Map<String, Integer> wordCounts) {
      return wordCounts.entrySet()
         .stream()
         .sorted(Entry.<String, Integer>comparingByValue().reversed())
         .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
   }

   public static String getRanker(int r) {
      int ra = 1;

      for (Entry<String, Integer> info : rank.entrySet()) {
         if (ra == r) {
            return info.getKey();
         }

         ra++;
      }

      return "";
   }
}
