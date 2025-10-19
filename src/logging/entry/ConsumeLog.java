package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class ConsumeLog extends LoggingEntry {
   private int itemID;

   public ConsumeLog(MapleCharacter player, int itemID, StringBuilder log) {
      super(
         LoggingType.ConsumeLog, player.getClient().getAccountName(), player.getName(), player.getId(), player.getAccountID(), log, System.currentTimeMillis()
      );
      this.setItemID(itemID);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`item_id`, `name`, `account_name`, `player_id`, `account_id`, `log`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setInt(1, this.getItemID());
         ps.setString(2, this.getPlayerName());
         ps.setString(3, this.getAccountName());
         ps.setInt(4, this.getPlayerID());
         ps.setInt(5, this.getAccountID());
         ps.setString(6, this.getLog().toString());
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

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }
}
