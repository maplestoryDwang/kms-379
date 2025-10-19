package commands;

import objects.users.MapleClient;

public interface Command {
   CommandDefinition[] getDefinition();

   void execute(MapleClient var1, String[] var2) throws Exception, IllegalCommandSyntaxException;
}
