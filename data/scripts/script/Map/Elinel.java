package script.Map;

import scripting.newscripting.ScriptEngineNPC;

public class Elinel extends ScriptEngineNPC {

    public void _101070100_east() {
        playPortalSE();
        registerTransferField(101070010, 2);
    }

    public void _101071000_west() {
        playPortalSE();
        registerTransferField(101070010, 1);
    }

    public void _101072000_east() {
        playPortalSE();
        registerTransferField(101073000, 3);
    }

    public void pt_moleking() {
        playPortalSE();
        registerTransferField(101073310, 1);
    }
}
