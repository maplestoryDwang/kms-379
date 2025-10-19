importPackage(Packages.database);
importPackage(java.lang);

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
        cm.dispose();
        cm.getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
        cm.getPlayer().setRegisterTransferField(121);
        cm.getPlayer().setTransferFieldOverlap(true);
        return;

        cm.effectText("#fn나눔고딕 ExtraBold##fs16#< 강림월드 > - 강림 타운", 100, 1000, 6, 0, 430, -550);

        cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
        cm.dispose();
    }
}
