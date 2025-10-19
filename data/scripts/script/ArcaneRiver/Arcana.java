package script.ArcaneRiver;

import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class Arcana extends ScriptEngineNPC {

    @Script
    public void _450005010_pt00() {
        if (getPlayer().getLevel() >= 225) {
            registerTransferField(450005015, 1);
        } else {
            getPlayer().dropMessage(5, "225레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450005015_east00() {
        if (getPlayer().getLevel() >= 225) {
            registerTransferField(450005100, 2);
        } else {
            getPlayer().dropMessage(5, "225레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450005130_east00() {
        if (getPlayer().getLevel() >= 225) {
            registerTransferField(450005000, 1);
        } else {
            getPlayer().dropMessage(5, "225레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450005000_out10() {
        if (getPlayer().getLevel() >= 225) {
            registerTransferField(450005200, 1);
        } else {
            getPlayer().dropMessage(5, "225레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450005300_col00() {
        if (getPlayer().getLevel() >= 225) {
            registerTransferField(450005400, 1);
        } else {
            getPlayer().dropMessage(5, "225레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450005400_pt00() {
        if (getPlayer().getLevel() >= 225) {
            registerTransferField(450005500, 2);
        } else {
            getPlayer().dropMessage(5, "225레벨 이상만 입장하실 수 있습니다.");
        }
    }

    public void morass_fish() {
        if (getPlayer().getLevel() >= 230) {
            if (1 == target.askAccept("자, 그럼 어서 날치를 타고 다음 지역으로 이동할까?\r\n#b(수락 시 자동 이동합니다.)", ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage)) {
                registerTransferField(450006000, 0);
            }
        } else {
            self.sayOk("230레벨 이상만 모라스로 이동하실 수 있습니다.", ScriptMessageFlag.NpcReplacedByNpc);
        }
    }

}
