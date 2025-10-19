package objects.users.achievement;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AchievementRank {
   private static Map<Integer, AchievementRank.AchievementRankEntry> lastWeekRanks = new HashMap<>();

   public static void main(String[] args) {
   }

   public static void loadLastWeekRank() {
      lastWeekRanks.clear();
      PreparedStatement ps = null;
      ResultSet rs = null;

      try {
         Connection con = DBConnection.getConnection();
         if (con != null) {
            con.close();
         }
      } catch (SQLException var11) {
         System.out.println("LastWeek Load Err");
         var11.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var13 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var14 = null;
            }
         } catch (SQLException var10) {
         }
      }
   }

   public static class AchievementRankEntry {
      private int score;
      private int accountID;

      public AchievementRankEntry(int score, int accountID) {
         this.setScore(score);
         this.setAccountID(accountID);
      }

      public int getScore() {
         return this.score;
      }

      public void setScore(int score) {
         this.score = score;
      }

      public int getAccountID() {
         return this.accountID;
      }

      public void setAccountID(int accountID) {
         this.accountID = accountID;
      }
   }
}
