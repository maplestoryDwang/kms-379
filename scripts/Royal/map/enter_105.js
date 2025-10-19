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
        if (mailboxcheck()) {
            cm.getPlayer().dropMessage(-22, "[우편함] 수령하지 않은 아이템이 있습니다. 이벤트 NPC - 우편함에서 수령해 주세요!");
        }
        cm.effectText("#fn나눔고딕 ExtraBold##fs16#< 강림월드 > - 강림 정원", 100, 1000, 6, 0, 430, -550);

        cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
        cm.dispose();
    }
}

function mailboxcheck() {
    Check = false;
    try {
        con = DBConnection.getConnection();
        ps = con.prepareStatement("SELECT * FROM `offline` WHERE `chrid` = " + cm.getPlayer().getId() + " AND status = 0");
        rs = ps.executeQuery();
        while (rs.next()) {
            Check = true;
        }
        rs.close();
        ps.close();
        con.close();
        return Check;
    } catch (e) {
        return false;
    }
}