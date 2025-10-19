package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class CabinetLog extends LoggingEntry {
   private int itemID;
   private int quantity;
   private int type;

   public CabinetLog(MapleCharacter player, int itemID, int quantity, int type, StringBuilder log) {
      super(
         LoggingType.CabinetLog, player.getClient().getAccountName(), player.getName(), player.getId(), player.getAccountID(), log, System.currentTimeMillis()
      );
      this.setItemID(itemID);
      this.setQuantity(quantity);
      this.setType(type);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`item_id`, `quantity`, `name`, `account_name`, `player_id`, `account_id`, `log`, `type`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setInt(1, this.getItemID());
         ps.setInt(2, this.getQuantity());
         ps.setString(3, this.getPlayerName());
         ps.setString(4, this.getAccountName());
         ps.setInt(5, this.getPlayerID());
         ps.setInt(6, this.getAccountID());
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
}
