package script.ArcaneRiver;

import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class MoonBridge extends ScriptEngineNPC {

    @Script
    public void _450009100_up00() {
        if (getPlayer().getLevel() >= 245) {
            registerTransferField(450009200, 5);
        } else {
            getPlayer().dropMessage(5, "245레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450009200_down00() {
        if (getPlayer().getLevel() >= 245) {
            registerTransferField(450009100, 4);
        } else {
            getPlayer().dropMessage(5, "245레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450009200_up00() {
        if (getPlayer().getLevel() >= 245) {
            registerTransferField(450009300, 4);
        } else {
            getPlayer().dropMessage(5, "245레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450009300_down00() {
        if (getPlayer().getLevel() >= 245) {
            registerTransferField(450009200, 4);
        } else {
            getPlayer().dropMessage(5, "245레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void _450009300_up00() {
        if (getPlayer().getLevel() >= 245) {
            registerTransferField(450009301, 2);
        } else {
            getPlayer().dropMessage(5, "245레벨 이상만 입장하실 수 있습니다.");
        }
    }



    public void BM1_Ship() {
        if (getPlayer().getLevel() >= 245) {
            if (getPlayer().getMap().getId() == 450009300 && getPlayer().getLevel() >= 250) {
                int v = target.askMenu("소형 비공정을 타고 원하는 갑판으로 갈 수 있다.\r\n어디로 갈까?\r\n\r\n #b#L0#추락 지점#l\r\n\r\n #k#L2#소형 비공정을 이용하지 않는다.", ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
                switch (v) {
                    case 0: { //추락 지점
                        registerTransferField(450011120, 3);
                        break;
                    }
                    case 2: //소형비공정 이용x
                        break;
                }
            } else if (getPlayer().getLevel() >= 245) {
                int v = target.askMenu("소형 비공정을 타고 원하는 갑판으로 갈 수 있다.\r\n어디로 갈까?\r\n\r\n #b#L0#전초기지#l\r\n\r\n #k#L2#소형 비공정을 이용하지 않는다.", ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
                switch (v) {
                    case 0: { //추락 지점
                        registerTransferField(450009050, 5);
                        break;
                    }
                    case 2: //소형비공정 이용x
                        break;
                }
            }
        } else {
            getPlayer().dropMessage(5, "245레벨 이상만 이용하실 수 있습니다.");
        }
    }

    public void _450009050_out00() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() >= 245) {
            if (1 == target.askYesNo("문브릿지로 이동할까?", ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage)) {
                registerTransferField(450009100, 1);
            }
        } else {
            getPlayer().dropMessage(5, "245레벨 이상만 이용하실 수 있습니다.");
        }
    }


}
