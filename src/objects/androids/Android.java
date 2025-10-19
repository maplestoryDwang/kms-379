package objects.androids;

import database.DBConnection;
import java.awt.Point;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleItemInformationProvider;
import objects.movepath.AbsoluteLifeMovement;
import objects.movepath.LifeMovement;
import objects.movepath.LifeMovementFragment;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class Android implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private int stance = 0;
   private int itemid;
   private int hair;
   private int face;
   private int skin;
   private long uniqueid = 0L;
   private boolean ear = false;
   private String name;
   private Point pos = new Point(0, 0);
   private boolean changed = false;

   private Android(int itemid, long uniqueid) {
      this.itemid = itemid;
      this.uniqueid = uniqueid;
   }

   public static final Android loadFromDb(int itemid, long uid) {
      DBConnection db = new DBConnection();

      try {
         Android var8;
         try (Connection con = DBConnection.getConnection()) {
            Android ret = new Android(itemid, uid);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM androids WHERE uniqueid = ?");
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return null;
            }

            ret.setHair(rs.getInt("hair"));
            ret.setFace(rs.getInt("face"));
            ret.setSkin(rs.getInt("skin"));
            ret.setName(rs.getString("name"));
            ret.setEar(rs.getInt("ear") == 1);
            ret.changed = false;
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

   public final void saveToDb() {
      if (this.changed) {
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE androids SET hair = ?, face = ?, skin = ?, name = ?, ear = ? WHERE uniqueid = ?");
            ps.setInt(1, this.hair);
            ps.setInt(2, this.face);
            ps.setInt(3, this.skin);
            ps.setString(4, this.name);
            ps.setInt(5, this.ear ? 1 : 0);
            ps.setLong(6, this.uniqueid);
            ps.executeUpdate();
            ps.close();
            this.changed = false;
         } catch (SQLException var7) {
            var7.printStackTrace();
         }
      }
   }

   public static final Android create(int itemid, long uniqueid) {
      Pair<List<Integer>, List<Integer>> aInfo = MapleItemInformationProvider.getInstance()
         .getAndroidInfo(MapleItemInformationProvider.getInstance().getAndroid(itemid));
      return aInfo == null
         ? null
         : create(itemid, uniqueid, aInfo.left.get(Randomizer.nextInt(aInfo.left.size())), aInfo.right.get(Randomizer.nextInt(aInfo.right.size())));
   }

   public static final Android create(int itemid, long uniqueid, int hair, int face) {
      if (uniqueid <= -1L) {
         uniqueid = MapleInventoryIdentifier.getInstance();
      }

      try (Connection con = DBConnection.getConnection()) {
         boolean find = true;

         for (int i = 0; i < 1000 && find; i++) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM androids WHERE uniqueid = ?")) {
               ps.setLong(1, uniqueid);

               try (ResultSet rs = ps.executeQuery()) {
                  if (rs.next()) {
                     find = true;
                     uniqueid = MapleInventoryIdentifier.getInstance();
                  } else {
                     find = false;
                  }
               }
            } catch (Exception var20) {
               System.out.println("Create Android Err");
               var20.printStackTrace();
               find = false;
            }
         }
      } catch (Exception var22) {
         System.out.println("CreateAndroid 2 Err");
         var22.printStackTrace();
      }

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement pse = con.prepareStatement("INSERT INTO androids (uniqueid, hair, face, skin, name, ear) VALUES (?, ?, ?, ?, ?, ?)");
         pse.setLong(1, uniqueid);
         pse.setInt(2, hair);
         pse.setInt(3, face);
         pse.setInt(4, 0);
         pse.setString(5, "안드로이드");
         pse.setInt(6, 0);
         pse.executeUpdate();
         pse.close();
      } catch (SQLException var17) {
         var17.printStackTrace();
         return null;
      }

      Android skin = new Android(itemid, uniqueid);
      skin.setHair(hair);
      skin.setFace(face);
      skin.setSkin(0);
      skin.setName("안드로이드");
      return skin;
   }

   public long getUniqueId() {
      return this.uniqueid;
   }

   public final void setHair(int closeness) {
      this.hair = closeness;
      this.changed = true;
   }

   public final int getHair() {
      return this.hair;
   }

   public final void setFace(int closeness) {
      this.face = closeness;
      this.changed = true;
   }

   public final int getFace() {
      return this.face;
   }

   public final void setSkin(int skin) {
      this.skin = skin;
      this.changed = true;
   }

   public final int getSkin() {
      return this.skin;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String n) {
      this.name = n;
      this.changed = true;
   }

   public final Point getPos() {
      return this.pos;
   }

   public final void setPos(Point pos) {
      this.pos = pos;
   }

   public final int getStance() {
      return this.stance;
   }

   public final void setStance(int stance) {
      this.stance = stance;
   }

   public final int getItemId() {
      return this.itemid;
   }

   public final void updatePosition(List<LifeMovementFragment> movement) {
      for (LifeMovementFragment move : movement) {
         if (move instanceof LifeMovement) {
            if (move instanceof AbsoluteLifeMovement) {
               this.setPos(((LifeMovement)move).getPosition());
            }

            this.setStance(((LifeMovement)move).getNewstate());
         }
      }
   }

   public boolean isEar() {
      return this.ear;
   }

   public void setEar(boolean ear) {
      this.ear = ear;
      this.changed = true;
   }
}
