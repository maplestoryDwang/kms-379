package script.ArcaneRiver;

import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;
import objects.fields.gameobject.lifes.MapleLifeFactory;

public class Morass extends ScriptEngineNPC {

    @Script
    public void center_450006240() {
        if (getPlayer().getLevel() >= 230) {
            registerTransferField(450006400, 1);
        } else {
            getPlayer().dropMessage(5, "230레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450006400() {
        if (getPlayer().getLevel() >= 230) {
            registerTransferField(450006410, 1);
        } else {
            getPlayer().dropMessage(5, "230레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void morassDQ_34285() {
        if (getPlayer().getLevel() >= 230) {
            if (getPlayer().getQuestStatus(34285) == 1) {
                /*
                    940204309 - pt_940204309
                    940204410 - pt_940204410
                    940204430 - pt_940204430
                    940204450 - pt_940204450
                    940204470 - pt_940204470
                 */
                registerTransferField(940204309, 1);
            } else {
                getPlayer().dropMessage(5, "이곳은 아직 에르다가 안정적인 것 같다.");
            }
        } else {
            getPlayer().dropMessage(5, "230레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void morassDQ_MP() {
        if (getPlayer().getLevel() >= 230) {
            getPlayer().dropMessage(5, "이곳엔 별다른 문제가 없어보인다.");
            //이곳은 아직 에르다가 안정적인 것 같다.
        } else {
            getPlayer().dropMessage(5, "230레벨 이상만 입장하실 수 있습니다.");
        }
    }

    public void pt_940204309() {
        initNPC(MapleLifeFactory.getNPC(2007));
        if (getPlayer().getLevel() >= 230) {
            if (getPlayer().getItemQuantity(4036328, false) >= 50) {
                int v0 = target.askMenu("이 정도면 충분히 #i4036328# #b#t4036328##k을 모은 것 같다.\r\n트뤼에페 광장에 있는 쟝에게 가져다 주자\r\n#L0#트뤼에페 광장으로#l\r\n#L1#연구실로#l", ScriptMessageFlag.FlipImage, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario);
                if (v0 == 0) {
                    registerTransferField(450006130);
                } else if (v0 == 1) {
                    registerTransferField(450006240);
                }
            } else {
                int v0 = target.askMenu("아직 충분히 #i4036328# #b#t4036328##k을 모으지 못했다.\r\n어떻게 할까?\r\n#L0#트뤼에페 광장으로#l\r\n#L1#연구실로#l", ScriptMessageFlag.FlipImage, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario);
                if (v0 == 0) {
                    registerTransferField(450006130);
                } else if (v0 == 1) {
                    registerTransferField(450006240);
                }
            }
        } else {
            getPlayer().dropMessage(5, "230레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east00_450006040() {
        if (getPlayer().getLevel() >= 230) {
            registerTransferField(450006130, 4);
        } else {
            getPlayer().dropMessage(5, "230레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450006320() {
        if (getPlayer().getLevel() >= 235) {
            registerTransferField(450006330, 3);
        } else {
            getPlayer().dropMessage(5, "235레벨 이상만 입장하실 수 있습니다.");
        }
    }

}
