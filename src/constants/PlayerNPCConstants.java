package constants;

import database.DBConfig;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PlayerNPCConstants {
   public static final int DamageRank1NPC = 9901001;
   public static final int DojangRank1NPC = 9901002;
   public static final int DamageRank2NPC = 9901003;
   public static final int DamageRank3NPC = 9901004;
   public static int DamageRank1_Map = 0;
   public static int DamageRank1_X = 0;
   public static int DamageRank1_Y = 0;
   public static int DamageRank1_FH = 0;
   public static int DamageRank2_Map = 0;
   public static int DamageRank2_X = 0;
   public static int DamageRank2_Y = 0;
   public static int DamageRank2_FH = 0;
   public static int DamageRank3_Map = 0;
   public static int DamageRank3_X = 0;
   public static int DamageRank3_Y = 0;
   public static int DamageRank3_FH = 0;

   public static boolean isPlayerNPC(int npcID) {
      switch (npcID) {
         case 9901001:
         case 9901002:
         case 9901003:
         case 9901004:
            return true;
         default:
            return false;
      }
   }

   public static void loadPlayerNPCPos() {
      try {
         Properties props = getDefaultProperties();
         DamageRank1_Map = Integer.parseInt(props.getProperty("DamageRank1_Map", "100"));
         DamageRank1_X = Integer.parseInt(props.getProperty("DamageRank1_X", "0"));
         DamageRank1_Y = Integer.parseInt(props.getProperty("DamageRank1_Y", "0"));
         DamageRank1_FH = Integer.parseInt(props.getProperty("DamageRank1_FH", "0"));
         DamageRank2_Map = Integer.parseInt(props.getProperty("DamageRank2_Map", "100"));
         DamageRank2_X = Integer.parseInt(props.getProperty("DamageRank2_X", "0"));
         DamageRank2_Y = Integer.parseInt(props.getProperty("DamageRank2_Y", "0"));
         DamageRank2_FH = Integer.parseInt(props.getProperty("DamageRank2_FH", "0"));
         DamageRank3_Map = Integer.parseInt(props.getProperty("DamageRank3_Map", "100"));
         DamageRank3_X = Integer.parseInt(props.getProperty("DamageRank3_X", "0"));
         DamageRank3_Y = Integer.parseInt(props.getProperty("DamageRank3_Y", "0"));
         DamageRank3_FH = Integer.parseInt(props.getProperty("DamageRank3_FH", "0"));
         System.out.println("플레이어NPC 좌표데이터 불러오기 성공");
      } catch (Exception var1) {
         System.out.println("NPC설정 불러오기 실패");
         var1.printStackTrace();
      }
   }

   static Properties getDefaultProperties() throws FileNotFoundException, IOException {
      Properties props = new Properties();
      FileInputStream fileInputStream = new FileInputStream(DBConfig.isGanglim ? "settings_ganglim.properties" : "settings_jin.properties");
      props.load(fileInputStream);
      fileInputStream.close();
      return props;
   }
}
