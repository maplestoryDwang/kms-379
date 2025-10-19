package objects.item;

import database.DBConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

public class MapleInventoryIdentifier implements Serializable {
   private static final long serialVersionUID = 21830921831301L;
   private final AtomicLong runningUID = new AtomicLong(0L);
   private static MapleInventoryIdentifier instance = new MapleInventoryIdentifier();

   public static long getInstance() {
      return instance.getNextUniqueId();
   }

   public long getNextUniqueId() {
      if (this.runningUID.get() <= 0L) {
         this.runningUID.set(this.initUID());
      } else {
         this.runningUID.set(this.runningUID.get() + 1L);
      }

      return this.runningUID.get();
   }

   public long initUID() {
      long ret = 0L;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         long[] ids = new long[5];
         PreparedStatement ps = con.prepareStatement(
            "SELECT MAX(uniqueid) FROM (SELECT uniqueid FROM inventoryitems WHERE uniqueid > 0 UNION ALL SELECT uniqueid FROM csitems WHERE uniqueid > 0) as s"
         );
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            ids[0] = rs.getLong(1) + 1L;
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT MAX(petid) FROM pets");
         rs = ps.executeQuery();
         if (rs.next()) {
            ids[1] = rs.getLong(1) + 1L;
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT MAX(ringid) FROM rings");
         rs = ps.executeQuery();
         if (rs.next()) {
            ids[2] = rs.getLong(1) + 1L;
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT MAX(partnerringid) FROM rings");
         rs = ps.executeQuery();
         if (rs.next()) {
            ids[3] = rs.getLong(1) + 1L;
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT MAX(uniqueid) FROM androids");
         rs = ps.executeQuery();
         if (rs.next()) {
            ids[4] = rs.getLong(1) + 1L;
         }

         rs.close();
         ps.close();

         for (int i = 0; i < ids.length; i++) {
            if (ids[i] > ret) {
               ret = ids[i];
            }
         }
      } catch (Exception var11) {
         System.out.println("UID Err");
         var11.printStackTrace();
      }

      return ret;
   }
}
