package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.childs.*;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class GuardianAngelSlime extends ScriptEngineNPC {


    public void slime_enterGate() {
        String Message = "感覺到莫名其妙的氣息。 為了與衛報天使斯萊姆戰鬥而移動嗎？ \r\n\r\n";
        if (DBConfig.isGanglim) {
        	Message += "#L0#申請進入《衛報天使史萊姆》（#b諾馬爾模式#k）。 #r（等級210以上）#g[" + getPlayer().getOneInfoQuestInteger(1234570, "guardian_angel_slime_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "申請進入#L1#衛報天使史萊姆（#b混沌模式#k）。 #r（等級210以上）#g[" + getPlayer().getOneInfoQuestInteger(1234569, "guardian_angel_slime_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "申請進入#L2#衛報天使史萊姆（#b無言練習模式#k）。 #r（級別210或更高）#k#l\r\n";
            Message += "申請進入#L3#衛報天使史萊姆（#b混沌練習模式#k）。 #r（級別210或更高）#k#l\r\n\r\n";
            Message += "#L4#不移動。 #l";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	Message += "#L0##b與守護天使史萊姆的戰鬥（正常模式）" + (single ? "（單曲）" : "（多個）") + "為移動。 #r（級別210或更高）#k#l\r\n";
            Message += "#L1##b與衛士天使史萊姆的戰鬥（混沌模式）" + (single ? "（單曲）" : "（多個）") + "為移動。 #r（級別210或更高）#k#l\r\n";
            Message += "#L2##b與守護天使史萊姆的戰鬥（正常練習模式）" + (single ? "（單曲）" : "（多個）") + "為移動。 #r（級別210或更高）#k#l\r\n";
            Message += "#L3##b與衛士天使史萊姆的戰鬥（混沌練習模式）" + (single ? "（單曲）" : "（多個）") + "為移動。 #r（級別210或更高）#k#l\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "GuardianSlime" + (single ? "Single" : "Multi"));
            Message += "#L5#入場次數新增" + (single ? "（單曲）" : "（多個）") + "(" + (1-reset) + "可能）#l\r\n\r\n";
            Message += "#L4#不移動。 #l";
        }
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 4) return; //이동하지 않는다
        if (Menu == 5 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("合作積分不足。 擁有協同積分：" + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "GuardianSlime" + (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "GuardianSlime" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
        	self.sayOk("可進入次數新增。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        	return;
        }
//        if (DBConfig.isGanglim) {
//            self.sayOk("我是正在檢查的老闆。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
//            return;
//        }
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
                NormalGuardianSlimeEnter fieldSet = (NormalGuardianSlimeEnter) fieldSet("NormalGuardianSlimeEnter");
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
                    self.sayOk("有隊員在最近一周內打倒了衛士天使史萊姆。 衛士天使水晶泥加上正常模式和混沌模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                NormalGuardianSlimeEnter fieldSet = (NormalGuardianSlimeEnter) fieldSet("NormalGuardianSlimeEnter");
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

                HardGuardianSlimeEnter fieldSet = (HardGuardianSlimeEnter) fieldSet("HardGuardianSlimeEnter");
                int enter = fieldSet.enter(target.getId(), false, 5);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("有隊員在最近一周內打倒了衛士天使史萊姆。 衛士天使水晶泥加上正常模式和混沌模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                HardGuardianSlimeEnter fieldSet = (HardGuardianSlimeEnter) fieldSet("HardGuardianSlimeEnter");
                int enter = fieldSet.enter(target.getId(), true, 5);
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
        }
    }


    public void slimeOut() {
        initNPC(MapleLifeFactory.getNPC(9091025));
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
