package script.Boss;

import constants.ServerConstants;
import objects.fields.fieldset.childs.HardBlackHeavenBossEnter;
import objects.fields.fieldset.childs.HellBlackHeavenBossEnter;
import objects.fields.fieldset.childs.NormalBlackHeavenBossEnter;
import objects.fields.fieldset.childs.TenguEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

public class Tengu extends ScriptEngineNPC {


    public void tengu_enter() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        String v0 = "為了打倒滕古，我們移動嗎？ \r\n\r\n";
        v0 += "前往#L0#天狗地圖（#b正常模式#k）。 （250級以上）#l\r\n\r\n\r\n";
        v0 += "#L5#不移動。 #l\r\n\r\n";
        int Menu = target.askMenu(v0, ScriptMessageFlag.BigScenario);
        if (Menu == 5) return; //이동하지 않는다
        if (target.getParty() == null) {
            self.sayOk("1人以上組隊才能入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.sayOk("請通過派對現場進行。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (!target.getParty().isPartySameMap()) {
            self.sayOk("所有隊員必須在同一地圖上。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        switch (Menu) { //따로 제한되는거 없으면 바로 입장가능함
            case 0: { //노멀모드
                TenguEnter fieldSet = (TenguEnter) fieldSet("TenguEnter");
                int enter = fieldSet.enter(target.getId(), 2);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("今天有打倒天狗的隊員。 \r\n#r<清除歷史記錄將在每天午夜進行批量初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == -1 || enter == 4)) {
                    self.sayOk("入場限制次數不足或有等級限制不符的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -2) {
                    self.sayOk("因為還有限制入場時間的隊員，所以無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
        }
    }
}
