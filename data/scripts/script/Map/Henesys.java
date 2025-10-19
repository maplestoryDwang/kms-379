package script.Map;

import constants.GameConstants;
import objects.users.skills.SkillFactory;
import scripting.newscripting.ScriptEngineNPC;

public class Henesys extends ScriptEngineNPC {

    public void enterGhostMS() {
        playPortalSE();
        registerTransferField(100020401, 1);
    }
}
