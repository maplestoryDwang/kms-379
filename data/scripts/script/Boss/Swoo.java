package script.Boss;

import constants.GameConstants;
import constants.QuestExConstants;
import constants.ServerConstants;
import database.DBConfig;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.childs.HardBlackHeavenBossEnter;
import objects.fields.fieldset.childs.HellBlackHeavenBossEnter;
import objects.fields.fieldset.childs.NormalBlackHeavenBossEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class Swoo extends ScriptEngineNPC {


    public void blackHeaven_boss() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        String v0 = "為了打倒史宇，要不要移動到黑色天堂覈心？ \r\n\r\n";
        if (DBConfig.isGanglim) {
            v0 += "前往#L0#黑色天堂覈心（#b正常模式#k）。 #r（等級230以上）#k#g[" + getPlayer().getOneInfoQuestInteger(1234570, "swoo_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            v0 += "前往#L1#黑色天堂覈心（#b硬模式#k）。 #r（等級230以上）#k#g[" + getPlayer().getOneInfoQuestInteger(1234569, "swoo_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            v0 += "前往#L2#黑色天堂覈心（#b正常練習模式#k）。 #r（230級以上）#k#l\r\n";
            v0 += "前往#L3#黑色天堂覈心（#b硬練習模式#k）。 #r（230級以上）#k#l\r\n";
            v0 += "前往#L4#黑色天堂覈心（#r健康模式#k）。 #r（等級270以上）#k#l\r\n";
            v0 += "#L5#不移動。 #l\r\n\r\n";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	v0 += "#L0#黑色天堂覈心（#b正常模式#k）" + (single ? "（單曲）" : "（多個）") + "移動到。 （230級以上）#l\r\n";
            v0 += "#L1#黑色天堂覈心（#b硬模式#k）" + (single ? "（單曲）" : "（多個）") + "移動到。 （230級以上）#l\r\n";
            v0 += "#L2#黑色天堂覈心（#b正常練習模式#k）" + (single ? "（單曲）" : "（多個）") + "移動到。 （230級以上）#l\r\n";
            v0 += "#L3#黑色天堂覈心（#b硬練習模式#k）" + (single ? "（單曲）" : "（多個）") + "移動到。 （230級以上）#l\r\n";
            v0 += "前往#L4#黑色天堂覈心（#r健康模式#k）。 （270級以上）#l\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Swoo" + (single ? "Single" : "Multi"));
            v0 += "#L6#可進入次數新增" + (single ? "（單曲）" : "（多個）") + "(" + (1-reset) + "）可能次數#l\r\n\r\n";
            v0 += "#L5#不移動。 #l\r\n\r\n";
        }
        int Menu = target.askMenu(v0, ScriptMessageFlag.BigScenario);
        if (Menu == 5) return; //이동하지 않는다
        if (Menu == 6 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("合作積分不足。 擁有協同積分：" + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Swoo" + (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Swoo" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
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
                NormalBlackHeavenBossEnter fieldSet = (NormalBlackHeavenBossEnter) fieldSet("NormalBlackHeavenBossEnter");
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
                    self.sayOk("最近一周內有個隊員打倒了史宇。 將正常模式、硬模式、健康模式加在一起，每週只可清除一次。\r\n#r<清除記錄將於每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                NormalBlackHeavenBossEnter fieldSet = (NormalBlackHeavenBossEnter) fieldSet("NormalBlackHeavenBossEnter");
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
                if (getPlayer().getQuestStatus(2000022) == 1) {
                    int Genesis = target.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-最終傷害减少20%\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l");
                    if (Genesis == 0) { //미션을 수행한다.
                        GenesisQuest = true;
                    } else if (Genesis == 1) { //미션을 수행하지 않는다.
                        GenesisQuest = false;
                    }
                }
                HardBlackHeavenBossEnter fieldSet = (HardBlackHeavenBossEnter) fieldSet("HardBlackHeavenBossEnter");
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
                    self.sayOk("最近一周內有個隊員打倒了史宇。 將正常模式、硬模式、健康模式加在一起，每週只可清除一次。\r\n#r<清除記錄將於每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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
                HardBlackHeavenBossEnter fieldSet = (HardBlackHeavenBossEnter) fieldSet("HardBlackHeavenBossEnter");
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
            case 4: { //헬모드
                String hellMenu = ""; 
                if (DBConfig.isGanglim){
                    hellMenu = "#fs11##e<飛行模式>#n\r\n飛行模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少99%，適用角色强化數值50%\r\n-體力新增\r\n-强化模式\r\n-死亡3次（組隊共亯）\r\n#r擊破時，有55%的概率獲得#b#i4038#z4038#1個庫存。 \r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                } else {
                    hellMenu = "#e<Lucid Hell模式>#n\r\n Hell模式按以下條件進行： \r\n\r\n#e#r<入場條件>#n#k\r\n#b-最多擊破3人\r\n-最終傷害减少95%\r\n-體力新增\r\n-體力恢復效果適用50%\r\n-强化模式\r\n-死亡5次（組隊共亯）\r\n#k#L0#入場。 #l\r\n#L1#不入場。 #l";
                }
                int WelcomeToTheHell = target.askMenu(hellMenu);
                if (WelcomeToTheHell != 0) {
                    return;
                }
                HellBlackHeavenBossEnter fieldSet = (HellBlackHeavenBossEnter) fieldSet("HellBlackHeavenBossEnter");
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
                        self.sayOk("最近一周內有打倒赫爾斯烏的隊員。 \r\n健身房每週只能通關一次。 \r\n#r<清除歷史記錄將於每週一進行批量初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.sayOk("最近一周內有個隊員打倒了史宇。 將正常模式、硬模式、健康模式加在一起，每週只可清除一次。\r\n#r<清除記錄將於每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
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

    public void bh_bossOutN() {
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

    public void bh_bossOut() {
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
