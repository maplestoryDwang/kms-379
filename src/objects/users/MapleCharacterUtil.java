package objects.users;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConnection;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import objects.utils.LoginCryptoLegacy;
import objects.utils.Pair;
import objects.utils.Triple;

public class MapleCharacterUtil {
   private static final Pattern namePattern = Pattern.compile("[a-zA-Z0-9]{4,12}");
   private static final Pattern petPattern = Pattern.compile("[a-zA-Z0-9]{4,12}");

   public static final boolean canCreateChar(String name, boolean gm, boolean force) {
      if (ServerConstants.useTempCharacterName && name.startsWith("임시") && !force) {
         return false;
      } else {
         return (name.length() >= 2 || name.getBytes(Charset.forName("MS949")).length >= 4)
               && (name.length() <= 6 || name.getBytes(Charset.forName("MS949")).length <= 13)
               && getIdByName(name) == -1
            ? Pattern.matches("^[a-zA-Z0-9가-힝]*$", name)
            : false;
      }
   }

   public static final boolean isEligibleCharName(String name, boolean gm) {
      if (name.length() > 12) {
         return false;
      } else if (gm) {
         return true;
      } else if (name.length() >= 3 && namePattern.matcher(name).matches()) {
         for (String z : GameConstants.RESERVED) {
            if (name.indexOf(z) != -1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static final boolean canChangePetName(String name) {
      if (name.getBytes(Charset.forName("MS949")).length > 12) {
         return false;
      } else if (name.getBytes(Charset.forName("MS949")).length < 3) {
         return false;
      } else if (petPattern.matcher(name).matches()) {
         for (String z : GameConstants.RESERVED) {
            if (name.indexOf(z) != -1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static final String makeMapleReadable(String in) {
      String wui = in.replace('I', 'i');
      wui = wui.replace('l', 'L');
      wui = wui.replace("rn", "Rn");
      wui = wui.replace("vv", "Vv");
      return wui.replace("VV", "Vv");
   }

   public static final int getIdByName(String name) {
      DBConnection db = new DBConnection();

      try {
         int var6;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return -1;
            }

            int id = rs.getInt("id");
            rs.close();
            ps.close();
            var6 = id;
         }

         return var6;
      } catch (SQLException var9) {
         System.err.println("error 'getIdByName' " + var9);
         return -1;
      }
   }

   public static final int getAccByName(String name) {
      DBConnection db = new DBConnection();

      try {
         int var6;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name LIKE ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return -1;
            }

            int id = rs.getInt("accountid");
            rs.close();
            ps.close();
            var6 = id;
         }

         return var6;
      } catch (SQLException var9) {
         System.err.println("error 'getIdByName' " + var9);
         return -1;
      }
   }

   public static final int Change_SecondPassword(int accid, String password, String newpassword) {
      DBConnection db = new DBConnection();

      try {
         byte e;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * from accounts where id = ?");
            ps.setInt(1, accid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return -1;
            }

            String secondPassword = rs.getString("2ndpassword");
            String salt2 = rs.getString("salt2");
            if (secondPassword != null && salt2 != null) {
               secondPassword = LoginCrypto.rand_r(secondPassword);
            } else if (secondPassword == null && salt2 == null) {
               rs.close();
               ps.close();
               return 0;
            }

            if (!check_ifPasswordEquals(secondPassword, password, salt2)) {
               rs.close();
               ps.close();
               return 1;
            }

            rs.close();
            ps.close();

            String SHA1hashedsecond;
            try {
               SHA1hashedsecond = LoginCryptoLegacy.encodeSHA1(newpassword);
            } catch (Exception var13) {
               return -2;
            }

            ps = con.prepareStatement("UPDATE accounts set 2ndpassword = ?, salt2 = ? where id = ?");
            ps.setString(1, SHA1hashedsecond);
            ps.setString(2, null);
            ps.setInt(3, accid);
            if (!ps.execute()) {
               ps.close();
               return 2;
            }

            ps.close();
            e = -2;
         }

         return e;
      } catch (SQLException var15) {
         System.err.println("error 'getIdByName' " + var15);
         return -2;
      }
   }

   private static final boolean check_ifPasswordEquals(String passhash, String pwd, String salt) {
      if (LoginCryptoLegacy.isLegacyPassword(passhash) && LoginCryptoLegacy.checkPassword(pwd, passhash)) {
         return true;
      } else {
         return salt == null && LoginCrypto.checkSha1Hash(passhash, pwd) ? true : LoginCrypto.checkSaltedSha512Hash(passhash, pwd, salt);
      }
   }

   public static Triple<Integer, Integer, Integer> getInfoByName(String name, int world) {
      DBConnection db = new DBConnection();

      try {
         Triple var7;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name = ? AND world = ?");
            ps.setString(1, name);
            ps.setInt(2, world);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return null;
            }

            Triple<Integer, Integer, Integer> id = new Triple<>(rs.getInt("id"), rs.getInt("accountid"), rs.getInt("gender"));
            rs.close();
            ps.close();
            var7 = id;
         }

         return var7;
      } catch (Exception var10) {
         System.out.println("GetInfoName Err");
         var10.printStackTrace();
         return null;
      }
   }

   public static void setNXCodeUsed(String name, String code) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE nxcode SET `user` = ?, `valid` = 0 WHERE code = ?");
         ps.setString(1, name);
         ps.setString(2, code);
         ps.execute();
         ps.close();
      } catch (SQLException var8) {
         System.out.println("setNXCodeUsed Err");
         var8.printStackTrace();
      }
   }

   public static void sendNote(String to, String name, String msg, int fame) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("INSERT INTO notes (`to`, `from`, `message`, `timestamp`, `gift`) VALUES (?, ?, ?, ?, ?)", 1);
         ps.setString(1, to);
         ps.setString(2, name);
         ps.setString(3, msg);
         ps.setLong(4, System.currentTimeMillis());
         ps.setInt(5, fame);
         ps.executeUpdate();
         ResultSet rs = ps.getGeneratedKeys();
         ps.close();
         rs.close();
      } catch (SQLException var10) {
         System.err.println("Unable to send note" + var10);
      }
   }

   public static int memoCount(String name) {
      DBConnection db = new DBConnection();
      PreparedStatement ps = null;
      ResultSet rs = null;
      int count = 0;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", 1005, 1008);
         ps.setString(1, name);
         rs = ps.executeQuery();
         rs.last();
         count = rs.getRow();
         ps.close();
         rs.close();
      } catch (SQLException var20) {
         System.err.println("Unable to show note" + var20);
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }

            if (rs != null) {
               rs.close();
            }
         } catch (SQLException var17) {
            var17.printStackTrace();
         }
      }

      return count;
   }

   public static Pair<MapleMessage, MapleMessage> sendNewMemo(int fromcid, String from, int tocid, String to, String msg, int fame, boolean isGM) {
      MapleMessage left = null;
      MapleMessage right = null;
      DBConnection db = new DBConnection();
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         long now = System.currentTimeMillis();
         ps = con.prepareStatement(
            "INSERT INTO notes (`type`, `fromcid`, `from`, `tocid`, `to`, `message`, `timestamp`, `gift`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", 1
         );
         ps.setByte(1, (byte)0);
         ps.setInt(2, fromcid);
         ps.setString(3, isGM ? "메이플GM" : from);
         ps.setInt(4, tocid);
         ps.setString(5, to);
         ps.setString(6, msg);
         ps.setLong(7, now);
         ps.setInt(8, fame);
         ps.executeUpdate();
         rs = ps.getGeneratedKeys();
         if (rs.next()) {
            left = new MapleMessage(rs.getInt(1), fromcid, from, tocid, to, msg, now, fame > 0, false);
         }

         ps.close();
         rs.close();
         ps = con.prepareStatement(
            "INSERT INTO notes (`type`, `fromcid`, `from`, `tocid`, `to`, `message`, `timestamp`, `gift`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", 1
         );
         ps.setByte(1, (byte)1);
         ps.setInt(2, fromcid);
         ps.setString(3, isGM ? "메이플GM" : from);
         ps.setInt(4, tocid);
         ps.setString(5, to);
         ps.setString(6, msg);
         ps.setLong(7, now);
         ps.setInt(8, fame);
         ps.executeUpdate();
         rs = ps.getGeneratedKeys();
         if (rs.next()) {
            right = new MapleMessage(rs.getInt(1), fromcid, from, tocid, to, msg, now, fame > 0, false);
         }

         ps.close();
         rs.close();
         return new Pair<>(left, right);
      } catch (SQLException var29) {
         System.err.println("Unable to send note" + var29);
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }

            if (rs != null) {
               rs.close();
            }
         } catch (Exception var26) {
         }
      }

      return left != null && right != null ? new Pair<>(left, right) : null;
   }

   public static Triple<Boolean, Integer, Integer> getNXCodeInfo(String code) {
      Triple<Boolean, Integer, Integer> ret = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT `valid`, `type`, `item` FROM nxcode WHERE code LIKE ?");
         ps.setString(1, code);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            ret = new Triple<>(rs.getInt("valid") > 0, rs.getInt("type"), rs.getInt("item"));
         }

         rs.close();
         ps.close();
      } catch (SQLException var8) {
         System.out.println("DMG Parse Err");
         var8.printStackTrace();
      }

      return ret;
   }
}
