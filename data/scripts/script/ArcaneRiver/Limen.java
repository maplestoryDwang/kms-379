package script.ArcaneRiver;


import constants.ServerConstants;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class Limen extends ScriptEngineNPC {

    public void BPReturn_dunkel() {
        initNPC(MapleLifeFactory.getNPC(9001000));
        if (getPlayer().getLevel() >= 255) {
            int v = target.askMenu("어디로 갈까?\r\n#b#L0#광장으로#l\r\n#L1#세계의 눈물 중단3#l");
            switch (v) {
                case 0: { //광장
                    registerTransferField(ServerConstants.TownMap);
                    break;
                }
                case 1: { //세계의 눈물 중단3
                    registerTransferField(450012120, 3);
                    break;
                }
            }
        } else {
            getPlayer().dropMessage(5, "255레벨 이상만 입장하실 수 있습니다.");
        }
    }

    public void BPReturn_BM() {
        initNPC(MapleLifeFactory.getNPC(9001000));
        if (getPlayer().getLevel() >= 255) {
            int v = target.askMenu("어디로 갈까?\r\n#b#L0#광장으로#l\r\n#L1#세계가 끝나는 곳 1-3#l");
            switch (v) {
                case 0: { //광장
                    registerTransferField(ServerConstants.TownMap);
                    break;
                }
                case 1: { //세계가 끝나는 곳 1-3
                    registerTransferField(450012320, 2);
                    break;
                }
            }
        } else {
            getPlayer().dropMessage(5, "255레벨 이상만 입장하실 수 있습니다.");
        }
    }

    public void east_450012320() {
        initNPC(MapleLifeFactory.getNPC(9001000));
        if (getPlayer().getLevel() >= 255) {
            int v = target.askMenu("어디로 갈까?\r\n#b#L0#거인의 심장#l\r\n#L1#세계가 끝나는 곳 2-1#l");
            switch (v) {
                case 0: { //광장
                    registerTransferField(450012500, 2);
                    break;
                }
                case 1: { //#세계가 끝나는 곳 2-1
                    registerTransferField(450012400, 3);
                    break;
                }
            }
        } else {
            getPlayer().dropMessage(5, "255레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450012000() {
        if (getPlayer().getLevel() >= 255) {
            registerTransferField(450012010, 1);
        } else {
            getPlayer().dropMessage(5, "255레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450012010() {
        if (getPlayer().getLevel() >= 255) {
            registerTransferField(450012100, 1);
        } else {
            getPlayer().dropMessage(5, "255레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450012100() {
        if (getPlayer().getLevel() >= 255) {
            registerTransferField(450012110, 1);
        } else {
            getPlayer().dropMessage(5, "255레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450012110() {
        if (getPlayer().getLevel() >= 255) {
            registerTransferField(450012120, 1);
        } else {
            getPlayer().dropMessage(5, "255레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450012120() {
        if (getPlayer().getLevel() >= 255) {
            registerTransferField(450012200, 3);
        } else {
            getPlayer().dropMessage(5, "255레벨 이상만 입장하실 수 있습니다.");
        }
    }
}
