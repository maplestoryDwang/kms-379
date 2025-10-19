package scripting;

import database.DBConfig;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import objects.fields.Portal;
import objects.users.MapleClient;
import objects.utils.FileoutputUtil;

public class PortalScriptManager {
   private static final PortalScriptManager instance = new PortalScriptManager();
   private final Map<String, PortalScript> scripts = new HashMap<>();
   private static final ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("javascript").getFactory();

   public static final PortalScriptManager getInstance() {
      return instance;
   }

   private final PortalScript getPortalScript(String scriptName) {
      if (this.scripts.containsKey(scriptName)) {
         return this.scripts.get(scriptName);
      } else {
         File scriptFile = new File("scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + "portal/" + scriptName + ".js");
         if (!scriptFile.exists()) {
            return null;
         } else {
            FileReader fr = null;
            ScriptEngine portal = sef.getScriptEngine();

            try {
               fr = new FileReader(scriptFile);
               CompiledScript compiled = ((Compilable)portal).compile(fr);
               compiled.eval();
            } catch (Exception var14) {
               System.err.println("Error executing Portalscript: " + scriptName + ":" + var14);
               FileoutputUtil.log("Log_Script_Except.rtf", "Error executing Portal script. (" + scriptName + ") " + var14);
            } finally {
               if (fr != null) {
                  try {
                     fr.close();
                  } catch (IOException var13) {
                     System.err.println("ERROR CLOSING" + var13);
                  }
               }
            }

            PortalScript script = ((Invocable)portal).getInterface(PortalScript.class);
            this.scripts.put(scriptName, script);
            return script;
         }
      }
   }

   public final void executePortalScript(Portal portal, MapleClient c) {
      PortalScript script = this.getPortalScript(portal.getScriptName());
      if (script != null) {
         try {
            script.enter(new PortalPlayerInteraction(c, portal));
         } catch (Exception var5) {
            System.err.println("Error entering Portalscript: " + portal.getScriptName() + " : " + var5);
         }
      } else {
         System.out.println("Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
         FileoutputUtil.log("Log_Script_Except.rtf", "Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
      }
   }

   public final void clearScripts() {
      this.scripts.clear();
   }
}
