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
        String Message = "알 수 없는 기운이 느껴진다. 가디언 엔젤 슬라임과의 전투를 위해 이동할까?\r\n\r\n";
        if (DBConfig.isGanglim) {
        	Message += "#L0#가디언 엔젤 슬라임(#b노말 모드#k) 입장을 신청한다. #r(레벨 210이상) #g[" + getPlayer().getOneInfoQuestInteger(1234570, "guardian_angel_slime_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L1#가디언 엔젤 슬라임(#b카오스 모드#k) 입장을 신청한다. #r(레벨 210이상) #g[" + getPlayer().getOneInfoQuestInteger(1234569, "guardian_angel_slime_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "#L2#가디언 엔젤 슬라임(#b노말 연습 모드#k) 입장을 신청한다. #r(레벨 210이상)#k#l\r\n";
            Message += "#L3#가디언 엔젤 슬라임(#b카오스 연습 모드#k) 입장을 신청한다. #r(레벨 210이상)#k#l\r\n\r\n";
            Message += "#L4#이동하지 않는다.#l";
        }
        else {
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	Message += "#L0##b가디언 엔젤 슬라임과의 전투(노멀 모드)" + (single ? "(싱글)" : "(멀티)") + "를 위해 이동한다.#r(레벨 210이상)#k#l\r\n";
            Message += "#L1##b가디언 엔젤 슬라임과의 전투(카오스 모드)" + (single ? "(싱글)" : "(멀티)") + "를 위해 이동한다.#r(레벨 210이상)#k#l\r\n";
            Message += "#L2##b가디언 엔젤 슬라임과의 전투(노멀 연습 모드)" + (single ? "(싱글)" : "(멀티)") + "를 위해 이동한다.#r(레벨 210이상)#k#l\r\n";
            Message += "#L3##b가디언 엔젤 슬라임과의 전투(카오스 연습 모드)" + (single ? "(싱글)" : "(멀티)") + "를 위해 이동한다.#r(레벨 210이상)#k#l\r\n";
            int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "GuardianSlime" + (single ? "Single" : "Multi"));
            Message += "#L5#입장횟수 증가 " + (single ? "(싱글)" : "(멀티)") + "(" + (1-reset) + "회 가능)#l\r\n\r\n";
            Message += "#L4#이동하지 않는다.#l";
        }
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 4) return; //이동하지 않는다
        if (Menu == 5 && !DBConfig.isGanglim) {
        	if (getPlayer().getTogetherPoint() < 150) {
        		self.sayOk("협동 포인트가 부족합니다. 보유 협동포인트 : " + getPlayer().getTogetherPoint(), ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	boolean single = getPlayer().getPartyMemberSize() == 1;
        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "GuardianSlime" + (single ? "Single" : "Multi"));
        	if (reset > 0) {
        		self.sayOk("이번 주에는 이미 입장가능 횟수를 증가시켰습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        		return;
        	}
        	getPlayer().gainTogetherPoint(-150);
        	getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "GuardianSlime" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
        	self.sayOk("입장가능 횟수가 증가되었습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
        	return;
        }
//        if (DBConfig.isGanglim) {
//            self.sayOk("현재 점검중인 보스입니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
//            return;
//        }
        if (target.getParty() == null) {
            self.sayOk("1인 이상 파티를 맺어야만 입장할 수 있습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.sayOk("파티장을 통해 진행해 주십시오.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (!target.getParty().isPartySameMap()) {
            self.sayOk("파티원이 모두 같은맵에 있어야 합니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        switch (Menu) { //따로 제한되는거 없으면 바로 입장가능함
            case 0: { //노멀모드
                NormalGuardianSlimeEnter fieldSet = (NormalGuardianSlimeEnter) fieldSet("NormalGuardianSlimeEnter");
                int enter = fieldSet.enter(target.getId(), false, 3);
                if (enter == 6) {
                    self.sayOk("이용 가능한 인스턴스가 없습니다. 다른 채널을 이용해주세요.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("보스 티어가 부족한 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("최근 일주일 이내 가디언 엔젤 슬라임을 쓰러뜨린 파티원이 있습니다. 가디언 엔젤 슬라임은 노멀 모드, 카오스 모드를 합쳐 일주일에 1회만 클리어 가능합니다\r\n#r<클리어 기록은 매주 목요일에 일괄 초기화됩니다.>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == -1 || enter == 4)) {
                    self.sayOk("입장 제한횟수가 부족하거나 레벨 제한이 맞지 않는 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -2) {
                    self.sayOk("입장 제한시간이 남은 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
            case 2: { //노멀 연습 모드
                int practiceMode = self.askYesNo("연습 모드에 입장을 선택하였습니다. 연습 모드에서는 #b#e경험치와 보상을 얻을 수 없으며#n#k 보스 몬스터의 종류와 상관없이 #b#e하루 20회#n#k만 이용할 수 있습니다. 입장하시겠습니까?");
                if (practiceMode == 0) {
                    return;
                }
                NormalGuardianSlimeEnter fieldSet = (NormalGuardianSlimeEnter) fieldSet("NormalGuardianSlimeEnter");
                int enter = fieldSet.enter(target.getId(), true, 3);
                if (enter == 6) {
                    self.sayOk("이용 가능한 인스턴스가 없습니다. 다른 채널을 이용해주세요.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("보스 티어가 부족한 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == 4) {
                    self.sayOk("레벨 제한이 맞지 않는 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -1) {
                    self.sayOk("연습 모드는 하루 20회만 가능합니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
            case 1: { //하드모드

                HardGuardianSlimeEnter fieldSet = (HardGuardianSlimeEnter) fieldSet("HardGuardianSlimeEnter");
                int enter = fieldSet.enter(target.getId(), false, 5);
                if (enter == 6) {
                    self.sayOk("이용 가능한 인스턴스가 없습니다. 다른 채널을 이용해주세요.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("보스 티어가 부족한 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("최근 일주일 이내 가디언 엔젤 슬라임을 쓰러뜨린 파티원이 있습니다. 가디언 엔젤 슬라임은 노멀 모드, 카오스 모드를 합쳐 일주일에 1회만 클리어 가능합니다\r\n#r<클리어 기록은 매주 목요일에 일괄 초기화됩니다.>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == -1 || enter == 4)) {
                    self.sayOk("입장 제한횟수가 부족하거나 레벨 제한이 맞지 않는 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -2) {
                    self.sayOk("입장 제한시간이 남은 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
            case 3: { //하드 연습 모드
                int practiceMode = self.askYesNo("연습 모드에 입장을 선택하였습니다. 연습 모드에서는 #b#e경험치와 보상을 얻을 수 없으며#n#k 보스 몬스터의 종류와 상관없이 #b#e하루 20회#n#k만 이용할 수 있습니다. 입장하시겠습니까?");
                if (practiceMode == 0) {
                    return;
                }
                HardGuardianSlimeEnter fieldSet = (HardGuardianSlimeEnter) fieldSet("HardGuardianSlimeEnter");
                int enter = fieldSet.enter(target.getId(), true, 5);
                if (enter == 6) {
                    self.sayOk("이용 가능한 인스턴스가 없습니다. 다른 채널을 이용해주세요.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("보스 티어가 부족한 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == 4)) {
                    self.sayOk("레벨 제한이 맞지 않는 파티원이 있어 입장할 수 없습니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -1) {
                    self.sayOk("연습 모드는 하루 20회만 가능합니다.", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
            }
            break;
        }
    }


    public void slimeOut() {
        initNPC(MapleLifeFactory.getNPC(9091025));
        if (self.askYesNo("전투를 중지하고 나가시겠습니까?") == 1) {
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
