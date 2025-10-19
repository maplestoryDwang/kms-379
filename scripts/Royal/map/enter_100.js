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
        cm.getPlayer().setRegisterTransferField(110);
        cm.getPlayer().setTransferFieldOverlap(true);
        return;

        cm.effectText("#fn나눔고딕 ExtraBold##fs20#< 강림월드에 오신것을 환영합니다 >", 50, 1000, 6, 0, 380, -550);

        cm.getPlayer().send(Packages.network.models.CField.addPopupSay(1052206, 10000, "[강림월드]에 오신것을\r\n#r환영합니다!!!#k\r\n\r\nNPC 슈가를 통해\r\n#b이용규칙에 동의#k하신 다음\r\n원하는 직업으로 #r전직#k해 보세요!", ""));

        cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
        cm.dispose();
    }
}