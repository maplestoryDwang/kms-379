package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class PickupLog extends LoggingEntry {
   private int itemID;
   private int quantity;
   private int fieldID;
   private int type;
   private long serialNumber;
   private int channel;
   private long price;

   public PickupLog(MapleCharacter player, int channel, int itemID, int quantity, int fieldID, int type, long price, long serialNumber, StringBuilder log) {
      super(
         LoggingType.PickupLog, player.getClient().getAccountName(), player.getName(), player.getId(), player.getAccountID(), log, System.currentTimeMillis()
      );
      this.setItemID(itemID);
      this.setQuantity(quantity);
      this.setFieldID(fieldID);
      this.setType(type);
      this.setSerialNumber(serialNumber);
      this.setChannel(channel);
      this.setPrice(price);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`item_id`, `quantity`, `channel`, `field_id`, `serial_number`, `name`, `log`, `account_name`, `player_id`, `account_id`, `type`, `price` ,`time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setInt(1, this.getItemID());
         ps.setInt(2, this.getQuantity());
         ps.setInt(3, this.getChannel());
         ps.setInt(4, this.getFieldID());
         ps.setLong(5, this.getSerialNumber());
         ps.setString(6, this.getPlayerName());
         ps.setString(7, this.getLog().toString());
         ps.setString(8, this.getAccountName());
         ps.setInt(9, this.getPlayerID());
         ps.setInt(10, this.getAccountID());
         ps.setByte(11, (byte)this.getType());
         ps.setLong(12, this.getPrice());
         ps.setTimestamp(13, new Timestamp(this.getTime()));
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

   public int getQuantity() {
      return this.quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public long getSerialNumber() {
      return this.serialNumber;
   }

   public void setSerialNumber(long serialNumber) {
      this.serialNumber = serialNumber;
   }

   public int getFieldID() {
      return this.fieldID;
   }

   public void setFieldID(int fieldID) {
      this.fieldID = fieldID;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getChannel() {
      return this.channel;
   }

   public void setChannel(int channel) {
      this.channel = channel;
   }

   public void setPrice(long price) {
      this.price = price;
   }

   public long getPrice() {
      return this.price;
   }
}
