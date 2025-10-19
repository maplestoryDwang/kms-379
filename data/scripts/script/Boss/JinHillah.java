package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.childs.*;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

public class JinHillah extends ScriptEngineNPC {

    public void JinHillah_enter() {
        Maze3_dungeon();
    }

    // Maze3_dungeon
    public void Maze3_dungeon() {
        initNPC(MapleLifeFactory.getNPC(1402400));
        String Message = "為了封锁真希拉，要不要前往#b欲望祭壇#k？ \r\n\r\n";
        if (DBConfig.isGanglim) {
            Message += "前往#L0#欲望祭壇（#b困難模式#k）。 #r（等級250以上）#g[" + getPlayer().getOneInfoQuestInteger(1234569, "jinhillah_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "前往#L1#欲望祭壇（#b困難練習模式#k）。 #r（250級以上）#k#l\r\n\r\n";
            //Message += "前往#L5#欲望祭壇（#r地獄模式#k）。 #r（250級以上）#k#l\r\n";
            Message += "#L2#不移動。 #l";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
            Message += "#L3##b欲望祭壇（諾瑪模式）"+ (single ? "（單曲）" : "（多個）") +"移動到。 #r（250級以上）#k#l\r\n";
            Message += "#L0##b欲望祭壇（困難模式）"+ (single ? "（單曲）" : "（多個）") +"移動到。 #r（250級以上）#k#l\r\n";
            //Message += "前往#L5##b欲望祭壇（#r健康模式#b）。 #r（250級以上）#k#l\r\n";
            Message += "#L4##b欲望祭壇（無言練習模式）"+ (single ? "（單曲）" : "（多個）") +"移動到。 #r（250級以上）#k#l\r\n";
            Message += "#L1##b欲望祭壇（困難練習模式）"+ (single ? "（單曲）" : "（多個）") +"移動到。 #r（250級以上）#k#l\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "JinHillah" + (single ? "Single" : "Multi"));
            Message += "#L8#可進入次數新增" + (single ? "（單曲）" : "（多個）") + "(" + (1-reset) + "可能）#l\r\n\r\n\r\n";
            Message += "#L2#不移動。 #l";
        }
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 8 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("合作積分不足。 擁有協同積分：" + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "JinHillah"+ (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("本周已新增可進入次數。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "JinHillah" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
        	self.sayOk("可進入次數新增。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        	return;
        }
        if (Menu == 2) return; //이동하지 않는다
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
            case 0: { //하드모드
                boolean GenesisQuest = false;
                if (getPlayer().getQuestStatus(2000026) == 1) {
                    int Genesis = target.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#b-全體正在進行“紅魔女真希拉痕迹”，在未完成真希拉擊破條件的2人以下隊伍中擊破\r\n-2人隊伍時，最終傷害减少50%，如果1人失敗，所有隊員一起失敗，在死亡的隊員死亡的情况下，將無法减少到威爾的HP1。 #l\r\n#L1#不執行任務。 #l");
                    if (Genesis == 0) { //미션을 수행한다.
                        GenesisQuest = true;
                    } else if (Genesis == 1) { //미션을 수행하지 않는다.
                        GenesisQuest = false;
                    }
                }
                HardJinHillahEnter fieldSet = (HardJinHillahEnter) fieldSet("HardJinHillahEnter");
                int enter = fieldSet.enter(target.getId(), false, GenesisQuest, 6);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.NpcReplacedByNpc);
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
                    self.sayOk("最近一周內有一名隊員打倒了金希拉。 真希拉每週只能清空一次\r\n#r<清空記錄每週四統一初始化。>", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == -1 || enter == 4)) {
                    self.sayOk("入場限制次數不足或有等級限制不符的隊員無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -2) {
                    self.sayOk("因為還有限制入場時間的隊員，所以無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
            case 1: { //하드 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                HardJinHillahEnter fieldSet = (HardJinHillahEnter) fieldSet("HardJinHillahEnter");
                int enter = fieldSet.enter(target.getId(), true, false, 6);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == 4)) {
                    self.sayOk("有等級限制不符的隊員，無法進入。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -1) {
                    self.sayOk("練習模式每天只能進行20次。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }

            case 3: { //노말모드
                NormalJinHillahEnter fieldSet = (NormalJinHillahEnter) fieldSet("NormalJinHillahEnter");
                int enter = fieldSet.enter(target.getId(), false, 6);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("最近一周內有一名隊員打倒了金希拉。 真希拉每週只能清空一次\r\n#r<清空記錄每週四統一初始化。>", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == -1 || enter == 4)) {
                    self.sayOk("入場限制次數不足或有等級限制不符的隊員無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -2) {
                    self.sayOk("因為還有限制入場時間的隊員，所以無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
            case 4: { //노말 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                NormalJinHillahEnter fieldSet = (NormalJinHillahEnter) fieldSet("NormalJinHillahEnter");
                int enter = fieldSet.enter(target.getId(), true, 6);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == 4)) {
                    self.sayOk("有等級限制不符的隊員，無法進入。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -1) {
                    self.sayOk("練習模式每天只能進行20次。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
            }
            break;
            case 5: { //헬모드
                HellJinHillahEnter fieldSet = (HellJinHillahEnter) fieldSet("HellJinHillahEnter");
                int enter = fieldSet.enter(target.getId(), 6);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("最近一周內有一名隊員打倒了金希拉。 真希拉每週只能清空一次\r\n#r<清空記錄每週四統一初始化。>", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == -1 || enter == 4)) {
                    self.sayOk("入場限制次數不足或有等級限制不符的隊員無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -2) {
                    self.sayOk("因為還有限制入場時間的隊員，所以無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
        }
    }

    public void jinHillah_out() {
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
