package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class MacroLog extends LoggingEntry {
   private int type;
   private int result;

   public MacroLog(MapleCharacter player, int type, int result, StringBuilder log) {
      super(LoggingType.MacroLog, player.getClient().getAccountName(), player.getName(), player.getId(), player.getAccountID(), log, System.currentTimeMillis());
      this.setType(type);
      this.setResult(result);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`name`, `log`, `account_name`, `player_id`, `account_id`, `type`, `result`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setString(1, this.getPlayerName());
         ps.setString(2, this.getLog().toString());
         ps.setString(3, this.getAccountName());
         ps.setInt(4, this.getPlayerID());
         ps.setInt(5, this.getAccountID());
         ps.setInt(6, this.getType());
         ps.setInt(7, this.getResult());
         ps.setTimestamp(8, new Timestamp(this.getTime()));
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

   public int getResult() {
      return this.result;
   }

   public void setResult(int result) {
      this.result = result;
   }
}
