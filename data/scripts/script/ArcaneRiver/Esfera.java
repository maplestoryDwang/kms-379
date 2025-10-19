package script.ArcaneRiver;

import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class Esfera extends ScriptEngineNPC {

    @Script
    public void _450006330_door() {
        if (getPlayer().getLevel() >= 235) {
            registerTransferField(450007000, 2);
        } else {
            getPlayer().dropMessage(5, "235레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void pt_450007030() {
        if (getPlayer().getLevel() >= 235) {
            registerTransferField(450007040, 3);
        } else {
            getPlayer().dropMessage(5, "235레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450007160() {
        if (getPlayer().getLevel() >= 240) {
            registerTransferField(450007170, 1);
        } else {
            getPlayer().dropMessage(5, "240레벨 이상만 입장하실 수 있습니다.");
        }
    }

    public void npc_3003533() {
        if (getPlayer().getLevel() >= 235) {
            if (1 == target.askYesNo("안으로 들어가야한다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage)) {
                registerTransferField(450007200, 0);
            }
        } else {
            getPlayer().dropMessage(5, "235레벨 이상만 입장하실 수 있습니다.");
        }
    }


}
