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
	cm.setIngameDirectionMode(false, false, false);
	cm.setBlind(1, 255, 0, 0, 0, 0, 0);
	cm.environmentChange(7, "Bgm00.img/Silence", 0);
	cm.spineEffect("Effect/Direction20.img/bossWill/intro_spine/skeleton", "2", "intro", 0, 0, 1, 0);
	cm.effectSound("Sound/SoundEff.img/esfera/boss/intro2");
	cm.delay(5500);
    } else if (status == 1) {
	cm.environmentChange(31, "intro", 100);
	cm.getOnOffFade(100, "BlackOut", 0);
	cm.getPlayer().setRegisterTransferField(cm.getPlayer().getMapId() + 50);
	cm.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 1000);
	cm.getPlayer().setTransferFieldOverlap(true);
	cm.dispose();
    }
}