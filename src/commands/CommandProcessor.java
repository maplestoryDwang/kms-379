package commands;

import constants.ServerConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.Pair;
import objects.utils.StringUtil;
import objects.utils.Timer;

public class CommandProcessor {
   private final List<Pair<String, String>> gmlog = new LinkedList<>();
   private final Map<String, DefinitionCommandPair> commands = new LinkedHashMap<>();
   private static CommandProcessor instance = new CommandProcessor();
   private static Runnable persister;
   private final Lock rl = new ReentrantLock();

   public static CommandProcessor getInstance() {
      return instance;
   }

   private CommandProcessor() {
      instance = this;
      Timer.WorldTimer.getInstance().register(persister, 1800000L);
      this.registerCommand(new HelpCommand());
      this.registerCommand(new BanningCommands());
      this.registerCommand(new CharCommands());
      this.registerCommand(new DebugCommands());
      this.registerCommand(new MonsterInfoCommands());
      this.registerCommand(new NPCSpawningCommands());
      this.registerCommand(new NoticeCommand());
      this.registerCommand(new PlayerCommand());
      this.registerCommand(new ProfilingCommands());
      this.registerCommand(new ReloadingCommands());
      this.registerCommand(new SearchCommands());
      this.registerCommand(new ServerMessageCommand());
      this.registerCommand(new ShutdownCommands());
      this.registerCommand(new SpawnMonsterCommand());
      this.registerCommand(new WarpCommands());
      this.registerCommand(new ProfilingCommands());
      this.registerCommand(new SpawnObjectCommands());
      this.registerCommand(new FieldCommand());
      this.registerCommand(new RoyalCommand());
   }

   private void registerCommand(Command command) {
      for (CommandDefinition def : command.getDefinition()) {
         this.commands.put(def.getCommand(), new DefinitionCommandPair(command, def));
      }
   }

   public static String joinAfterString(String[] splitted, String str) {
      for (int i = 1; i < splitted.length; i++) {
         if (splitted[i].equalsIgnoreCase(str) && i + 1 < splitted.length) {
            return StringUtil.joinStringFrom(splitted, i + 1);
         }
      }

      return null;
   }

   public static int getOptionalIntArg(String[] splitted, int position, int def) {
      if (splitted.length > position) {
         try {
            return Integer.parseInt(splitted[position]);
         } catch (NumberFormatException var4) {
            return def;
         }
      } else {
         return def;
      }
   }

   public static String getNamedArg(String[] splitted, int startpos, String name) {
      for (int i = startpos; i < splitted.length; i++) {
         if (splitted[i].equalsIgnoreCase(name) && i + 1 < splitted.length) {
            return splitted[i + 1];
         }
      }

      return null;
   }

   public static Integer getNamedIntArg(String[] splitted, int startpos, String name) {
      String arg = getNamedArg(splitted, startpos, name);
      if (arg != null) {
         try {
            return Integer.parseInt(arg);
         } catch (NumberFormatException var5) {
         }
      }

      return null;
   }

   public static int getNamedIntArg(String[] splitted, int startpos, String name, int def) {
      Integer ret = getNamedIntArg(splitted, startpos, name);
      return ret == null ? def : ret;
   }

   public static Double getNamedDoubleArg(String[] splitted, int startpos, String name) {
      String arg = getNamedArg(splitted, startpos, name);
      if (arg != null) {
         try {
            return Double.parseDouble(arg);
         } catch (NumberFormatException var5) {
         }
      }

      return null;
   }

   public boolean processCommand(MapleClient c, String line) {
      return instance.processCommandInternal(c, line);
   }

   public static void forcePersisting() {
      persister.run();
   }

   public void dropHelp(MapleCharacter chr, int page) {
      List<DefinitionCommandPair> allCommands = new ArrayList<>(this.commands.values());
      int startEntry = (page - 1) * 20;
      chr.dropMessage(6, "명령 도움말: --------" + page + "---------");

      for (int i = startEntry; i < startEntry + 20 && i < allCommands.size(); i++) {
         CommandDefinition commandDefinition = allCommands.get(i).getDefinition();
         if (commandDefinition.getRequiredLevel() > 0 && chr.hasGmLevel((byte)commandDefinition.getRequiredLevel())) {
            chr.dropMessage(6, commandDefinition.getCommand() + " " + commandDefinition.getParameterDescription() + ": " + commandDefinition.getHelp());
         }
      }

      allCommands.clear();
      allCommands = null;
   }

   private boolean processCommandInternal(MapleClient c, String line) {
      char adminCode = '/';
      if (line.charAt(0) == adminCode || line.charAt(0) == '@') {
         if (line.length() <= 1) {
            return false;
         }

         String[] splitted = line.split(String.valueOf(line.charAt(0)))[1].split(" ");
         if (splitted.length > 0 && splitted[0].length() > 0) {
            DefinitionCommandPair definitionCommandPair = this.commands.get(splitted[0]);
            if (definitionCommandPair != null
               && (c.getPlayer().getGMLevel() >= definitionCommandPair.getDefinition().getRequiredLevel() || line.charAt(0) == '/')) {
               try {
                  definitionCommandPair.getCommand().execute(c, splitted);
                  List<String> commands = new ArrayList<>(Arrays.asList(splitted));
                  String var7 = String.join(" ", commands);
               } catch (Exception | Error var12) {
                  if (ServerConstants.DEBUG_SEND) {
                     var12.printStackTrace();
                  }

                  return true;
               }

               if (c != null && c.getPlayer() != null && c.getPlayer().getGMLevel() > 0) {
                  this.rl.lock();

                  try {
                     this.gmlog.add(new Pair<>(c.getPlayer().getName(), line));
                  } finally {
                     this.rl.unlock();
                  }
               }

               return true;
            }

            c.getPlayer().dropMessage(6, "명령어 " + splitted[0] + " 은(는) 존재하지 않거나 실행권한이 없습니다.");
            return true;
         }
      }

      return false;
   }
}
