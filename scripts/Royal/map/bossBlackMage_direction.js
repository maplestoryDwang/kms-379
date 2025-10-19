importPackage(java.lang);
importPackage(java.text);

status = -1;

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
	if (cm.getPlayer().getMapId() == 450013000) {
		cm.setIngameDirectionMode(false, false, false);
		cm.setBlind(1, 255, 0, 0, 0, 0, 0);
		cm.environmentChange(7, "Bgm00.img/Silence", 0);
	} else if (cm.getPlayer().getMapId() == 450013200) {
		cm.setIngameDirectionMode(false, false, false);
		cm.setBlind(1, 255, 0, 0, 0, 0, 0);
		cm.environmentChange(7, "Bgm50/ThroneOfDarkness", 0);
	} else if (cm.getPlayer().getMapId() == 450013400) {
		cm.setIngameDirectionMode(false, false, false);
		cm.setBlind(1, 255, 0, 0, 0, 0, 0);
		cm.environmentChange(7, "Bgm50/WorldHorizon", 0);
	} else if (cm.getPlayer().getMapId() == 450013600) {
		cm.setIngameDirectionMode(false, false, false);
		cm.setBlind(1, 255, 0, 0, 0, 0, 0);
		cm.environmentChange(7, "Bgm50/WorldHorizon", 0);
	}
	if (cm.getPlayer().getMapId() == 450013000) {
		cm.spineEffect("Effect/Direction20.img/bossBlackMage/start_spine/blasck_space", "animation", "intro", 0, 0, 1, 0);
		cm.effectSound("Sound/SoundEff.img/BM3/boss_start");
		cm.delay(5000);
	} else if (cm.getPlayer().getMapId() == 450013200) {
		cm.spineEffect("Effect/Direction20.img/bossBlackMage/start2_spine/skeleton", "animation", "intro", 0, 0, 1, 0);
		cm.effectSound("Sound/SoundEff.img/BM3/boss_start2");
		cm.delay(5000);
	} else if (cm.getPlayer().getMapId() == 450013400) {
		cm.spineEffect("Effect/Direction20.img/bossBlackMage/space/blasck_space", "animation", "intro", 0, 0, 1, 0);
		cm.effectSound("Sound/SoundEff.img/BM3/boss_start3");
		cm.delay(5000);
	} else if (cm.getPlayer().getMapId() == 450013600) {
		cm.spineEffect("Effect/Direction20.img/bossBlackMage/start4_spine/black_Phase_3_4", "animation", "intro", 0, 0, 1, 0);
		cm.effectSound("Sound/SoundEff.img/BM3/boss_start4");
		cm.delay(5000);
	}
    } else if (status == 1) {
	cm.environmentChange(31, "intro", 100);
	cm.getOnOffFade(100, "BlackOut", 0);
	cm.getPlayer().setRegisterTransferField(cm.getPlayer().getMapId() + 100);
	cm.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 1000);
	cm.getPlayer().setTransferFieldOverlap(true);
	cm.dispose();
    }
}