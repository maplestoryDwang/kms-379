package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class AuctionLog extends LoggingEntry {
   private int itemID;
   private int quantity;
   private long serialNumber;
   private int type;
   private long price;
   private String itemName;

   public AuctionLog(MapleCharacter player, int itemID, int quantity, long price, long serialNumber, int type, StringBuilder log) {
      super(
         LoggingType.AuctionLog, player.getClient().getAccountName(), player.getName(), player.getId(), player.getAccountID(), log, System.currentTimeMillis()
      );
      this.setItemID(itemID);
      this.setQuantity(quantity);
      this.setSerialNumber(serialNumber);
      this.setType(type);
      this.setPrice(price);
   }

   public AuctionLog(
      int accID, String accName, int chrID, String chrName, int itemID, String itemName, int qty, long price, long serialNumber, int type, StringBuilder log
   ) {
      super(LoggingType.AuctionLog, accName, chrName, chrID, accID, log, System.currentTimeMillis());
      this.setItemID(itemID);
      this.setItemName(itemName);
      this.setQuantity(qty);
      this.setSerialNumber(serialNumber);
      this.setType(type);
      this.setPrice(price);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         if (this.getItemName() != null) {
            ps = con.prepareStatement(
               String.format(
                  "INSERT INTO `%s`(`item_id`, `quantity`, `price`, `serial_number`, `name`, `account_name`, `player_id`, `account_id`, `log`, `type`, `time`, `item_name`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                  this.getLoggingType().getSchemaName()
               )
            );
         } else {
            ps = con.prepareStatement(
               String.format(
                  "INSERT INTO `%s`(`item_id`, `quantity`, `price`, `serial_number`, `name`, `account_name`, `player_id`, `account_id`, `log`, `type`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                  this.getLoggingType().getSchemaName()
               )
            );
         }

         ps.setInt(1, this.getItemID());
         ps.setInt(2, this.getQuantity());
         ps.setLong(3, this.getPrice());
         ps.setLong(4, this.getSerialNumber());
         ps.setString(5, this.getPlayerName());
         ps.setString(6, this.getAccountName());
         ps.setInt(7, this.getPlayerID());
         ps.setInt(8, this.getAccountID());
         ps.setString(9, this.getLog().toString());
         ps.setByte(10, (byte)this.getType());
         ps.setTimestamp(11, new Timestamp(this.getTime()));
         if (this.getItemName() != null) {
            ps.setString(12, this.getItemName());
         }

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

   public String getItemName() {
      return this.itemName;
   }

   public void setItemName(String itemName) {
      this.itemName = itemName;
   }

   public int getQuantity() {
      return this.quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public long getSerialNumber() {
      return this.serialNumber;
   }

   public void setSerialNumber(long serialNumber) {
      this.serialNumber = serialNumber;
   }

   public long getPrice() {
      return this.price;
   }

   public void setPrice(long price) {
      this.price = price;
   }
}
