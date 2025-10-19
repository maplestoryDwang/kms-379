package script.Map;

import scripting.newscripting.ScriptEngineNPC;

public class MushroomCastle extends ScriptEngineNPC {

    public void _106030100_IP() {
        playPortalSE();
        registerTransferField(106030000, 2);
    }

    public void _106030201_east00() {
        playPortalSE();
        registerTransferField(106030210, 4);
    }

    public void _106030211_out() {
        playPortalSE();
        registerTransferField(106030200, 4);
    }

    public void _106030200_out00() {
        playPortalSE();
        registerTransferField(106030300, 1);
    }

    public void _106030302_out00() {
        playPortalSE();
        registerTransferField(106030300, 1);
    }

    public void viking_cannon1() {
        registerTransferField(106030000, 1);
    }

    public void viking_cannon2() {
        registerTransferField(106030000, 1);
    }

    public void _106030501_in00() {
        playPortalSE();
        registerTransferField(106030600, 0);
    }

    public void _106030800_out() {
        playPortalSE();
        registerTransferField(106030600, 3);
    }

    public void _106030600_east00() {
        playPortalSE();
        registerTransferField(106030800, 1);
    }
}
