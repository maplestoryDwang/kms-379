package network.connector;

import java.util.ArrayList;
import java.util.List;
import network.center.Center;
import network.game.GameServer;
import objects.users.MapleCharacter;

public class ConnectorThread implements Runnable {
   @Override
   public void run() {
      try {
         List<MapleCharacter> removeChars = new ArrayList<>();

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
               try {
                  if (!chr.getClient().isAllowedClient()) {
                     if (chr.getClient().getConnectorClient() != null) {
                        ConnectorClient cli = ConnectorServer.getInstance().getClientStorage().getLoginClient(chr.getClient().getConnectorClient().getId());
                        if (cli != null) {
                           if (cli.getAccountId() != chr.getClient().getAccID()) {
                              System.out
                                 .println("[커넥터 오류] " + chr.getName() + " 캐릭터의 AccountID가 다름 : " + cli.getAccountId() + " / " + chr.getClient().getAccID());
                              removeChars.add(chr);
                           }
                        } else {
                           System.out.println("[커넥터 오류] " + chr.getName() + " 캐릭터의 저장된 커넥터 클라이언트 정보가 Null");
                           removeChars.add(chr);
                        }
                     } else {
                        ConnectorClient cli = ConnectorServer.getInstance().getClientStorage().getLoginClient(chr.getClient().getAccountName());
                        if (cli != null) {
                           chr.getClient().setConnectorClient(cli);
                        } else {
                           System.out.println("[커넥터 오류] " + chr.getName() + " 캐릭터의 계정 커넥터 클라이언트 정보가 Null");
                           removeChars.add(chr);
                        }
                     }
                  }
               } catch (Exception var7) {
                  System.out.println("커넥터 쓰레드 오류 발생");
                  var7.printStackTrace();
               }
            }
         }

         for (MapleCharacter chr : removeChars) {
            if (chr.getClient().getConnectorClient() != null) {
               chr.getClient().getConnectorClient().getSession().close();
               System.out.println("팅겼다고인마");
            }

            chr.getClient().getSession().close();
            System.out.println("팅겼다고인마");
            Center.Find.forceDeregister(chr.getId(), chr.getName(), chr.getAccountID());
         }
      } catch (Exception var8) {
         System.out.println("커넥터 쓰레드 오류 발생");
         var8.printStackTrace();
      }
   }
}
