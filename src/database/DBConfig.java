package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DBConfig {
   public static final boolean defaultisHosting = false;
   public static final boolean defaultisRoyal = true;
   public static final boolean isHosting;
   public static final boolean isGanglim;
   public static final boolean isJin;
   public static final boolean usePasswordMD5 = true;
   public static final String DB_URL;
   public static final String Log_DB_URL = "jdbc:mariadb://127.0.0.1:3309/maple_log?characterEncoding=utf8&autoReconnect=true";
   public static final String DB_USER = "root";
   public static final String DB_PASSWORD;
   public static final String RoyalOutDB_URL = "jdbc:mariadb://127.0.0.1:3309/odin_sea?characterEncoding=utf8&autoReconnect=true";
   public static final String RoyalOutDB_PASSWORD = "123456";
   public static final int DB_MIN_IMUM_IDLE = 10;
   public static final int DB_MAXIMUM_POOL_SIZE = 5000;

   static Properties getDefaultProperties() throws FileNotFoundException, IOException {
      Properties props = new Properties();
      FileInputStream fileInputStream = new FileInputStream("server.properties");
      props.load(fileInputStream);
      fileInputStream.close();
      return props;
   }

   static {
      boolean hosting = false;
      boolean ganglim = false;

      try {
         Properties prop = getDefaultProperties();
         hosting = prop.getProperty("isHosting", "true").equals("true");
         ganglim = prop.getProperty("isGanglim", "true").equals("true");
      } catch (Exception var6) {
         System.out.println("기본값 불러오기 실패 팩 기본값 사용");
         var6.printStackTrace();
         hosting = false;
         ganglim = true;
      } finally {
         System.out.println("isHosting = " + hosting);
         System.out.println("isGanglim = " + ganglim);
      }

      isHosting = hosting;
      isGanglim = ganglim;
      isJin = !ganglim;
      DB_URL = isGanglim
         ? "jdbc:mariadb://127.0.0.1:3309/ganglim?characterEncoding=utf8&autoReconnect=true"
         : "jdbc:mariadb://127.0.0.1:3309/odin_sea?characterEncoding=utf8&autoReconnect=true";
      DB_PASSWORD = "123456";
   }
}
