package scripting;

import database.DBConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import network.game.GameServer;

public class EventScriptManager extends AbstractScriptManager {
   private final Map<String, EventScriptManager.EventEntry> events = new LinkedHashMap<>();
   private static final AtomicInteger runningInstanceMapId = new AtomicInteger(0);

   public static final int getNewInstanceMapId() {
      return runningInstanceMapId.addAndGet(1);
   }

   public EventScriptManager(GameServer cserv, String[] scripts) {
      for (String script : scripts) {
         if (!script.equals("")) {
            Invocable iv = this.getInvocable("scripts/" + (DBConfig.isGanglim ? "Royal" : "Jin") + "/event/" + script + ".js", null);
            if (iv != null) {
               this.events.put(script, new EventScriptManager.EventEntry(script, iv, new EventManager(cserv, iv, script)));
            }

            iv = null;
         }
      }
   }

   public final EventManager getEventManager(String event) {
      EventScriptManager.EventEntry entry = this.events.get(event);
      return entry == null ? null : entry.em;
   }

   public final void init() {
      for (EventScriptManager.EventEntry entry : this.events.values()) {
         try {
            ((ScriptEngine)entry.iv).put("em", entry.em);
            entry.iv.invokeFunction("init", null);
         } catch (Exception var4) {
         }
      }
   }

   public final void cancel() {
      for (EventScriptManager.EventEntry entry : this.events.values()) {
         entry.em.cancel();
      }
   }

   private static class EventEntry {
      public String script;
      public Invocable iv;
      public EventManager em;

      public EventEntry(String script, Invocable iv, EventManager em) {
         this.script = script;
         this.iv = iv;
         this.em = em;
      }
   }
}
