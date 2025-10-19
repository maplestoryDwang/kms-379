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
        cm.dispose();
        return;
    }
    if (status == 0) {
	cm.setIngameDirectionMode(false, false, false);
	cm.setBlind(1, 255, 0, 0, 0, 0, 0);
	cm.setStandAloneMode(true);
	cm.delay(1200);
    } else if (status == 1) {
	cm.setBlind(0, 0, 0, 0, 0, 1000, 0); //off
	cm.delay(1400);
    } else if (status == 2) {
	cm.delay(2000);
    } else if (status == 3) {
	cm.sayNpc("거의 다 온 것 같다...", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 4) {
	cm.sayNpc("정령들이 보내준 순록을 타고 오니 금방 도착하는군.", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 5) {
	cm.delay(2000);
    } else if (status == 6) {
	cm.setBlind(0, 0, 0, 0, 0, 2000, 0); //off
	cm.delay(2100);
	cm.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
	cm.getPlayer().setRegisterTransferField(993189102);
	cm.dispose();
    }
}