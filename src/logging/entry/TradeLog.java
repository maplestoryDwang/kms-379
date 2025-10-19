package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class TradeLog extends LoggingEntry {
   private int type;
   private String name;
   private String name2;
   private int player_id;
   private int player_id2;
   private int account_id;
   private int account_id2;

   public TradeLog(MapleCharacter from, MapleCharacter to, int type, long serialNumber, StringBuilder log) {
      super(LoggingType.TradeLog, from.getClient().getAccountName(), from.getName(), from.getId(), from.getAccountID(), log, System.currentTimeMillis());
      this.setName(from.getName());
      this.setName2(to.getName());
      this.setPlayer_id(from.getId());
      this.setPlayer_id2(to.getId());
      this.setAccount_id(from.getAccountID());
      this.setAccount_id2(to.getAccountID());
      this.setType(type);
   }

   public TradeLog(MapleCharacter from, String partnerName, int partnerChrID, int partnerAccID, int type, long serialNumber, StringBuilder log) {
      super(LoggingType.TradeLog, from.getClient().getAccountName(), from.getName(), from.getId(), from.getAccountID(), log, System.currentTimeMillis());
      this.setName(from.getName());
      this.setName2(partnerName);
      this.setPlayer_id(from.getId());
      this.setPlayer_id2(partnerChrID);
      this.setAccount_id(from.getAccountID());
      this.setAccount_id2(partnerAccID);
      this.setType(type);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`name`, `player_id`, `account_id`, `name2`, `player_id2`, `account_id2`, `log`, `type`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setString(1, this.getName());
         ps.setInt(2, this.getPlayer_id());
         ps.setInt(3, this.getAccount_id());
         ps.setString(4, this.getName2());
         ps.setInt(5, this.getPlayer_id2());
         ps.setInt(6, this.getAccount_id2());
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

   public void setType(int type) {
      this.type = type;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName2() {
      return this.name2;
   }

   public void setName2(String name2) {
      this.name2 = name2;
   }

   public int getAccount_id2() {
      return this.account_id2;
   }

   public void setAccount_id2(int account_id2) {
      this.account_id2 = account_id2;
   }

   public int getAccount_id() {
      return this.account_id;
   }

   public void setAccount_id(int account_id) {
      this.account_id = account_id;
   }

   public int getPlayer_id() {
      return this.player_id;
   }

   public void setPlayer_id(int player_id) {
      this.player_id = player_id;
   }

   public int getPlayer_id2() {
      return this.player_id2;
   }

   public void setPlayer_id2(int player_id2) {
      this.player_id2 = player_id2;
   }
}
