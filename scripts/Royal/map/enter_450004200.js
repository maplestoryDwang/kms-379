importPackage(java.lang);
importPackage(java.text);

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
	cm.setBlind(1, 255, 240, 240, 240, 0, 0);
	cm.effectPlay("Map/Effect3.img/BossLucid/Lucid5", 0, 89, 36, 1, 0, 1);
	cm.sendScenarioNpcNoESC("#face6#어머, 이를 어째? 꿈이 무너지나봐요~!", 1, 3000);
    } else if (status == 1) {
	cm.setBlind(0, 0, 0, 0, 0, 500, 0); //off
	cm.effectSound("Sound/SoundEff.img/ArcaneRiver/phase2");
	cm.effectPlay("Map/Effect3.img/BossLucid/Lucid2", 0, 89, 36, 10, 0, 1);
	cm.effectPlay("Map/Effect3.img/BossLucid/Lucid3", 0, -140, 100, 11, 0, 1);
	cm.effectPlay("Map/Effect3.img/BossLucid/Lucid4", 0, 89, 36, 1, 0, 1);
	cm.delay(4000);
    } else if (status == 2) {
	cm.setBlind(1, 255, 240, 240, 240, 1300, 0);
	cm.delay(1300);
    } else if (status == 3) {
	cm.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
	cm.getPlayer().setRegisterTransferField(cm.getPlayer().getMapId() + 50);
	cm.getPlayer().setTransferFieldOverlap(true);
	cm.dispose();
    }
}