package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.childs.HardDemianEnter;
import objects.fields.fieldset.childs.HellDemianEnter;
import objects.fields.fieldset.childs.NormalDemianEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class Demian extends ScriptEngineNPC {


    public void fallenWT_boss() {
        initNPC(MapleLifeFactory.getNPC(1540795));
        String Message = "為了打倒戴米安，我們要移動到“墮落世界的頂峰”嗎？ \r\n\r\n";
        if (DBConfig.isGanglim) {
        	Message += "#L0#進入墮落世界的正常狀態（#b正常模式#k）。 #r（等級200以上）#k#g[" + getPlayer().getOneInfoQuestInteger(1234570, "demian_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L1#進入墮落世界的頂峰（#b硬模式#k）。 #r（等級200以上）#k#g[" + getPlayer().getOneInfoQuestInteger(1234569, "demian_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L3#進入墮落世界的正常（#b正常練習模式#k）。 #r（級別200或更高）#k#l\r\n";
            Message += "#L4#進入墮落世界的頂峰（#b硬練習模式#k）。 #r（級別200或更高）#k#l\r\n";
            Message += "#L2#進入墮落世界的頂峰（#r健康模式#k）。 #r（等級270以上）#k#l\r\n";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	Message += "#L0#墮落的世界正常（#b正常模式#k）" + (single ? "（單曲）" : "（多個）") + "移動到。 （級別200或更高）#l\r\n";
            Message += "#L1#墮落世界正常（#b硬模式#k）" + (single ? "（單曲）" : "（多個）") + "移動到。 （級別200或更高）#l\r\n";
            Message += "#L3#墮落的世界正常（#b正常練習模式#k）" + (single ? "（單曲）" : "（多個）") + "移動到。 （級別200或更高）#l\r\n";
            Message += "#L4#墮落世界正常（#b硬練習模式#k）" + (single ? "（單曲）" : "（多個）") + "移動到。 （級別200或更高）#l\r\n";
            Message += "#L2#進入墮落世界的頂峰（#r健康模式#k）。 （270級以上）#l\r\n\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Demian" + (single ? "Single" : "Multi"));
            Message += "#L6#可進入次數新增" + (single ? "（單曲）" : "（多個）") + "(" + (1-reset) + "可能）#l\r\n\r\n";
        }
        Message += "#L5#不移動。 #l";
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 5) return; //이동하지 않는다
        if (Menu == 6 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("合作積分不足。 擁有協同積分：" + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Demian" + (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Demian" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
        	self.sayOk("可進入次數新增。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        	return;
        }
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
                NormalDemianEnter fieldSet = (NormalDemianEnter) fieldSet("NormalDemianEnter");
                int enter = fieldSet.enter(target.getId(), false, 0);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("最近一周內有打倒戴米安的隊員。 達米安的正常模式、硬模式、健康模式加起來每週只能清除一次\r\n#r<清除記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
            case 3: { //노멀 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                NormalDemianEnter fieldSet = (NormalDemianEnter) fieldSet("NormalDemianEnter");
                int enter = fieldSet.enter(target.getId(), true, 2);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == 4) {
                    self.sayOk("有等級限制不符的隊員，無法進入。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -1) {
                    self.sayOk("練習模式每天只能進行20次。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
            case 1: { //하드모드
                boolean GenesisQuest = false;
                if (getPlayer().getQuestStatus(2000023) == 1) {
                    int Genesis = target.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊敗\r\n-减少到5個折扣\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l");
                    if (Genesis == 0) { //미션을 수행한다.
                        GenesisQuest = true;
                    } else if (Genesis == 1) { //미션을 수행하지 않는다.
                        GenesisQuest = false;
                    }
                }
                HardDemianEnter fieldSet = (HardDemianEnter) fieldSet("HardDemianEnter");
                int enter = fieldSet.enter(target.getId(), false, GenesisQuest, 2);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -10) {
                    target.sayOk("該任務需要一個人挑戰。");
                    return;
                }
                if (enter == -3) {
                    self.sayOk("最近一周內有打倒戴米安的隊員。 達米安的正常模式、硬模式、健康模式加起來每週只能清除一次\r\n#r<清除記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
            case 4: { //하드 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                HardDemianEnter fieldSet = (HardDemianEnter) fieldSet("HardDemianEnter");
                int enter = fieldSet.enter(target.getId(), true, false, 2);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == 4)) {
                    self.sayOk("有等級限制不符的隊員，無法進入。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -1) {
                    self.sayOk("練習模式每天只能進行20次。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
            }
            break;
            case 2: { //헬모드
                String hellMenu = "";
                if (DBConfig.isGanglim) {
                    hellMenu = "#fs11##e<達米安健康模式>#n\r\n健康模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少99%，適用角色强化數值50%\r\n-體力新增\r\n-强化模式\r\n-死亡5次（組隊共亯）\r\n#r擊破時，有55%的概率獲得#b#i4038#z4038#1個庫存。 \r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                } else {
                    hellMenu = "#e<達米安健康模式>#n\r\n健康模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少95%\r\n-體力新增\r\n-强化模式\r\n-死亡計數5次（組隊共亯）\r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                }
                int WelcomeToTheHell = target.askMenu(hellMenu);
                if (WelcomeToTheHell != 0) {
                    return;
                }
                HellDemianEnter fieldSet = (HellDemianEnter) fieldSet("HellDemianEnter");
                int enter = fieldSet.enter(target.getId(), DBConfig.isGanglim ? 8 : 2);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -10) {
                    target.sayOk("該任務需要3人以下才能挑戰。");
                    return;
                }
                if (enter == -3) {
                    if (DBConfig.isGanglim) {
                        self.sayOk("最近一周內有打倒赫爾·戴米安的隊員。 \r\n赫爾德米安一周只能通關一次。 \r\n#r<清除歷史記錄將於每週一進行批量初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.sayOk("最近一周內有打倒戴米安的隊員。 達米安的正常模式、硬模式、健康模式加起來每週只能清除一次\r\n#r<清除記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    }
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
            }
            break;
        }
    }

    public void DemianOut() {
        if (self.askYesNo("你要停止戰鬥出去嗎？") == 1) {
            registerTransferField(getPlayer().getMap().getReturnMap().getId());
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
            }
            if (getPlayer().getBossMode() == 1) {
                getPlayer().setBossMode(0);
            }
        }
    }
}
