package script.FieldSet;

import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class YutaFarm extends ScriptEngineNPC {

    @Script
    public void out_yFarm_field() {
        getPlayer().dropMessage(5, "@광장 명령어를 통해 이동해주세요.");
    }
}
