package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;

public class ConnectLog extends LoggingEntry {
   int type;
   String mac;
   String volume;

   public ConnectLog(String name, String accountName, int playerID, int accountID, int type, String mac, String volume, StringBuilder log) {
      super(LoggingType.ConnectLog, accountName, name, playerID, accountID, log, System.currentTimeMillis());
      this.setType(type);
      this.setMac(mac);
      this.setVolume(volume);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`name`, `account_name`, `player_id`, `account_id`, `mac`, `volume`, `log`, `type`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setString(1, this.getPlayerName());
         ps.setString(2, this.getAccountName());
         ps.setInt(3, this.getPlayerID());
         ps.setInt(4, this.getAccountID());
         ps.setString(5, this.getMac());
         ps.setString(6, this.getVolume());
         ps.setString(7, this.getLog().toString());
         ps.setByte(8, (byte)this.getType());
         ps.setTimestamp(9, new Timestamp(this.getTime()));
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

   public String getMac() {
      return this.mac;
   }

   public String getVolume() {
      return this.volume;
   }

   public void setType(int type) {
      this.type = type;
   }

   public void setMac(String mac) {
      this.mac = mac;
   }

   public void setVolume(String volume) {
      this.volume = volume;
   }
}
