package objects.context;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class DailyGift {
   private int dailyCount;
   private int dailyDay;
   private String data;
   Calendar cal = Calendar.getInstance();
   int Month = this.cal.get(2) + 1;

   public int getDailyCount() {
      return this.dailyCount;
   }

   public void setDailyCount(int count) {
      this.dailyCount = count;
   }

   public int getDailyDay() {
      return this.dailyDay;
   }

   public void setDailyDay(int day) {
      this.dailyDay = day;
   }

   public String getDailyData() {
      return this.data;
   }

   public void setDailyData(String data) {
      this.data = data;
   }

   public boolean checkDailyGift(int accountid) {
      PreparedStatement query = null;
      ResultSet result = null;
      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            query = con.prepareStatement("SELECT * FROM dailyGift WHERE accountid = ?");
            query.setInt(1, accountid);
            result = query.executeQuery();
            if (result.next()) {
               return true;
            }
         } catch (SQLException var21) {
            var21.printStackTrace();
         }

         return false;
      } finally {
         try {
            if (query != null) {
               query.close();
            }

            if (result != null) {
               result.close();
            }
         } catch (SQLException var18) {
            var18.printStackTrace();
         }
      }
   }

   public void InsertDailyData(int accountid, int dailyDay, int dailyCount) {
      PreparedStatement query = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         query = con.prepareStatement("INSERT INTO dailyGift(accountid, daily_day, daily_count) VALUES (?, ?, ?)", 1);
         query.setInt(1, accountid);
         query.setInt(2, dailyDay);
         query.setInt(3, dailyCount);
         query.executeUpdate();
         query.close();
      } catch (SQLException var21) {
         var21.printStackTrace();
      } finally {
         try {
            if (query != null) {
               query.close();
            }
         } catch (SQLException var18) {
            var18.printStackTrace();
         }
      }
   }

   public void loadDailyGift(int accountid) {
      PreparedStatement query = null;
      ResultSet result = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         query = con.prepareStatement("SELECT * FROM dailyGift WHERE accountid = ?");
         query.setInt(1, accountid);
         result = query.executeQuery();
         if (result.next()) {
            this.dailyDay = result.getInt("daily_day");
            this.dailyCount = result.getInt("daily_count");
            this.data = result.getString("daily_data");
         }

         result.close();
         query.close();
      } catch (SQLException var20) {
         var20.printStackTrace();
      } finally {
         try {
            if (query != null) {
               query.close();
            }

            if (result != null) {
               result.close();
            }
         } catch (SQLException var17) {
            var17.printStackTrace();
         }
      }
   }

   public void resetDailyGift(int accountid) {
      PreparedStatement query = null;
      ResultSet result = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         query = con.prepareStatement("SELECT * FROM dailyGift WHERE accountid = ?");
         query.setInt(1, accountid);
         result = query.executeQuery();
         if (result.next()) {
            query = con.prepareStatement("UPDATE dailyGift SET daily_day = ?, daily_count = ?, daily_data = ? WHERE accountid = ?");
            query.setInt(1, this.getDailyDay());
            query.setInt(2, 0);
            query.setString(
               3,
               this.cal.get(1)
                  + (this.Month != 10 && this.Month != 11 && this.Month != 12 ? "0" : "")
                  + this.Month
                  + (this.cal.get(5) < 10 ? "0" : "")
                  + this.cal.get(5)
            );
            query.setInt(4, accountid);
            query.executeUpdate();
         }

         result.close();
         query.close();
      } catch (SQLException var20) {
         var20.printStackTrace();
      } finally {
         try {
            if (query != null) {
               query.close();
            }

            if (result != null) {
               result.close();
            }
         } catch (SQLException var17) {
            var17.printStackTrace();
         }
      }
   }

   public void saveDailyGift(int accountid, int dailyDay, int dailyCount, String dailyData) {
      PreparedStatement query = null;
      ResultSet result = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         query = con.prepareStatement("SELECT * FROM dailyGift WHERE accountid = ?");
         query.setInt(1, accountid);
         result = query.executeQuery();
         if (result.next()) {
            query = con.prepareStatement("UPDATE dailyGift SET daily_day = ?, daily_count = ?, daily_data = ? WHERE accountid = ?");
            query.setInt(1, dailyDay);
            query.setInt(2, dailyCount);
            query.setString(3, dailyData);
            query.setInt(4, accountid);
            query.executeUpdate();
         }

         result.close();
         query.close();
      } catch (SQLException var23) {
         var23.printStackTrace();
      } finally {
         try {
            if (query != null) {
               query.close();
            }

            if (result != null) {
               result.close();
            }
         } catch (SQLException var20) {
            var20.printStackTrace();
         }
      }
   }
}
