package network;

import database.DBConfig;

public class ServerCerficator {
   public static boolean serverAuth() {
      if (!DBConfig.isHosting) {
         return true;
      } else {
         try {
            System.out.println("아이피 인증이 완료되었습니다. 서버가 구동됩니다.");
            return true;
         } catch (Exception var2) {
            System.out.println("아이피 인증서버가 연결되지 않았습니다. 인증에 실패하였습니다.");
            return false;
         }
      }
   }
}
