package objects.item;

import database.DBConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import objects.users.MapleCharacter;

public class MapleRing implements Serializable {
   private static final long serialVersionUID = 9179541993413738579L;
   private long ringId;
   private long ringId2;
   private int partnerId;
   private int itemId;
   private String partnerName;
   private boolean equipped = false;

   private MapleRing(long id, long id2, int partnerId, int itemid, String partnerName) {
      this.ringId = id;
      this.ringId2 = id2;
      this.partnerId = partnerId;
      this.itemId = itemid;
      this.partnerName = partnerName;
   }

   public static MapleRing loadFromDb(long ringId) {
      return loadFromDb(ringId, false);
   }

   public static MapleRing loadFromDb(long ringId, boolean equipped) {
      DBConnection db = new DBConnection();

      try {
         MapleRing var8;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM rings WHERE ringId = ?");
            ps.setLong(1, ringId);
            ResultSet rs = ps.executeQuery();
            MapleRing ret = null;
            if (rs.next()) {
               ret = new MapleRing(ringId, rs.getLong("partnerRingId"), rs.getInt("partnerChrId"), rs.getInt("itemid"), rs.getString("partnerName"));
               ret.setEquipped(equipped);
            }

            rs.close();
            ps.close();
            var8 = ret;
         }

         return var8;
      } catch (SQLException var11) {
         var11.printStackTrace();
         return null;
      }
   }

   public static void addToDB(int itemid, MapleCharacter chr, String player, int id, long[] ringId) throws SQLException {
      DBConnection db = new DBConnection();
      Connection con = DBConnection.getConnection();
      PreparedStatement ps = con.prepareStatement("INSERT INTO rings (ringId, itemid, partnerChrId, partnerName, partnerRingId) VALUES (?, ?, ?, ?, ?)");
      ps.setLong(1, ringId[0]);
      ps.setInt(2, itemid);
      ps.setInt(3, chr.getId());
      ps.setString(4, chr.getName());
      ps.setLong(5, ringId[1]);
      ps.executeUpdate();
      ps.close();
      ps = con.prepareStatement("INSERT INTO rings (ringId, itemid, partnerChrId, partnerName, partnerRingId) VALUES (?, ?, ?, ?, ?)");
      ps.setLong(1, ringId[1]);
      ps.setInt(2, itemid);
      ps.setInt(3, id);
      ps.setString(4, player);
      ps.setLong(5, ringId[0]);
      ps.executeUpdate();
      ps.close();
      con.close();
   }

   public static long createRing(int itemid, MapleCharacter partner1, String partner2, String msg, int id2, int sn) {
      try {
         if (partner1 == null) {
            return -2L;
         } else {
            return id2 <= 0 ? -1L : makeRing(itemid, partner1, partner2, id2, msg, sn);
         }
      } catch (Exception var7) {
         System.out.println("CreateRing Err");
         var7.printStackTrace();
         return 0L;
      }
   }

   public static long[] makeRing(int itemid, MapleCharacter partner1, MapleCharacter partner2) throws Exception {
      long[] ringID = new long[]{MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};

      try {
         addToDB(itemid, partner1, partner2.getName(), partner2.getId(), ringID);
         return ringID;
      } catch (SQLException var5) {
         return ringID;
      }
   }

   public static long makeRing(int itemid, MapleCharacter partner1, String partner2, int id2, String msg, int sn) throws Exception {
      long[] ringID = new long[]{MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};

      try {
         addToDB(itemid, partner1, partner2, id2, ringID);
      } catch (SQLException var8) {
         return 0L;
      }

      MapleInventoryManipulator.addRing(partner1, itemid, ringID[1], sn, partner2);
      partner1.getCashInventory().gift(id2, partner1.getName(), msg, sn, ringID[0]);
      return 1L;
   }

   public long getRingId() {
      return this.ringId;
   }

   public long getPartnerRingId() {
      return this.ringId2;
   }

   public int getPartnerChrId() {
      return this.partnerId;
   }

   public int getItemId() {
      return this.itemId;
   }

   public boolean isEquipped() {
      return this.equipped;
   }

   public void setEquipped(boolean equipped) {
      this.equipped = equipped;
   }

   public String getPartnerName() {
      return this.partnerName;
   }

   public void setPartnerName(String partnerName) {
      this.partnerName = partnerName;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof MapleRing ? ((MapleRing)o).getRingId() == this.getRingId() : false;
   }

   @Override
   public int hashCode() {
      int hash = 5;
      return 53 * hash + (int)this.ringId;
   }

   public static void removeRingFromDb(MapleCharacter player) {
      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM rings WHERE partnerChrId = ?");
            ps.setInt(1, player.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
               int otherId = rs.getInt("partnerRingId");
               int otherotherId = rs.getInt("ringId");
               rs.close();
               ps.close();
               ps = con.prepareStatement("DELETE FROM rings WHERE ringId = ? OR ringId = ?");
               ps.setInt(1, otherotherId);
               ps.setInt(2, otherId);
               ps.executeUpdate();
               ps.close();
               return;
            }

            ps.close();
            rs.close();
         }
      } catch (SQLException var9) {
         var9.printStackTrace();
      }
   }

   public static class RingComparator implements Comparator<MapleRing>, Serializable {
      public int compare(MapleRing o1, MapleRing o2) {
         if (o1.ringId < o2.ringId) {
            return -1;
         } else {
            return o1.ringId == o2.ringId ? 0 : 1;
         }
      }
   }
}
