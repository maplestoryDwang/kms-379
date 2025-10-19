package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import database.callback.DBCallback;
import database.callback.DBGenerateKeyCallback;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DBConnection {
   private static HikariDataSource dataSource;
   public static final int CLOSE_CURRENT_RESULT = 1;
   public static final int KEEP_CURRENT_RESULT = 2;
   public static final int CLOSE_ALL_RESULTS = 3;
   public static final int SUCCESS_NO_INFO = -2;
   public static final int EXECUTE_FAILED = -3;
   public static final int RETURN_GENERATED_KEYS = 1;
   public static final int NO_GENERATED_KEYS = 2;

   public static synchronized void init() {
      try {
         dataSource = setupDataSource(DBConfig.DB_URL);
      } catch (Exception var1) {
         System.err.println("데이터베이스를 연결하는 과정에서 오류가 발생하였습니다.2\r\n" + var1.toString());
         System.exit(1);
      }

      System.out.println("데이터베이스와 연결되었습니다.");
   }

   private static HikariDataSource setupDataSource(String URL) throws Exception {
      HikariConfig config = new HikariConfig();
      config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
      config.addDataSourceProperty("url", URL);
      config.addDataSourceProperty("user", "root");
      config.addDataSourceProperty("password", DBConfig.DB_PASSWORD);
      config.setMinimumIdle(10);
      config.setMaximumPoolSize(5000);
      config.setConnectionTimeout(3000L);
      config.setMaxLifetime(298000L);
      config.setIdleTimeout(TimeUnit.MINUTES.toMillis(30L));
      return new HikariDataSource(config);
   }

   public static synchronized void shutdown() {
      try {
         dataSource.close();
      } catch (Exception var1) {
         System.err.println("데이터베이스를 종료하는 과정에서 오류가 발생하였습니다!\r\n" + var1);
      }

      dataSource = null;
   }

   public static Connection getConnection() {
      try {
         return dataSource.getConnection();
      } catch (SQLException var3) {
         System.err.println("데이터베이스에 연결할 수 없습니다!" + var3.getMessage());

         try {
            return dataSource.getConnection();
         } catch (SQLException var2) {
            return null;
         }
      }
   }

   public static void insertOrUpdate(String query, DBCallback callback) {
      DBConnection db = new DBConnection();
      PreparedStatement ps = null;
      Connection con = null;

      try {
         con = getConnection();
         con.setTransactionIsolation(1);
         con.setAutoCommit(false);
         ps = con.prepareStatement(query);
         if (callback != null) {
            callback.execute(ps);
            ps.executeUpdate();
            con.commit();
         }
      } catch (SQLException var14) {
         System.out.println("DB InsertOrUpdate 작업중 오류가 발생하였습니다. : " + var14.getSQLState());
         var14.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var16 = null;
            }

            if (con != null) {
               con.setAutoCommit(true);
               con.setTransactionIsolation(4);
               con.close();
               Connection var17 = null;
            }
         } catch (SQLException var13) {
         }
      }
   }

   public static void insertOrUpdateBatch(String query, DBCallback callback) {
      DBConnection db = new DBConnection();
      PreparedStatement ps = null;
      Connection con = null;

      try {
         con = getConnection();
         con.setTransactionIsolation(1);
         con.setAutoCommit(false);
         ps = con.prepareStatement(query);
         if (callback != null) {
            callback.execute(ps);
            ps.executeBatch();
            con.commit();
         }
      } catch (SQLException var14) {
         System.out.println("DB InsertOrUpdateBatch 작업중 오류가 발생하였습니다. : " + var14.getSQLState());
         System.out.println("해당쿼리 : " + query);
         var14.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var16 = null;
            }

            if (con != null) {
               con.setAutoCommit(true);
               con.setTransactionIsolation(4);
               con.close();
               Connection var17 = null;
            }
         } catch (SQLException var13) {
         }
      }
   }

   public static void insertOrUpdateGenerateKey(String query, DBGenerateKeyCallback after) {
      DBConnection db = new DBConnection();
      PreparedStatement ps = null;
      ResultSet rs = null;
      Connection con = null;

      try {
         con = getConnection();
         con.setTransactionIsolation(1);
         con.setAutoCommit(false);
         ps = con.prepareStatement(query, 1);
         if (after != null) {
            after.execute(ps);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            after.afterAction(ps, rs);
            con.commit();
         }
      } catch (SQLException var15) {
         System.out.println("DB InsertOrUpdate 작업중 오류가 발생하였습니다. : " + var15.getSQLState());
         var15.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var17 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var18 = null;
            }

            if (con != null) {
               con.setAutoCommit(true);
               con.setTransactionIsolation(4);
               con.close();
               Connection var19 = null;
            }
         } catch (SQLException var14) {
         }
      }
   }
}
