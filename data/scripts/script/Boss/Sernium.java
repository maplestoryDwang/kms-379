package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.childs.HardSerenEnter;
import objects.fields.fieldset.childs.NormalSerenEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.users.MapleCharacter;

public class Sernium extends ScriptEngineNPC {

    public void serenOut() {
        initNPC(MapleLifeFactory.getNPC(3004430));
        if (self.askYesNo("戰鬥結束後退場嗎？") == 1) {
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
            }
            registerTransferField(410000670, 4);
        }
    }


    public void seren_enterGate() {
        initNPC(MapleLifeFactory.getNPC(2007));

        String Message = "為了與被選中的瑟琳戰鬥而移動嗎？ \r\n\r\n";
        if (DBConfig.isGanglim) {
            Message += "#L1#申請進入被選中的賽倫（#b困難模式#k）。 #r（等級265以上）#g[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.SerniumSeren.getQuestID(), "clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L3#申請進入被選中的賽倫（#b硬練習模式#k）。 #r（級別265或更高）#k#l\r\n\r\n";
            Message += "#L4#不移動。 #l";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
            Message += "#L0##b選擇的Seren（無語言模式）" + (single ? "（單曲）" : "（多個）") + "為了戰鬥而移動。 #r（級別265或更高）#k#l\r\n";
            Message += "#L1##b已選擇的Seren（硬模式）" + (single ? "（單曲）" : "（多個）") + "為了戰鬥而移動。 #r（級別265或更高）#k#l\r\n";
            Message += "#L2##b被選中的Seren（無語言練習模式）" + (single ? "（單曲）" : "（多個）") + "為了戰鬥而移動。 #r（級別265或更高）#k#l\r\n";
            Message += "#L3##b選擇的Seren（硬練習模式）" + (single ? "（單曲）" : "（多個）") + "為了戰鬥而移動。 #r（級別265或更高）#k#l\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Seren" + (single ? "Single" : "Multi"));
            Message += "#L8#可進入次數新增" + (single ? "（單曲）" : "（多個）") + "(" + (1-reset) + "可能）#l\r\n\r\n\r\n";
            Message += "#L4#不移動。 #l";
        }
        
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 4) return; //이동하지 않는다
        if (Menu == 8 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("合作積分不足。 擁有協同積分：" + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Seren" + (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Seren" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
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
                NormalSerenEnter fieldSet = (NormalSerenEnter) fieldSet("NormalSerenEnter");
                int enter = fieldSet.enter(target.getId(), false, 7);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("最近一周內有一個隊員打倒了瑟琳。 瑟琳加上正常模式和混沌模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                NormalSerenEnter fieldSet = (NormalSerenEnter) fieldSet("NormalSerenEnter");
                int enter = fieldSet.enter(target.getId(), true, 7);
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
                HardSerenEnter fieldSet = (HardSerenEnter) fieldSet("HardSerenEnter");
                int enter = fieldSet.enter(target.getId(), false, 7);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("最近一周內有一個隊員打倒了瑟琳。 瑟琳加上正常模式和混沌模式，每週只能通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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

                Party party = getPlayer().getParty();
                for (PartyMemberEntry mpc : party.getPartyMemberList()) {
                    if (mpc != null) {
                        MapleCharacter player = getPlayer().getMap().getCharacterById(mpc.getId());
                        if (player != null) {
                            if (player.getJob() == 2217 || player.getJob() == 2218 || player.getJob() == 3712 || player.getJob() == 434) {
                                player.setDebugPacket(40);
                            }
                        }
                     }
                 }

                break;
            }
            case 3: { //하드 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                HardSerenEnter fieldSet = (HardSerenEnter) fieldSet("HardSerenEnter");
                int enter = fieldSet.enter(target.getId(), true, 7);
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
}
