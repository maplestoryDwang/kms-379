package scripting;

import database.DBConfig;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import objects.users.MapleClient;

public abstract class AbstractScriptManager {
   private static final ScriptEngineManager sem = new ScriptEngineManager();

   protected Invocable getInvocable(String path, MapleClient c) {
      return this.getInvocable(path, c, false);
   }

   protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
      if (!path.contains("Royal/") && !path.contains("Jin/")) {
         path = "scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + path;
      }

      ScriptEngine engine = null;
      if (c != null) {
         engine = c.getScriptEngine(path);
      }

      if (engine == null) {
         File scriptFile = new File(path);
         if (!scriptFile.exists()) {
            return null;
         }

         engine = sem.getEngineByName("nashorn");
         if (c != null) {
            c.setScriptEngine(path, engine);
         }

         String encoding = "EUC-KR";

         try (Stream<String> stream = Files.lines(scriptFile.toPath(), Charset.forName(encoding))) {
            String lines = "load('nashorn:mozilla_compat.js');";
            lines = lines + stream.collect(Collectors.joining(System.lineSeparator()));
            engine.eval(lines);
         } catch (Exception var16) {
            encoding = "UTF-8";

            try (Stream<String> streamx = Files.lines(scriptFile.toPath(), Charset.forName(encoding))) {
               String linesx = "load('nashorn:mozilla_compat.js');";
               linesx = linesx + streamx.collect(Collectors.joining(System.lineSeparator()));
               engine.eval(linesx);
            } catch (IOException | ScriptException var14) {
               var14.printStackTrace();
               return null;
            }
         }
      }

      return (Invocable)engine;
   }
}
