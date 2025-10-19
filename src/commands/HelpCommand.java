package commands;

import objects.users.MapleClient;

public class HelpCommand implements Command {
   @Override
   public void execute(MapleClient c, String[] splittedLine) throws Exception, IllegalCommandSyntaxException {
      try {
         CommandProcessor.getInstance().dropHelp(c.getPlayer(), CommandProcessor.getOptionalIntArg(splittedLine, 1, 1));
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("GM명령어", "[페이지 - 기본값 : 1]", "명령어 도움말을 표시합니다.", 1), new CommandDefinition("GM도움말", "[페이지 - 기본값 : 1]", "명령어 도움말을 표시합니다.", 1)
      };
   }
}
