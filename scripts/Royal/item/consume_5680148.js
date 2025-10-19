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
        qty = cm.getPlayer().getItemQuantity(5680148, false);
        cm.dispose();
        cm.gainItem(5680148, -qty);
        cm.getPlayer().dropMessage(1, "사용되지 않는 아이템입니다.\r\n해당 아이템이 삭제됩니다.");
    }
}
