package scripting.newscripting;

import java.util.HashMap;
import java.util.Map;

public class ScriptHolder {
   public final Map<String, Class<? extends ScriptEngineNPC>> _scripts = new HashMap<>();

   public ScriptHolder(ScriptHolder holder) {
      if (holder != null) {
         this._scripts.putAll(holder._scripts);
      }
   }

   void clear() {
      this._scripts.clear();
   }
}
