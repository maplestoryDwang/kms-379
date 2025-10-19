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
	cm.spineEffect("Effect/Direction20.img/bossSeren/1pahse_spine/skeleton", "animation", "intro", 0, 0, 1, 0);
	cm.effectSound("Sound/SoundEff.img/seren/1phase");
	cm.delay(12000);
    } else if (status == 1) {
	//cm.getOnOff(1000, "BlackOut", 0, 0, 13, "Map/Effect2.img/BlackOut", 4, 1, -1, 0);
	cm.environmentChange(31, "intro", 100);
	cm.getOnOffFade(100, "BlackOut", 0);
	cm.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
	cm.getPlayer().setRegisterTransferField(cm.getPlayer().getMapId() + 20);
	cm.getPlayer().setTransferFieldOverlap(true);
	cm.dispose();
    }
}