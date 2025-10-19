package script.Boss;

import objects.fields.fieldset.FieldSet;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.MapleInventoryType;
import objects.users.MapleCharacter;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

public class BossOutPortal extends ScriptEngineNPC {
    public void pt_hillah() { // 힐라
        allBossOut();
    }

    public void pt_outrootaBoss() { // 루타비스
        allBossOut();
    }

    public void pt_portalNPC1() { // 아카이럼
        allBossOut();
    }

    public void will_out() { // 윌
        allBossOut();
    }

    public void pt_bh_bossOutN() { // 노말스우 포탈
        allBossOutPosition(350060300, 1);
    }

    public void pt_bh_bossOut() { // 하드스우 포탈
        allBossOutPosition(350060300, 1);
    }

    public void pt_bh_bossOutN_R() { // 노멀스우 보상맵포탈
        allBossOutPosition(350060300, 1);
    }

    public void pt_bh_bossOut_R() { // 하드스우 보상맵포탈
        allBossOutPosition(350060300, 1);
    }

    public void ptDemianOut() { // 데미안 포탈
        allBossOutPosition(105300303, 1);
    }

    public void ptDemianOut_R() { // 데미안 보상맵포탈
        allBossOutPosition(105300303, 1);
    }

    public void pt_GC_out() { // 듄켈 포탈 & 보상맵 포탈
        allBossOutPosition(450012200, 2);
    }

    public void pt_BM1_bossOut() { // 더스크 포탈 & 보상맵 포탈
        allBossOutPosition(450009301, 1);
    }

    public void pt_jinHillah_out() { // 진힐라 포탈 & 보상맵 포탈
        allBossOutPosition(450011990, 2);
    }

    public void serenOut() { // 세렌 게이트 & 보상맵포탈
        allBossOutPosition(410000670, 4);
    }

    public void allBossOut() {
        initNPC(MapleLifeFactory.getNPC(1530723));
        if (self.askYesNo("#fs11#전투를 중지하고 나갈까?", ScriptMessageFlag.NpcReplacedByUser) == 1) {
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
            }
            if (getPlayer().getBossMode() == 1) {
                getPlayer().setBossMode(0);
            }
            registerTransferField(getPlayer().getMap().getReturnMap().getId());
        }
    }

    public void allBossOutPosition(int Map, int Portal) {
        initNPC(MapleLifeFactory.getNPC(1530723));
        if (self.askYesNo("#fs11#전투를 중지하고 나갈까?", ScriptMessageFlag.NpcReplacedByUser) == 1) {
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
            }
            if (getPlayer().getBossMode() == 1) {
                getPlayer().setBossMode(0);
            }
            registerTransferField(Map, Portal);
        }
    }

}