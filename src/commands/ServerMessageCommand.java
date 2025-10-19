package commands;

import java.util.Collection;
import network.game.GameServer;
import objects.users.MapleClient;
import objects.utils.StringUtil;

public class ServerMessageCommand implements Command {
   @Override
   public void execute(MapleClient c, String[] splittedLine) throws Exception, IllegalCommandSyntaxException {
      Collection<GameServer> cservs = GameServer.getAllInstances();
      String outputMessage = StringUtil.joinStringFrom(splittedLine, 1);

      for (GameServer cserv : cservs) {
         cserv.setServerMessage(outputMessage);
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{new CommandDefinition("서버공지", "<메시지>", "서버 전체의 노란색 공지 메시지를 바꿉니다.", 2)};
   }
}
