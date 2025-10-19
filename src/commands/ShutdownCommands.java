package commands;

import network.game.GameServer;
import network.shop.CashShopServer;
import objects.users.MapleClient;

public class ShutdownCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      if (splitted[0].equals("서버종료")) {
         CommandProcessor.forcePersisting();
         c.getChannelServer().shutdown();
      } else if (splitted[0].equals("캐시샵서버종료")) {
         CashShopServer.shutdown();
      } else if (splitted[0].equals("전체서버종료")) {
         int time = 60000;
         if (splitted.length > 1) {
            time = Integer.parseInt(splitted[1]) * 60000;
         }

         CommandProcessor.forcePersisting();

         for (GameServer cs : GameServer.getAllInstances()) {
            cs.shutdown();
         }
      } else if (splitted[0].equals("고용상점종료")) {
         c.getChannelServer().closeAllMerchant();
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("고용상점종료", "", "현재 채널의 모든 고용상점을 닫습니다.", 6),
         new CommandDefinition("캐시샵서버종료", "", "캐시샵 서버를 종료합니다.", 6),
         new CommandDefinition("서버종료", "(<종료대기시간>)", "현재 채널을 종료합니다.", 6),
         new CommandDefinition("전체서버종료", "(<종료대기시간>)", "모든 서버를 종료합니다.", 6)
      };
   }
}
