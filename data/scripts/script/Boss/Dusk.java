package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.childs.ChaosDuskEnter;
import objects.fields.fieldset.childs.HardGuardianSlimeEnter;
import objects.fields.fieldset.childs.NormalDuskEnter;
import objects.fields.fieldset.childs.NormalGuardianSlimeEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.MapleCharacter;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

public class Dusk extends ScriptEngineNPC {

    public void dusk_enter() {
        initNPC(MapleLifeFactory.getNPC(2007));
        String Message = "不能放任由黑魔法師的邪念組成的巨大怪獸德斯克不管。 \r\n\r\n";
        if (DBConfig.isGanglim) {
        	Message += "#L0#移動到空虛的眼睛（#b正常模式#k）。 #r（等級245以上）#g[" + getPlayer().getOneInfoQuestInteger(1234590, "dusk_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L1#移動到空虛的眼睛（#b混沌模式#k）。 #r（等級245以上）#g[" + getPlayer().getOneInfoQuestInteger(1234589, "dusk_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L2#移動到空虛的眼睛（#b正常練習模式#k）。 #r（級別245以上）#k#l\r\n";
            Message += "#L3#移動到空虛的眼睛（#b混沌練習模式#k）。 #r（級別245以上）#k#l\r\n";
            //Message += "#L4#移動到空虛的眼睛（#r健康模式#k）。 #r（270級以上）#k#l\r\n\r\n\r\n";
            Message += "#L5#不移動。 #l\r\n\r\n";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	Message += "#L0##b空虛之眼（正常模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（級別245以上）#k#l\r\n";
            Message += "#L1##b空虛的眼睛（混沌模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（級別245以上）#k#l\r\n";
            Message += "#L2##b空虛之眼（正常練習模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（級別245以上）#k#l\r\n";
            Message += "#L3##b空虛的眼睛（混沌練習模式）" + (single ? "（單曲）" : "（多個）") + "移動到。 #r（級別245以上）#k#l\r\n";
            //Message += "#L4##b移動到空虛的眼睛（#r健康模式#b）。 #r（270級以上）#k#l\r\n\r\n\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Dusk" + (single ? "Single" : "Multi"));
            Message += "#L8#可進入次數新增" + (single ? "（單曲）" : "（多個）") + "(" + (1-reset) + "可能）#l\r\n\r\n\r\n";
            Message += "#L5#不移動。 #l\r\n\r\n";
        }
        
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 8 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("合作積分不足。 擁有協同積分：" + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Dusk" + (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Dusk" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
        	self.sayOk("可進入次數新增。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        	return;
        }
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
                NormalDuskEnter fieldSet = (NormalDuskEnter) fieldSet("NormalDuskEnter");
                int enter = fieldSet.enter(target.getId(), false, 4);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("有一個隊員在最近一周內打倒了達斯克。 達斯克加上正常模式和混沌模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                NormalDuskEnter fieldSet = (NormalDuskEnter) fieldSet("NormalDuskEnter");
                int enter = fieldSet.enter(target.getId(), true, 4);
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
                ChaosDuskEnter fieldSet = (ChaosDuskEnter) fieldSet("ChaosDuskEnter");
                int enter = fieldSet.enter(target.getId(), false, 6);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("有一個隊員在最近一周內打倒了達斯克。 達斯克加上正常模式和混沌模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                ChaosDuskEnter fieldSet = (ChaosDuskEnter) fieldSet("ChaosDuskEnter");
                int enter = fieldSet.enter(target.getId(), true, 6);
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
            case 4: { //헬 모드

                break;
            }
        }
    }

    public void BM1_bossOut() {
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
