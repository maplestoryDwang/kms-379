package logging.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import logging.LoggingEntry;
import logging.LoggingType;
import objects.users.MapleCharacter;

public class DamageHackLog extends LoggingEntry {
   private long damage;

   public DamageHackLog(MapleCharacter player, long damage, StringBuilder log) {
      super(
         LoggingType.DamageHackLog,
         player.getClient().getAccountName(),
         player.getName(),
         player.getId(),
         player.getAccountID(),
         log,
         System.currentTimeMillis()
      );
      this.setDamage(damage);
   }

   @Override
   public void insert(Connection con) {
      PreparedStatement ps = null;

      try {
         ps = con.prepareStatement(
            String.format(
               "INSERT INTO `%s`(`name`, `account_name`, `player_id`, `account_id`, `damage`, `log`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?)",
               this.getLoggingType().getSchemaName()
            )
         );
         ps.setString(1, this.getPlayerName());
         ps.setString(2, this.getAccountName());
         ps.setInt(3, this.getPlayerID());
         ps.setInt(4, this.getAccountID());
         ps.setLong(5, this.getDamage());
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

   public long getDamage() {
      return this.damage;
   }

   public void setDamage(long damage) {
      this.damage = damage;
   }
}
