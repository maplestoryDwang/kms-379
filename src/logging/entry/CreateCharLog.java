package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;

public class CreateCharLog extends LoggingEntry {
   private int type;

   public CreateCharLog(String name, String accountName, int plyerID, int accountID, int type, StringBuilder log) {
      super(LoggingType.CreateCharLog, accountName, name, plyerID, accountID, log, System.currentTimeMillis());
      this.setType(type);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`name`, `account_name`, `player_id`, `account_id`, `log`, `type`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setString(1, this.getPlayerName());
         ps.setString(2, this.getAccountName());
         ps.setInt(3, this.getPlayerID());
         ps.setInt(4, this.getAccountID());
         ps.setString(5, this.getLog().toString());
         ps.setByte(6, (byte)this.getType());
         ps.setTimestamp(7, new Timestamp(this.getTime()));
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

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }
}
