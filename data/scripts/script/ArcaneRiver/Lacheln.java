package script.ArcaneRiver;

import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class Lacheln extends ScriptEngineNPC {

    @Script
    public void out_450003000() {
        if (getPlayer().getLevel() >= 220) {
            registerTransferField(450002021, 1);
        } else {
            getPlayer().dropMessage(5, "220레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450003000_in04() {
        if (getPlayer().getLevel() >= 220) {
            registerTransferField(450003010, 2);
        } else {
            getPlayer().dropMessage(5, "220레벨 이상만 입장하실 수 있습니다.");
        }
    }

    public void arcana_fish() {
        if (getPlayer().getLevel() >= 225) {
            if (1 == self.askYesNo("#b(지금 바로 날치를 타고 아르카나로 이동하시겠습니까?)", ScriptMessageFlag.NpcReplacedByNpc)) {
                registerTransferField(450005010, 1);
            }
        } else {
            self.sayOk("225레벨 이상만 아르카나로 이동하실 수 있습니다.", ScriptMessageFlag.NpcReplacedByNpc);
        }
    }

    @Script
    public void go_ballroom() {
        if (getPlayer().getLevel() >= 220) {
            registerTransferField(450003400, 4);
        } else {
            getPlayer().dropMessage(5, "220레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void go_clockTower() {
        if (getPlayer().getLevel() >= 220) {
            registerTransferField(450003500, 1);
        } else {
            getPlayer().dropMessage(5, "220레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void top_450003500() {
        if (getPlayer().getLevel() >= 220) {
            registerTransferField(450003510, 1);
        } else {
            getPlayer().dropMessage(5, "220레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void top_450003510() {
        if (getPlayer().getLevel() >= 220) {
            registerTransferField(450003520, 1);
        } else {
            getPlayer().dropMessage(5, "220레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void top_450003520() {
        if (getPlayer().getLevel() >= 220) {
            registerTransferField(450003530, 2);
        } else {
            getPlayer().dropMessage(5, "220레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void top_450003540() {
        if (getPlayer().getLevel() >= 220) {
            registerTransferField(450003600, 1);
        } else {
            getPlayer().dropMessage(5, "220레벨 이상만 입장하실 수 있습니다.");
        }
    }
}
