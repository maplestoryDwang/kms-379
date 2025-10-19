package script.ArcaneRiver;

import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class ChewChewIsland extends ScriptEngineNPC {

    @Script
    public void out_450002025() {
        if (getPlayer().getLevel() >= 215) {
            registerTransferField(450015020, 7);
        } else {
            getPlayer().dropMessage(5, "215레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void out_450015170() {
        if (getPlayer().getLevel() >= 215) {
            registerTransferField(450015180, 1);
        } else {
            getPlayer().dropMessage(5, "215레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void pt_BackToArc1() {
        if (getPlayer().getLevel() >= 210) {
            registerTransferField(450001250, 1);
        } else {
            getPlayer().dropMessage(5, "210레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void go_deepForest() {
        getPlayer().dropMessage(5, "이용하실 수 없는 포탈입니다.");
    }

    public void goToLehel() {
        if (getPlayer().getLevel() >= 220) {
            if (1 == self.askYesNo("#b무토#k...이제 배 부르다... #b움직여 줄까#k...?\r\n\r\n(무토가 비켜주면 아케인리버를 따라 다음 지역으로 갈 수 있습니다.)", ScriptMessageFlag.NpcReplacedByNpc)) {
                registerTransferField(450003000, 0);
            }
        } else {
            self.sayOk("무토 아직 배가 고프다.. 지나가고 싶으면 레벨 220을 넘기고 와라", ScriptMessageFlag.NpcReplacedByNpc);
        }
    }

}
