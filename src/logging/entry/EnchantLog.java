package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class EnchantLog extends LoggingEntry {
   private int itemID;
   private int targetItemID;
   private long targetSerialNumber;
   private int type;
   private int result;

   public EnchantLog(MapleCharacter player, int itemID, int targetItemID, long targetSerialNumber, int type, int result, StringBuilder log) {
      super(
         LoggingType.EnchantLog, player.getClient().getAccountName(), player.getName(), player.getId(), player.getAccountID(), log, System.currentTimeMillis()
      );
      this.setItemID(itemID);
      this.setTargetItemID(targetItemID);
      this.setTargetSerialNumber(targetSerialNumber);
      this.setType(type);
      this.setResult(result);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`item_id`, `target_item_id`, `target_serial_number`, `name`, `account_name`, `player_id`, `account_id`, `log`, `type`, `result`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setInt(1, this.getItemID());
         ps.setInt(2, this.getTargetItemID());
         ps.setLong(3, this.getTargetSerialNumber());
         ps.setString(4, this.getPlayerName());
         ps.setString(5, this.getAccountName());
         ps.setInt(6, this.getPlayerID());
         ps.setInt(7, this.getAccountID());
         ps.setString(8, this.getLog().toString());
         ps.setByte(9, (byte)this.getType());
         ps.setByte(10, (byte)this.getResult());
         ps.setTimestamp(11, new Timestamp(this.getTime()));
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

   public int getTargetItemID() {
      return this.targetItemID;
   }

   public void setTargetItemID(int targetItemID) {
      this.targetItemID = targetItemID;
   }

   public long getTargetSerialNumber() {
      return this.targetSerialNumber;
   }

   public void setTargetSerialNumber(long targetSerialNumber) {
      this.targetSerialNumber = targetSerialNumber;
   }
}
