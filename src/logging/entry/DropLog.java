package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class DropLog extends LoggingEntry {
   private int itemID;
   private int quantity;
   private int fieldID;
   private int type;
   private int channel;

   public DropLog(MapleCharacter player, int itemID, int quantity, int channel, int fieldID, int type, StringBuilder log) {
      super(LoggingType.DropLog, player.getClient().getAccountName(), player.getName(), player.getId(), player.getAccountID(), log, System.currentTimeMillis());
      this.setItemID(itemID);
      this.setQuantity(quantity);
      this.setFieldID(fieldID);
      this.setType(type);
      this.setChannel(channel);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`item_id`, `quantity`, `channel`, `field_id`, `name`, `account_name`, `player_id`, `account_id`, `log`, `type`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setInt(1, this.getItemID());
         ps.setInt(2, this.getQuantity());
         ps.setInt(3, this.getChannel());
         ps.setInt(4, this.getFieldID());
         ps.setString(5, this.getPlayerName());
         ps.setString(6, this.getAccountName());
         ps.setInt(7, this.getPlayerID());
         ps.setInt(8, this.getAccountID());
         ps.setString(9, this.getLog().toString());
         ps.setByte(10, (byte)this.getType());
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

   public int getQuantity() {
      return this.quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public int getFieldID() {
      return this.fieldID;
   }

   public void setFieldID(int fieldID) {
      this.fieldID = fieldID;
   }

   public int getChannel() {
      return this.channel;
   }

   public void setChannel(int channel) {
      this.channel = channel;
   }
}
