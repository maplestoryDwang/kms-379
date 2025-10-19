package script.Boss;

import constants.ServerConstants;
import objects.fields.fieldset.childs.*;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

public class Mitsuhide extends ScriptEngineNPC {


    public void mitsuhide_enter() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        String v0 = "미츠히데를 쓰러뜨리기 위해 이동할까?\r\n\r\n";
        v0 += "#L0#미츠히데 맵(#b노멀 모드#k)으로 이동 한다.(레벨 250이상)#l\r\n\r\n\r\n";
        v0 += "#L1#미츠히데 맵(#b하드 모드#k)으로 이동 한다.(레벨 250이상)#l\r\n\r\n\r\n";
        v0 += "#L5#이동하지 않는다.#l\r\n\r\n";
        int Menu = target.askMenu(v0, ScriptMessageFlag.BigScenario);
        if (Menu == 5) return; //이동하지 않는다
        if (target.getParty() == null) {
            self.sayOk("1인 이상 파티를 맺어야만 입장할 수 있습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.sayOk("파티장을 통해 진행해 주십시오.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (!target.getParty().isPartySameMap()) {
            self.sayOk("파티원이 모두 같은맵에 있어야 합니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        switch (Menu) { //따로 제한되는거 없으면 바로 입장가능함
            case 0:
            case 1: { //노멀모드
                MitsuhideEnter fieldSet = (MitsuhideEnter) fieldSet("MitsuhideEnter");
                int enter = fieldSet.enter(target.getId(), false, Menu == 0, 2);
                if (enter == 6) {
                    self.sayOk("이용 가능한 인스턴스가 없습니다. 다른 채널을 이용해주세요.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("보스 티어가 부족한 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("오늘 미츠히데를 쓰러뜨린 파티원이 있습니다. \r\n#r<클리어 기록은 매일 자정에 일괄 초기화됩니다.>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == -1 || enter == 4)) {
                    self.sayOk("입장 제한횟수가 부족하거나 레벨 제한이 맞지 않는 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -2) {
                    self.sayOk("입장 제한시간이 남은 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
        }
    }
}
