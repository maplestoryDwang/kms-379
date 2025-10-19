package network.login;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import objects.utils.MD5Tool;
import objects.utils.Pair;

public class AutoRegister {
   private static final int ACCOUNTS_PER_IP = 2;
   public static final boolean autoRegister = false;

   public static Pair<String, String> getAccountsInfo(String charName) {
      String name = "";
      String secondpassword = "";
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement(
            "SELECT a.name, a.2ndpassword, c.name from accounts AS a, characters AS c WHERE a.id = c.accountid AND c.name = ?"
         );
         ps.setString(1, charName);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            name = rs.getString("a.name");
            secondpassword = rs.getString("a.2ndpassword");
         }

         rs.close();
         ps.close();
      } catch (Exception var9) {
         System.out.println("Reg Err");
         var9.printStackTrace();
      }

      return new Pair<>(name, secondpassword);
   }

   public static boolean getCharacterExists(String login) {
      boolean characterExists = false;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT name FROM characters WHERE name = ?");
         ps.setString(1, login);
         ResultSet rs = ps.executeQuery();
         if (rs.first()) {
            characterExists = true;
         }

         rs.close();
         ps.close();
      } catch (Exception var8) {
         System.out.println("Reg Err");
         var8.printStackTrace();
      }

      return characterExists;
   }

   public static boolean getAccountExists(String login) {
      boolean accountExists = false;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
         ps.setString(1, login);
         ResultSet rs = ps.executeQuery();
         if (rs.first()) {
            accountExists = true;
         }

         rs.close();
         ps.close();
      } catch (Exception var8) {
         System.out.println("Reg Err");
         var8.printStackTrace();
      }

      return accountExists;
   }

   public static boolean getAccountCheckPassword(String login, String pwd) {
      boolean checkPW = false;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
         ps.setString(1, login);
         ResultSet rs = ps.executeQuery();
         if (rs.first()) {
            String passhash = rs.getString("password");
            if (MD5Tool.generateMD5(pwd).equals(passhash)) {
               checkPW = true;
            }
         }

         rs.close();
         ps.close();
      } catch (Exception var10) {
         System.out.println("Reg Err");
         var10.printStackTrace();
      }

      return checkPW;
   }

   public static boolean getAccountExistsDiscordId(long id) {
      boolean accountExists = false;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT discordid FROM accounts WHERE discordid = ?");
         ps.setLong(1, id);
         ResultSet rs = ps.executeQuery();
         if (rs.first()) {
            accountExists = true;
         }

         rs.close();
         ps.close();
      } catch (Exception var9) {
         System.out.println("Reg Err");
         var9.printStackTrace();
      }

      return accountExists;
   }

   public static boolean getAccountExistsPhoneNumber(String number) {
      boolean accountExists = false;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT phonenumber FROM accounts WHERE phonenumber = ?");
         ps.setString(1, number);
         ResultSet rs = ps.executeQuery();
         if (rs.first()) {
            accountExists = true;
         }

         rs.close();
         ps.close();
      } catch (Exception var8) {
         System.out.println("Reg Err");
         var8.printStackTrace();
      }

      return accountExists;
   }

   public static synchronized boolean createAccount(String login, String pwd, long discordid, String phonenumber) {
      DBConnection db = new DBConnection();

      try {
         boolean var8;
         try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
               "INSERT INTO accounts (name, password, email, birthday, macs, SessionIP, discordid, phonenumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
         ) {
            ps.setString(1, login);
            ps.setString(2, MD5Tool.generateMD5(pwd));
            ps.setString(3, "no@email.provided");
            ps.setString(4, "2008-04-07");
            ps.setString(5, "00-00-00-00-00-00, 00-00-00-00");
            ps.setString(6, "");
            ps.setLong(7, discordid);
            ps.setString(8, phonenumber);
            ps.executeUpdate();
            ps.close();
            var8 = true;
         }

         return var8;
      } catch (SQLException var14) {
         System.out.println("Reg Err");
         var14.printStackTrace();
         return false;
      }
   }

   public static synchronized boolean updateAccount(String login, String pwd, long discordid, String phonenumber) {
      DBConnection db = new DBConnection();
      Connection con = DBConnection.getConnection();
      PreparedStatement ps = null;
      ResultSet rs = null;

      boolean var10;
      try {
         ps = con.prepareStatement("UPDATE accounts SET discordid = ?, phonenumber = ? WHERE name = ?");
         ps.setLong(1, discordid);
         ps.setString(2, phonenumber);
         ps.setString(3, login);
         int check = ps.executeUpdate();
         if (check != 1) {
            ps.close();
            rs.close();
            return false;
         }

         var10 = true;
      } catch (Exception var26) {
         System.out.println("Reg Err");
         var26.printStackTrace();
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (Exception var25) {
            System.out.println("Reg Err");
            var25.printStackTrace();
         }

         try {
            if (rs != null) {
               rs.close();
            }
         } catch (Exception var24) {
            System.out.println("Reg Err");
            var24.printStackTrace();
         }
      }

      return var10;
   }
}
