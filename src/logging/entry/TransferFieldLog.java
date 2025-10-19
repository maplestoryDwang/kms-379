package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class TransferFieldLog extends LoggingEntry {
   private int currentField;
   private int targetField;

   public TransferFieldLog(MapleCharacter player, int currentField, int targetField) {
      super(
         LoggingType.TransferFieldLog,
         player.getClient().getAccountName(),
         player.getName(),
         player.getId(),
         player.getAccountID(),
         null,
         System.currentTimeMillis()
      );
      this.setCurrentField(currentField);
      this.setTargetField(targetField);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`name`, `account_name`, `player_id`, `account_id`, `current_field`, `target_field`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setString(1, this.getPlayerName());
         ps.setString(2, this.getAccountName());
         ps.setInt(3, this.getPlayerID());
         ps.setInt(4, this.getAccountID());
         ps.setInt(5, this.getCurrentField());
         ps.setInt(6, this.getTargetField());
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

   public int getCurrentField() {
      return this.currentField;
   }

   public void setCurrentField(int currentField) {
      this.currentField = currentField;
   }

   public int getTargetField() {
      return this.targetField;
   }

   public void setTargetField(int targetField) {
      this.targetField = targetField;
   }
}
