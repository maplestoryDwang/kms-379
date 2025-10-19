package logging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LoggingEntry {
   private LoggingType loggingType;
   private String accountName;
   private String playerName;
   private int playerID;
   private int accountID;
   private StringBuilder log;
   private long time;

   public LoggingEntry(LoggingType loggingType, String accountName, String playerName, int playerID, int accountID, StringBuilder log, long time) {
      this.loggingType = loggingType;
      this.accountName = accountName;
      this.playerName = playerName;
      this.playerID = playerID;
      this.accountID = accountID;
      this.log = log;
      this.time = time;
   }

   public LoggingType getLoggingType() {
      return this.loggingType;
   }

   public void setLoggingType(LoggingType loggingType) {
      this.loggingType = loggingType;
   }

   public String getAccountName() {
      return this.accountName;
   }

   public void setAccountName(String accountName) {
      this.accountName = accountName;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public void setPlayerName(String playerName) {
      this.playerName = playerName;
   }

   public StringBuilder getLog() {
      return this.log;
   }

   public void setLog(StringBuilder log) {
      this.log = log;
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`name`, `account_name`, `player_id`, `account_id`, `log`, `time`) VALUES (?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setString(1, this.getPlayerName());
         ps.setString(2, this.getAccountName());
         ps.setInt(3, this.getPlayerID());
         ps.setInt(4, this.getAccountID());
         ps.setString(5, this.getLog().toString());
         ps.setTimestamp(6, new Timestamp(this.getTime()));
         ps.executeQuery();
      } catch (SQLException var12) {
         var12.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var14 = null;
            }
         } catch (SQLException var11) {
         }
      }
   }

   public int getPlayerID() {
      return this.playerID;
   }

   public void setPlayerID(int playerID) {
      this.playerID = playerID;
   }

   public int getAccountID() {
      return this.accountID;
   }

   public void setAccountID(int accountID) {
      this.accountID = accountID;
   }
}
