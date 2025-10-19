package script.ArcaneRiver;


import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class HotelArcs extends ScriptEngineNPC {

    @Script
    public void west_410003020() {
        if (getPlayer().getLevel() >= 270) {
            playPortalSE();
            registerTransferField(410003150, 1);
        } else {
            getPlayer().dropMessage(5, "270레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void west_410003080() {
        if (getPlayer().getLevel() >= 270) {
            playPortalSE();
            registerTransferField(410003000, 2);
        } else {
            getPlayer().dropMessage(5, "270레벨 이상만 입장하실 수 있습니다.");
        }
    }
}
