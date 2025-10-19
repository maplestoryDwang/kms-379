package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;

public class DonationLog extends LoggingEntry {
   private String price;

   public DonationLog(String accountName, String price, StringBuilder log, String playerName, int playerId, int accountId) {
      super(LoggingType.DonationLog, accountName, playerName, playerId, accountId, log, System.currentTimeMillis());
      this.setPrice(price);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`price`, `log`, `account_name`, `player_id`, `account_id`, `time`) VALUES (?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setString(1, this.getPrice());
         ps.setString(2, this.getLog().toString());
         ps.setString(3, this.getAccountName());
         ps.setInt(4, this.getPlayerID());
         ps.setInt(5, this.getAccountID());
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

   public String getPrice() {
      return this.price;
   }

   public void setPrice(String price) {
      this.price = price;
   }
}
