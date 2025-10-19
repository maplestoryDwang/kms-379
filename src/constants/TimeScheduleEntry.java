package constants;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TimeScheduleEntry {
   private int praiseRankCheck1 = 0;
   private int praiseRankCheck2 = 0;
   private int weekQuestCheck = 0;
   private int dojangRankCheck1 = 0;
   private int dojangRankCheck2 = 0;
   private int dojangRankCheck3 = 0;
   private int dojangRankCheck4 = 0;
   private int dreamBreakerRankCheck = 0;
   private int hottimeCheck = 0;
   private int dailyGiftCheck = 0;
   private int dailyEventCheck1 = 0;
   private int dailyEventCheck2 = 0;
   private int specialHottimeCheck = 0;
   private boolean change = false;

   public void load() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `check_time_schedule`");
         rs = ps.executeQuery();

         while (rs.next()) {
            String key = rs.getString("key");
            int value = rs.getInt("value");
            if (key.equals("praise_rank_check_1")) {
               this.praiseRankCheck1 = value;
            } else if (key.equals("praise_rank_check_2")) {
               this.praiseRankCheck2 = value;
            } else if (key.equals("week_quest_check")) {
               this.weekQuestCheck = value;
            } else if (key.equals("dojang_rank_check_1")) {
               this.dojangRankCheck1 = value;
            } else if (key.equals("dojang_rank_check_2")) {
               this.dojangRankCheck2 = value;
            } else if (key.equals("dojang_rank_check_3")) {
               this.dojangRankCheck3 = value;
            } else if (key.equals("dojang_rank_check_4")) {
               this.dojangRankCheck4 = value;
            } else if (key.equals("dream_breaker_rank_check")) {
               this.dreamBreakerRankCheck = value;
            } else if (key.equals("hottime_check")) {
               this.hottimeCheck = value;
            } else if (key.equals("daily_gift_check")) {
               this.dailyGiftCheck = value;
            } else if (key.equals("daily_event_cehck_1")) {
               this.dailyEventCheck1 = value;
            } else if (key.equals("daily_event_check_2")) {
               this.dailyEventCheck2 = value;
            } else if (key.equals("special_hottime_check")) {
               this.setSpecialHottimeCheck(value);
            }
         }

         System.out.println("특정 시간 발생 이벤트 체크 데이터를 불러왔습니다.");
      } catch (SQLException var17) {
         throw new RuntimeException("[오류] DB 연결에 실패하였습니다.");
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

   public void save() {
      PreparedStatement ps = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("DELETE FROM `check_time_schedule`");
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "praise_rank_check_1");
         ps.setInt(2, this.praiseRankCheck1);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "praise_rank_check_2");
         ps.setInt(2, this.praiseRankCheck2);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "week_quest_check");
         ps.setInt(2, this.weekQuestCheck);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "dojang_rank_check_1");
         ps.setInt(2, this.dojangRankCheck1);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "dojang_rank_check_2");
         ps.setInt(2, this.dojangRankCheck2);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "dojang_rank_check_3");
         ps.setInt(2, this.dojangRankCheck3);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "dojang_rank_check_4");
         ps.setInt(2, this.dojangRankCheck4);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "dream_breaker_rank_check");
         ps.setInt(2, this.dreamBreakerRankCheck);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "hottime_check");
         ps.setInt(2, this.hottimeCheck);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "daily_gift_check");
         ps.setInt(2, this.dailyGiftCheck);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "daily_event_check_1");
         ps.setInt(2, this.dailyEventCheck1);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "daily_event_check_2");
         ps.setInt(2, this.dailyEventCheck2);
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("INSERT INTO `check_time_schedule` (`key`, `value`) VALUES (?, ?)");
         ps.setString(1, "special_hottime_check");
         ps.setInt(2, this.getSpecialHottimeCheck());
         ps.executeUpdate();
         ps.close();
         System.out.println("특정 시간 발생 이벤트 체크 데이터를 저장했습니다.");
      } catch (SQLException var16) {
         throw new RuntimeException("[오류] DB 연결에 실패하였습니다.");
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var18 = null;
            }
         } catch (SQLException var13) {
         }
      }
   }

   public int getPraiseRankCheck1() {
      return this.praiseRankCheck1;
   }

   public void setPraiseRankCheck1(int praiseRankCheck1) {
      this.praiseRankCheck1 = praiseRankCheck1;
   }

   public int getPraiseRankCheck2() {
      return this.praiseRankCheck2;
   }

   public void setPraiseRankCheck2(int praiseRankCheck2) {
      this.praiseRankCheck2 = praiseRankCheck2;
   }

   public int getWeekQuestCheck() {
      return this.weekQuestCheck;
   }

   public void setWeekQuestCheck(int weekQuestCheck) {
      this.weekQuestCheck = weekQuestCheck;
   }

   public int getDojangRankCheck1() {
      return this.dojangRankCheck1;
   }

   public void setDojangRankCheck1(int dojangRankCheck1) {
      this.dojangRankCheck1 = dojangRankCheck1;
   }

   public int getDojangRankCheck2() {
      return this.dojangRankCheck2;
   }

   public void setDojangRankCheck2(int dojangRankCheck2) {
      this.dojangRankCheck2 = dojangRankCheck2;
   }

   public int getDojangRankCheck3() {
      return this.dojangRankCheck3;
   }

   public void setDojangRankCheck3(int dojangRankCheck3) {
      this.dojangRankCheck3 = dojangRankCheck3;
   }

   public int getDojangRankCheck4() {
      return this.dojangRankCheck4;
   }

   public void setDojangRankCheck4(int dojangRankCheck4) {
      this.dojangRankCheck4 = dojangRankCheck4;
   }

   public int getDreamBreakerRankCheck() {
      return this.dreamBreakerRankCheck;
   }

   public void setDreamBreakerRankCheck(int dreamBreakerRankCheck) {
      this.dreamBreakerRankCheck = dreamBreakerRankCheck;
   }

   public int getHottimeCheck() {
      return this.hottimeCheck;
   }

   public void setHottimeCheck(int hottimeCheck) {
      this.hottimeCheck = hottimeCheck;
   }

   public int getDailyGiftCheck() {
      return this.dailyGiftCheck;
   }

   public void setDailyGiftCheck(int dailyGiftCheck) {
      this.dailyGiftCheck = dailyGiftCheck;
   }

   public boolean isChange() {
      return this.change;
   }

   public void setChange(boolean change) {
      this.change = change;
   }

   public int getDailyEventCheck1() {
      return this.dailyEventCheck1;
   }

   public void setDailyEventCheck1(int dailyEventCheck1) {
      this.dailyEventCheck1 = dailyEventCheck1;
   }

   public int getDailyEventCheck2() {
      return this.dailyEventCheck2;
   }

   public void setDailyEventCheck2(int dailyEventCheck2) {
      this.dailyEventCheck2 = dailyEventCheck2;
   }

   public int getSpecialHottimeCheck() {
      return this.specialHottimeCheck;
   }

   public void setSpecialHottimeCheck(int specialHottimeCheck) {
      this.specialHottimeCheck = specialHottimeCheck;
   }
}
