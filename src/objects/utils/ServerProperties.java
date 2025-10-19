package objects.utils;

import database.DBConfig;
import database.DBConnection;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerProperties {
   private static final java.util.Properties props = new java.util.Properties();

   private ServerProperties() {
   }

   public static void loadProperties(String s) {
      try {
         FileReader fr = new FileReader(s);
         props.load(fr);
         fr.close();
      } catch (IOException var3) {
         var3.printStackTrace();
      }
   }

   public static String getProperty(String s) {
      return props.getProperty(s);
   }

   public static void setProperty(String prop, String newInf) {
      props.setProperty(prop, newInf);
   }

   public static String getProperty(String s, String def) {
      return props.getProperty(s, def);
   }

   static {
      String path = "./settings/";
      if (DBConfig.isGanglim) {
         path = path + "Ganglim/";
      } else {
         path = path + "Jin/";
      }

      String toLoad = path + "channel.properties";
      loadProperties(toLoad);
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM auth_server_channel_ip");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            props.put(rs.getString("name") + rs.getInt("channelid"), rs.getString("value"));
         }

         rs.close();
         ps.close();
      } catch (SQLException var8) {
         var8.printStackTrace();
         System.exit(0);
      }

      toLoad = path + "world.properties";
      loadProperties(toLoad);
      toLoad = path + "ModdifiedCommodity.properties";
      loadProperties(toLoad);
   }
}
