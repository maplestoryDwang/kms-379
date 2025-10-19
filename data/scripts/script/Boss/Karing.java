package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.childs.*;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;
import scripting.NPCScriptManager;

public class Karing extends ScriptEngineNPC {

    public void karing_enterGate() {
        if (!(getPlayer().getClient().isGm() || getPlayer().isGM())) {
            target.say("現在無法入場。");
            return;
        }
        String Message = "為了與卡林戰鬥而移動嗎？ （#r等級275以上#k可入場）。 \r\n\r\n";
        if (DBConfig.isGanglim) {
            Message += "#L0#<老闆：卡林（#b正常模式#k）>申請入場。 #r（級別220以上）#g[" + getPlayer().getOneInfoQuestInteger(1234570, "Karing_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L1#<老闆：卡林（#b硬模式#k）>申請入場。 #r（級別220以上）#g[" + getPlayer().getOneInfoQuestInteger(1234569, "Karing_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L2#<老闆：卡林（#b正常練習模式#k）>申請入場。 #r（220級以上）#l#k\r\n";
            Message += "#L3#<老闆：卡林（#b硬練習模式#k）>申請入場。 #r（220級以上）#l#k\r\n";
            Message += "#L4#<老闆：卡林（#r健康模式#k）>申請入場。 #r（等級270以上）#l#k\r\n";
            Message += "#L5#不移動。 #l";
        }
        else {
            boolean single = getPlayer().getPartyMemberSize() == 1;
            Message += "#L0#<老闆：Karing（#b正常#k）" + (single ? "（單曲）" : "（多個）") + ">申請入場。 #l\r\n";
            Message += "#L1#<老闆：卡林（#b困難#k）" + (single ? "（單曲）" : "（多個）") + ">申請入場。 #l\r\n";
            Message += "#L2#<老闆：Karing（#b正常#k）" + (single ? "（單曲）" : "（多個）") + ">申請進入練習模式。 #l\r\n";
            Message += "#L3#<老闆：卡林（#b困難#k）" + (single ? "（單曲）" : "（多個）") + ">申請進入練習模式。 #l\r\n";
            Message += "#L4#<老闆：卡林（#r赫爾#k）>申請入場。 #l\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Karing" + (single ? "Single" : "Multi"));
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
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Karing"+ (single ? "Single" : "Multi"));
            if (reset > 0) {
                self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                return;
            }
            getPlayer().gainTogetherPoint(-150);
            getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Karing"+ (single ? "Single" : "Multi"), String.valueOf(reset + 1));
            self.sayOk("可進入次數新增。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (target.getParty() == null) {
        	int partyReq = target.askYesNo("必須有派對才能入場。 您要創建派對嗎？");
        	if (partyReq != 1) {
        		return;
        	}
        	else {
        		getPlayer().createParty();
        	}
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
                NormalKaringEnter fieldSet = (NormalKaringEnter) fieldSet("NormalKaringEnter");
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
                        self.sayOk("最近一周內有一個隊員打倒了卡林。 卡環加上正常模式和困難模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                NormalKaringEnter fieldSet = (NormalKaringEnter) fieldSet("NormalKaringEnter");
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
                HardKaringEnter fieldSet = (HardKaringEnter) fieldSet("HardKaringEnter");
                int enter = fieldSet.enter(target.getId(), 2);
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
                        self.sayOk("最近一周內有一個隊員打倒了卡林。 達米安加上正常模式和硬模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                HardKaringEnter fieldSet = (HardKaringEnter) fieldSet("HardKaringEnter");
                int enter = fieldSet.enter(target.getId(), 2);
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
                    hellMenu = "#fs11##e<卡林健康模式>#n\r\n健康模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少99%，適用50%的角色强化數值\r\n-體力新增\r\n-强化的模式&角色體力恢復效果50%\r\n-死亡5次（組隊共亯）\r\n#r擊破時，有50%的概率#b#i4038#z# 1228#1228#4038#1個清點。 \r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                } else {
                    hellMenu = "#e<卡林健康模式>#n\r\n健康模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少95%\r\n-體力新增\r\n-體力恢復效果適用50%\r\n-强化模式\r\n-死亡5次（組隊共亯）\r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                }
                int WelcomeToTheHell = target.askMenu(hellMenu);
                if (WelcomeToTheHell == 1) {
                    return;
                }
                if (getPlayer().getPartyMemberSize() >= 4) {
                    self.sayOk("最多可3人進入健康模式。");
                    return;
                }
                ExtremeKaringEnter fieldSet = (ExtremeKaringEnter) fieldSet("ExtremeKaringEnter");
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
                        self.sayOk("有一個隊員在最近一周內打倒了赫爾·卡林。 \r\n健身環每週只能通關一次。 \r\n#r<清除歷史記錄將於每週一進行批量初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.sayOk("最近一周內有一個隊員打倒了卡林。 卡環加上正常模式和困難模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
        karing_enterGate();
        return;
    }

    public void dreamBreaker_NPC() {
        if (DBConfig.isGanglim) {
            return;
        }
        karing_enterGate();
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
