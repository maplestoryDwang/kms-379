importPackage(Packages.scripting);

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 2) {
            qm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sayNpc("핥핥! 이봐", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 1) {
        qm.sayNpc("?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
    } else if (status == 2) {
        qm.askAccept("#face0#할 말이 있어.\r\n아주 중요한 일이야. 핥핥!\r\n#r(수락 시 바로 이동됩니다.)", GameObjectType.Npc, 1, ScriptMessageFlag.Scenario);
    } else if (status == 3) {
        qm.forceStartQuest();
		qm.warp(450002000);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        qm.sayNpc("#face3#핥핥! 어서 오게!", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 1) {
        qm.sayNpc("거짓말쟁이에게 무슨 일인가요?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
    } else if (status == 2) {
        qm.sayNpc("핥! 아직도 마음에 담아두고 있는 건가! 내가 사과하지!\r\n오해가 있었던 것 같군! 핥핥!", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 3) {
        qm.sayNpc("무토와의 일도 잘 해결되고 시미아도 정식 주방장으로 들어오고 나선 주방에 여유가 생기는 줄 알았어... 그런데...", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 4) {
        qm.sayNpc("이제는 주변의 몬스터들까지 냄새를 맡고 주방에 침입을 하기 시작했다! 핥!", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 5) {
        qm.sayNpc("주방장님과 시미아는 괜찮나요?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
    } else if (status == 6) {
        qm.sayNpc("#face0#핥! 요리사가 없으면 요리가 없다는 건 아는지 요리만 쏙 훔쳐 가고 있는 게 더 화가 난다고!! 핥!!", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 7) {
        qm.sayNpc("#face3#그래서 주변 몬스터들을 좀 혼쭐 내주면 좋겠군! 핥?", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 8) {
        qm.sayNpc("으음...", GameObjectType.User, false, true, 1, ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
    } else if (status == 9) {
        qm.sayNpc("주방 보조 업무라고 생각해라! 핥! 무토와 마을 주민들을 위한 일이니\r\n#b일당#k도 섭섭치 않게 주지! 핥!", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 10) {
        qm.sayNpc("무토와 이곳을 위한 일이라면 도울게요.", GameObjectType.User, false, true, 1, ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
    } else if (status == 11) {
        qm.sayNpc("#face3#핥핥핥! 좋아.\r\n보조 업무는 매일 #b세 가지#k를 줄거야.\r\n특별히 오늘의 업무 중 마음에 안드는 업무는 교체할 수 있는 기회도 주지! 핥핥핥!", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 12) {
        qm.sayNpc("당연히 보조 업무에 대한 보상도 있으니 언제든 준비가 되면 나에게 말하라고 핥핥핥!", GameObjectType.Npc, false, true, 1, ScriptMessageFlag.Scenario);
    } else if (status == 13) {
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
