package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.childs.*;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;
import scripting.NPCScriptManager;

public class Lucid extends ScriptEngineNPC {

    public void lucid_accept() {
        String Message = "如果封锁不了路西德，會發生可怕的事情。 \r\n\r\n";
        if (DBConfig.isGanglim) {
        	Message += "#L0#<老闆：Lucid（#b正常模式#k）>申請入場。 #r（級別220以上）#g[" + getPlayer().getOneInfoQuestInteger(1234570, "lucid_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L1#<老闆：Lucid（#b硬模式#k）>申請入場。 #r（級別220以上）#g[" + getPlayer().getOneInfoQuestInteger(1234569, "lucid_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L2#<老闆：Lucid（#b正常練習模式#k）>申請入場。 #r（220級以上）#l#k\r\n";
            Message += "#L3#<老闆：Lucid（#b硬練習模式#k）>申請入場。 #r（220級以上）#l#k\r\n";
            Message += "#L4#<老闆：Lucid（#r健康模式#k）>申請入場。 #r（等級270以上）#l#k\r\n";
            Message += "#L5#不移動。 #l";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	Message += "#L0#<老闆：Lucid（#b正常#k）" + (single ? "（單曲）" : "（多個）") + ">申請入場。 #l\r\n";
            Message += "#L1#<老闆：Lucid（#b困難#k）" + (single ? "（單曲）" : "（多個）") + ">申請入場。 #l\r\n";
            Message += "#L2#<老闆：Lucid（#b正常#k）" + (single ? "（單曲）" : "（多個）") + ">申請進入練習模式。 #l\r\n";
            Message += "#L3#<老闆：Lucid（#b困難#k）" + (single ? "（單曲）" : "（多個）") + ">申請進入練習模式。 #l\r\n";
            Message += "#L4#<老闆：Lucid（#r健康#k）>申請入場。 #l\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Lucid" + (single ? "Single" : "Multi"));
            Message += "#L6#入場次數新增" + (single ? "（單曲）" : "（多個）") + " (#r" + (1-reset) +  "可回收#k）#l\r\n";
        }
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 5) return; //이동하지 않는다
        if (Menu == 6 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("合作積分不足。 擁有協同積分：" + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Lucid"+ (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Lucid"+ (single ? "Single" : "Multi"), String.valueOf(reset + 1));
        	self.sayOk("可進入次數新增。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        	return;
        }
        if (target.getParty() == null) {
            self.sayOk("1人以上組隊才能入場。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.sayOk("請通過派對現場進行。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (!target.getParty().isPartySameMap()) {
            self.sayOk("所有隊員必須在同一地圖上。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        switch (Menu) { //따로 제한되는거 없으면 바로 입장가능함
            case 0: { //노멀모드
                NormalLucidEnter fieldSet = (NormalLucidEnter) fieldSet("NormalLucidEnter");
                int enter = fieldSet.enter(target.getId(), false, 2);
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
                        self.sayOk("今天有消耗了所有入場次數的隊員。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.sayOk("最近一周內有打倒路西德的隊員。 Lucid加上正常模式和硬模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                break;
            }
            case 2: { //노멀 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                NormalLucidEnter fieldSet = (NormalLucidEnter) fieldSet("NormalLucidEnter");
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
                if (getPlayer().getQuestStatus(2000025) == 1) {
                    int Genesis = target.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#b-全員正在進行“噩夢之主路西德的痕迹”，以未完成路西德擊破條件的2人以下隊伍擊破\r\n-使用挑戰者的50個艾力克書擊破\r\n-2人隊伍時，最終傷害减少50%，如果1人失敗，所有隊員一起失敗，在死亡的隊員死亡的情况下，將不低於HP的1#r\n#L#0#L#0#執行任務。 #l\r\n#L1#不執行任務。 #l");
                    if (Genesis == 0) { //미션을 수행한다.
                        GenesisQuest = true;
                    } else if (Genesis == 1) { //미션을 수행하지 않는다.
                        GenesisQuest = false;
                    }
                }
                HardLucidEnter fieldSet = (HardLucidEnter) fieldSet("HardLucidEnter");
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
                    target.sayOk("該任務可以挑戰兩人以下。");
                    return;
                }
                if (enter == -20) {
                    target.sayOk("只能在進行中的配寘下進行勞恩斯任務。");
                    return;
                }
                if (enter == -30) {
                    target.sayOk("沒有空間接收挑戰者的艾力克薩斯。");
                    return;
                }
                if (enter == -3) {
                    if (DBConfig.isGanglim) {
                        self.sayOk("今天有消耗了所有入場次數的隊員。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.sayOk("最近一周內有打倒路西德的隊員。 達米安加上正常模式和硬模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                break;
            }
            case 3: { //하드 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                HardLucidEnter fieldSet = (HardLucidEnter) fieldSet("HardLucidEnter");
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
                break;
            }
            case 4: { // 헬 모드
                String hellMenu = ""; 
                if (DBConfig.isGanglim){
                    hellMenu = "#fs11##e<Lucid Hell模式>#n\r\n Hell模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少99%，適用50%的角色强化數值\r\n-體力新增\r\n-强化模式&角色體力恢復效果50%\r\n-死亡5次（組隊共亯）\r\n#r擊破時，有55%的概率#b#i4038#z4038# 1228#1個清點。 \r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                } else {
                    hellMenu = "#e<Lucid Hell模式>#n\r\n Hell模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少95%\r\n-體力新增\r\n-體力恢復效果適用50%\r\n-强化模式\r\n-死亡5次（組隊共亯）\r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                }
                int WelcomeToTheHell = target.askMenu(hellMenu);
                if (WelcomeToTheHell == 1) {
                    return;
                }
                if (getPlayer().getPartyMemberSize() >= 4) {
                	self.sayOk("最多可3人進入健康模式。");
                	return;
                }
                HellLucidEnter fieldSet = (HellLucidEnter) fieldSet("HellLucidEnter");
                int enter = fieldSet.enter(target.getId(), false, DBConfig.isGanglim ? 8 : 2);
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
                if (enter == -30) {
                    target.sayOk("沒有空間接收挑戰者的艾力克薩斯。");
                    return;
                }
                if (enter == -3) {
                    if (DBConfig.isGanglim) {
                        self.sayOk("最近一周內有一名隊員打倒了赫魯西德。 \r\n赫爾盧西德每週只能通關一次。 \r\n#r<清除歷史記錄將於每週一進行批量初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.sayOk("最近一周內有打倒路西德的隊員。 Lucid加上正常模式和硬模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                break;
            }
        }
    }
    
    public void midnightChaser_NPC() {
        if (DBConfig.isGanglim) {
            getClient().removeClickedNPC();
            NPCScriptManager.getInstance().start(getClient(), 9010100, "dreamBreaker_NPC", true);
            return;
        }
        lucid_accept();
        return;
    }

    public void dreamBreaker_NPC() {
        if (DBConfig.isGanglim) {
            return;
        }
        lucid_accept();
        return;
    }

    public void west_450004150() {
        if (self.askYesNo("你要停止戰鬥出去嗎？") == 1) {
            registerTransferField(getPlayer().getMap().getReturnMap().getId());
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
            }
            if (getPlayer().getBossMode() == 1) {
                getPlayer().setBossMode(0);
            }
        }
    }

    public void out_450004250() {
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

    public void out_450004300() {
        getPlayer().changeMap(450004000, 0);
    }
}
