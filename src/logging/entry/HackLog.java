package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class HackLog extends LoggingEntry {
   private int type;

   public HackLog(int type, MapleCharacter player, StringBuilder log) {
      super(LoggingType.HackLog, player.getClient().getAccountName(), player.getName(), player.getId(), player.getAccountID(), log, System.currentTimeMillis());
      this.type = type;
   }

   public HackLog(int type, int accID, String accName, StringBuilder log) {
      super(LoggingType.HackLog, accName, accName, accID, accID, log, System.currentTimeMillis());
      this.type = type;
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
         ps.setByte(6, (byte)this.type);
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
}
