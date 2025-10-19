importPackage(java.lang);
importPackage(java.text);
importPackage(Packages.scripting);

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
	cm.setIngameDirectionMode(false, false, false);
	cm.setBlind(1, 255, 0, 0, 0, 0, 0);
	cm.setStandAloneMode(true);
	cm.delay(500);
    } else if (status == 1) {
	cm.setBlind(1, 0, 0, 0, 0, 0, 255); //off
	cm.delay(1400);
    } else if (status == 2) {
	cm.effectText("#fn나눔고딕 ExtraBold##fs18#<진 캐슬> 입구", 100, 1000, 6, 0, -50, -50);
	cm.delay(2000);
    } else if (status == 3) {
	cm.sayNpc("저곳이 진 캐슬이구나.", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 4) {
	cm.forcedMove(2, 300);
	cm.delay(2000);
    } else if (status == 5) {
	cm.sayNpc("듣던 대로 정말 아름답다.\r\n어서 가보자.", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 6) {
	cm.delay(1000);
    } else if (status == 7) {
	cm.cameraZoom(2000, 1000, 2000, -520, -130);
	cm.forcedMove(2, 300);
 	cm.setBlind(1, 255, 0, 0, 0, 500, 2);
	cm.delay(500);
    } else if (status == 8) {
	cm.overlapDetail(0, 1000, 3000, 1);
	cm.cameraZoom(0, 1000, 2147483647, 2147483647, 2147483647);
	cm.cameraMoveBack(0, 0);
	cm.delay(300);
    } else if (status == 9) {
	cm.removeOverlapDetail(1000);
	cm.cameraMoveBack(0, 0);
	cm.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 1000);
	cm.getPlayer().setRegisterTransferField(680000710);
	cm.dispose();
    }
}