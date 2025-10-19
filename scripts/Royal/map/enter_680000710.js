importPackage(java.lang);
importPackage(java.text);
importPackage(Packages.scripting);
importPackage(Packages.network.models);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 1) {
        status++;
    } else {
        if (mode != 3) {
            cm.dispose();
        }
        return;
    }
    if (status == 0) {
	var job = cm.getPlayer().getJob();
	
	if (cm.getPlayer().getOneInfoQuestInteger(1234567, "see_intro") == 1) {
            		cm.getPlayer().send(CField.DirectionPacket.IntroEnableUI(1));
		cm.effectText("항상 즐거움이 가득찬 진 캐슬", 100, 1000, 4, 0, 0, -200);
		cm.dispose();
		if (cm.getPlayer().getOneInfoQuestInteger(121212, "job_select") == 0) {
			cm.openNpc(1540208);
		}
		return;
	}
	cm.setIngameDirectionMode(false, false, false);
	cm.setBlind(1, 255, 0, 0, 0, 0, 0);
	cm.setStandAloneMode(true);
	cm.spawnLocalNpc(9062474, -1000, 30, 0, 53, true);
	cm.spawnLocalNpc(9062475, -1080, 30, 1, 53, true);
	cm.setBlind(1, 255, 0, 0, 0, 250, 2);
	cm.delay(250);
    } else if (status == 1) {
	cm.overlapDetail(0, 1000, 3000, 1);
	cm.cameraZoom(0, 1000, 2147483647, 2147483647, 2147483647);
	cm.cameraMoveBack(0, 0);
	cm.delay(300);
    } else if (status == 2) {
	cm.removeOverlapDetail(1000);
	cm.delay(100);
    } else if (status == 3) {
	cm.cameraZoom(0, 2000, 0, -1500, 100);
	cm.setBlind(1, 255, 0, 0, 0, 0, 2);
	cm.delay(100);
    } else if (status == 4) {
	cm.setBlind(0, 0, 0, 0, 0, 250, 0);
	cm.delay(2500);
    } else if (status == 5) {
	cm.effectText("#fn나눔고딕 ExtraBold##fs18#<진 캐슬>", 100, 1000, 6, 0, -50, -50);
	cm.delay(2000);
    } else if (status == 6) {
	cm.sayNpc("우와...", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 7) {
	cm.sayNpc("가까이서 보니 정말 #r#e거대한 성#n#k이잖아...?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 8) {
	cm.delay(1000);
    } else if (status == 9) {
	cm.forcedMove(2, 400);
	cm.cameraZoom(4000, 2000, 4000, -1100, 100);
	cm.delay(4000);
    } else if (status == 10) {
	cm.effectPlay("Effect/OnUserEff.img/emotion/whatl", 0, 0, 0, 0, 0, false);
	cm.delay(2000);
    } else if (status == 11) {
	cm.effectPlay("Effect/OnUserEff.img/emotion/oh", 0, 0, 0, 0, 9062474, false);
	cm.delay(500);
    } else if (status == 12) {
	cm.npcFlipForcely(9062475, -1);
	cm.delay(500);
    } else if (status == 13) {
	cm.effectPlay("Effect/OnUserEff.img/emotion/oh", 0, 0, 0, 0, 9062475, false);
	cm.delay(2000);
    } else if (status == 14) {
	cm.sayReplacedNpc("와! #e#b#h0##k!!!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 15) {
	cm.sayReplacedNpc("헤헤! 기다렸어요!\r\n#fs30##r반가워요! 반갑죠? 환영해요!", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 16) {
	cm.sayReplacedNpc("#b제가 보낸 순록#k은 잘 타고 왔어요?", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 17) {
	cm.npcMoveForcely(9062474, -1, 10, 100);
	cm.delay(1000);
    } else if (status == 18) {
	cm.sayReplacedNpc("진정해, 리오...모처럼 찾아온 사람을 놀라게 하면 못써.", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 19) {
	cm.sayReplacedNpc("앗... 미안해요!\r\n저는 #b리오#k라고 해요!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 20) {
	cm.sayReplacedNpc("후후. #e#b#h0##k. \r\n드디어 왔구나. 나는 #b르네#k라고 해.", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 21) {
	cm.sayReplacedNpc("이곳은 #r<진 캐슬>#k이야.\r\n차원이 겹쳐진 곳에 비치는 덧없는 #b#e환상#n#k이지", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 22) {	
	cm.sayNpc("너희가 진 캐슬의 정령이구나.\r\n그런데... #b환상#k이라니?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 23) {
	cm.sayReplacedNpc("여긴 #b서로 다른 두 차원#k이 합쳐지면서 생겨난 공간이에요!\r\n차원이 안정을 찾으면 진 캐슬은 다시 사라질 거예요!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 24) {
	cm.sayReplacedNpc("후후. 그래서 다들 초대한 거야.\r\n이미 많은 사람들이 초대를 받고 진 캐슬에 도착했어!", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 25) {
	cm.sayReplacedNpc("그럼 먼저 온 사람들을 소개해 줄게.", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 26) {
	cm.setBlind(1, 255, 0, 0, 0, 250, 2);
	cm.delay(250);
    } else if (status == 27) {
	cm.overlapDetail(0, 1000, 3000, 1);
	cm.cameraZoom(0, 1000, 2147483647, 2147483647, 2147483647);
	cm.cameraMoveBack(0, 0);
	cm.delay(300);
    } else if (status == 28) {
	cm.removeOverlapDetail(1000);
	cm.removeLocalNpc(9062474);
	cm.removeLocalNpc(9062475);
	cm.cameraZoom(0, 2000, 0, -700, -100);
	cm.delay(1000);
    } else if (status == 29) {
	cm.setBlind(1, 255, 0, 0, 0, 0, 2);
	cm.delay(100);
    } else if (status == 30) {
	cm.setBlind(0, 0, 0, 0, 0, 250, 0);
	cm.delay(300);
    } else if (status == 31) {	
	cm.sayReplacedNpc("혼자서도 잘 노는 사람! 여기 붙어라~", false, true, 1, 9062461, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 32) {	
            cm.sayReplacedNpc("나보다 게임을 잘하는 사람은 없을걸?!", false, true, 1, 9062462, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 33) {
	cm.sayReplacedNpc("여기는 모두들 #b모여서 게임#k을 하고 있어요!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 34) {
	cm.sayReplacedNpc("게임이라니... 유치한 어린이들 같으니. \r\n나는 #r성공한 상인#k이 될 거야!", false, true, 1, 9062463, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 35) {
	cm.sayReplacedNpc("#b게임#k을 즐기면 #i4310307:##b#t4310307##k을 얻을 수 있어요!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 36) {
	cm.sayReplacedNpc("#i4310307:##b#t4310307##k을 사용해서 저 상인 꿈나무 친구에게 #r멋진 물건#k을 구할 수 있을 거야.", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 37) {
	cm.cameraZoom(1500, 2000, 1500, -450, -100);
	cm.delay(1500);
    } else if (status == 38) {
	cm.sayReplacedNpc("흠... 정말 강한 힘이 느껴지는군. 만족스러워.", false, true, 1, 9062459, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 39) {
	cm.sayReplacedNpc("저 사람은 아주 강한 힘을 가진 보석, \r\n#i4310308:##r#t4310308##k를 구하고 있어.", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 40) {
	cm.sayReplacedNpc("#b강한 보스#k를 처치하면 #i4310308:##r#t4310308##k를 만들 수 있대요!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 41) {
	cm.cameraZoom(3000, 2000, 3000, 200, -100);
	cm.delay(3000);
    } else if (status == 42) {	
	cm.sayReplacedNpc("오호... 당신 꽤나 좋은 물건을 가지고 있군?", false, true, 1, 9062457, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 43) {
	cm.sayReplacedNpc("후후. 메이플 월드에는 진귀한 물건이 많답니다.\r\n그란디스에도 신비한 물건이 참 많군요.", false, true, 1, 9062455, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 44) {
	cm.sayReplacedNpc("여기에는 #r다양한 상인분들#k이 있답니다!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 45) {
	cm.sayReplacedNpc("저 상인들은 진 캐슬이 생겨난 힘의 원천, \r\n#i4310306:##b#t4310306##k를 주면 #r진귀한 물건#k을 줄 거야.", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 46) {
	cm.cameraZoom(1500, 2000, 1500, 500, -100);
	cm.delay(1500);
    } else if (status == 47) {
	cm.spawnLocalNpc(9062453, -295, -320, 1, 48, false);
	cm.spawnLocalNpc(9062454, -8, -320, 0, 63, false);
	cm.sayReplacedNpc("꾸준한 훈련만이 평화를 지킬 힘을 기를 수 있습니다.", false, true, 1, 9062451, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 48) {	
	cm.sayReplacedNpc("초대받은 곳에서도 열심히 훈련하는 사람도 있지.", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 49) {
	cm.cameraZoom(2000, 2000, 2000, -200, -300);
	cm.delay(2000);
    } else if (status == 50) {
	cm.sayNpc("정말 #b메이플 월드#k와 #r그란디스#k의 다양한 사람들이 모여 있구나.", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 51) {	
	cm.sayReplacedNpc("그럼요! 너무 늦게 오셨어요!!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 52) {
	cm.cameraZoom(4000, 800, 4000, -200, -360);
	cm.delay(4000);
    } else if (status == 53) {
	cm.sayReplacedNpc("후후. #r진 캐슬#k이 부디 너에게도 좋은 추억으로 남길 바랄게.", false, true, 1, 9062474, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 54) {
	cm.sayReplacedNpc("저희와 함께 #r진 캐슬#k에서 좋은 시간 보내요!", false, true, 1, 9062475, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 55) {
	cm.setBlind(1, 255, 0, 0, 0, 250, 2);
	cm.delay(250);
    } else if (status == 56) {
	cm.overlapDetail(0, 1000, 3000, 1);
	cm.cameraZoom(0, 1000, 2147483647, 2147483647, 2147483647);
	cm.cameraMoveBack(0, 0);
	cm.delay(300);
    } else if (status == 57) {
	cm.removeOverlapDetail(1000);
	cm.cameraMoveBack(0, 0);
	cm.delay(300);
    } else if (status == 58) {
	cm.setStandAloneMode(false);
	cm.endIngameDirectionMode(1);
	cm.removeLocalNpc(9062453);
	cm.removeLocalNpc(9062454);
	cm.getPlayer().updateOneInfo(1234567, "see_intro", "1");
	cm.getPlayer().stackEventGaugeFirstSet();
	cm.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 1000);
	cm.getPlayer().setRegisterTransferField(680000710);
	cm.dispose();
    }
}