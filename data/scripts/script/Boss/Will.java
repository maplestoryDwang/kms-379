package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.childs.*;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

public class Will extends ScriptEngineNPC {

    public void will_enterGate() {
        String Message = "為了封锁威爾，要不要移動到#b轉彎的走廊#k？ \r\n\r\n";
        if (DBConfig.isGanglim) {
            Message += "前往#L0#旋轉的走廊（#b正常模式#k）。 #r（等級235以上）#g[" + getPlayer().getOneInfoQuestInteger(1234570, "will_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "前往#L1#旋轉的回廊（#b硬模式#k）。 #r（等級235以上）#g[" + getPlayer().getOneInfoQuestInteger(1234569, "will_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "前往#L2#旋轉的走廊（#b正常練習模式#k）。 #r（等級235以上）#k#l\r\n";
            Message += "前往#L3#旋轉的走廊（#b硬練習模式#k）。 #r（等級235以上）#k#l\r\n";
            Message += "前往#L7#旋轉的走廊（#r健康模式#k）。 #r（等級270以上）#k#l\r\n";
            Message += "#L4#不移動。 #l\r\n\r\n";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
            Message += "#L5##b旋轉的回廊（异地模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（等級235以上）#k#l\r\n";
            Message += "#L0##b旋轉的回廊（正常模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（等級235以上）#k#l\r\n";
            Message += "#L1##b旋轉的回廊（硬模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（等級235以上）#k#l\r\n";
            Message += "#L6##b旋轉的回廊（易地練習模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（等級235以上）#k#l\r\n";
            Message += "#L2##b旋轉的回廊（正常練習模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（等級235以上）#k#l\r\n";
            Message += "#L3##b旋轉的回廊（硬練習模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（等級235以上）#k#l\r\n";
            Message += "前往#L7##b旋轉的回廊（#r健康模式#b）。 #r（等級270以上）#k#l\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Will" + (single ? "Single" : "Multi"));
            Message += "#L8#可進入次數新增" + (single ? "（單曲）" : "（多個）") + "(" + (1-reset) + "可能）#l\r\n\r\n\r\n";
            Message += "#L4#不移動。 #l\r\n\r\n";
            
        }
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 4) return; //이동하지 않는다.
        if (target.getParty() == null) {
            self.sayOk("1人以上組隊才能入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (Menu == 8 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("合作積分不足。 擁有協同積分：" + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Will" + (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Will" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
        	self.sayOk("可進入次數新增。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                NormalWillEnter fieldSet = (NormalWillEnter) fieldSet("NormalWillEnter");
                int enter = fieldSet.enter(target.getId(), false, 3);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("有一個隊員在最近一周內打倒了威爾。 威爾加上正常模式和困難模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
            case 2: { //노멀 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                NormalWillEnter fieldSet = (NormalWillEnter) fieldSet("NormalWillEnter");
                int enter = fieldSet.enter(target.getId(), true, 3);
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
                if (getPlayer().getQuestStatus(2000024) == 1) {
                    int Genesis = target.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#b-全體正在進行“蜘蛛之王威爾的痕迹”，未完成威爾擊破條件的2人以下隊伍擊破\r\n-被敵人擊打時受到的傷害新增10%\r\n-2人隊伍時最終傷害减少50%，即使有1人失敗，所有隊員都會一起失敗，在死亡狀態下，將無法减少到威爾的HP1。 #l\r\n#L1#不執行任務。 #l");
                    if (Genesis == 0) { //미션을 수행한다.
                        GenesisQuest = true;
                    } else if (Genesis == 1) { //미션을 수행하지 않는다.
                        GenesisQuest = false;
                    }
                }
                HardWillEnter fieldSet = (HardWillEnter) fieldSet("HardWillEnter");
                int enter = fieldSet.enter(target.getId(), false, GenesisQuest, 5);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -10) {
                    target.sayOk("該任務可以挑戰兩人以下。");
                    return;
                }
                if (enter == -20) {
                    target.sayOk("只能在進行中的配寘下進行勞恩斯任務。");
                    return;
                }
                if (enter == -3) {
                    self.sayOk("有一個隊員在最近一周內打倒了威爾。 威爾加上正常模式、困難模式和健康模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
            case 3: { //하드 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                HardWillEnter fieldSet = (HardWillEnter) fieldSet("HardWillEnter");
                int enter = fieldSet.enter(target.getId(), true, false, 5);
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
            case 5: { //이지모드
                EasyWillEnter fieldSet = (EasyWillEnter) fieldSet("EasyWillEnter");
                int enter = fieldSet.enter(target.getId(), false, 3);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("有一個隊員在最近一周內打倒了威爾。 威爾加上正常模式、困難模式和健康模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
            case 6: { //이지 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                EasyWillEnter fieldSet = (EasyWillEnter) fieldSet("EasyWillEnter");
                int enter = fieldSet.enter(target.getId(), true, 3);
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
            case 7: { //헬모드
                String hellMenu = ""; 
                if (DBConfig.isGanglim){
                    hellMenu = "#fs11##e<威爾健康模式>#n\r\n健康模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少99%，角色强化數值適用50%\r\n-體力新增\r\n-死亡5次（組隊共亯）\r\n#r擊破時，有55%的概率清點#b#i4031228#z4038###r2個。 \r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                } else {
                    hellMenu = "#e<健康模式>#n\r\n健康模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少95%\r\n-體力新增\r\n-死亡計數5次（組隊共亯）\r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                }
                int WelcomeToTheHell = target.askMenu(hellMenu);
                if (WelcomeToTheHell == 1) {
                    return;
                }
                HellWillEnter fieldSet = (HellWillEnter) fieldSet("HellWillEnter");
                int enter = fieldSet.enter(target.getId(), DBConfig.isGanglim ? 8 : 2);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    if (DBConfig.isGanglim) {
                        self.sayOk("最近一周內有打倒赫爾威爾的隊員。 \r\n Helwill每週只能通關一次。 \r\n#r<清除歷史記錄將於每週一進行批量初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.sayOk("有一個隊員在最近一周內打倒了威爾。 威爾加上正常模式、困難模式和健康模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.NpcReplacedByNpc);
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
                if (enter == 3) {
                    self.sayOk("威爾（健康模式）只能挑戰1~3人組隊。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
        }
    }

    public void will_out() {
        if (self.askYesNo("你要停止戰鬥出去嗎？") == 1) {
            getPlayer().setRegisterTransferFieldTime(0);
            getPlayer().setRegisterTransferField(0);
            registerTransferField(getPlayer().getMap().getReturnMap().getId());
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
            }
            if (getPlayer().getBossMode() == 1) {
                getPlayer().setBossMode(0);
            }
        }
    }
}
