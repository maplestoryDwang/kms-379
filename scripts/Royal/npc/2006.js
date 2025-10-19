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
        두달전 = System.currentTimeMillis() - 24 * 3600 * 1000 * 60 + "\r\n";
        한달전 = System.currentTimeMillis() - 24 * 3600 * 1000 * 30 + "\r\n";
        어제 = System.currentTimeMillis() - 24 * 3600 * 1000 * 1 + "\r\n";
        현재시간 = System.currentTimeMillis() + "\r\n";
        내일 = System.currentTimeMillis() + 24 * 3600 * 1000 * 1 + "\r\n";
        한달후 = System.currentTimeMillis() + 24 * 3600 * 1000 * 30 + "\r\n";
        cm.sendOk("#i61500##z61500#두달전 : " + 두달전 + "한달전 : " + 한달전 + "어제 : " + 어제 + "현재시간 : " + 현재시간 + "내일 : " + 내일 + "한달후 : " + 한달후);
        cm.dispose();
    }
}
