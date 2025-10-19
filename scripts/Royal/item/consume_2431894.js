function start() {
    status = -1;
    action(1, 1, 0);
}

function action(mode, type, sel) {
    itemid = 2431894;
    reward = 5062010;
    basecount1 = 10;
    basecount2 = 100;
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        if (cm.itemQuantity(itemid) < 50) {
            cm.sendOk("#fs11##b#i" + itemid + "##z" + itemid + "##r 50개#k당\r\n#i" + reward + "##b#z" + reward + "# #r" + basecount1 + "개#k로 교환이 가능합니다.");
            cm.dispose();
            return;
        } else {
            talk = "#fs11##b#i" + itemid + "##z" + itemid + "# #r50개#k당\r\n#b#i" + reward + "##z" + reward + "# #r" + basecount1 + "개#k로 교환 할 수 있습니다.\r\n\r\n"
            talk += "#L0##i" + reward + "##b#z" + reward + "# " + basecount1 + "개#l#k";
            talk += "#L1##i" + reward + "##b#z" + reward + "# " + basecount2 + "개#l#k";
            talk += "\r\n\r\n\r\n";
            talk += "#fc0xFFFF3366#※ #z" + itemid + "# " + cm.itemQuantity(itemid) + "개를 가지고 있습니다.";
            cm.sendSimple(talk);
        }
    } else if (status == 1) {
        switch (sel) {
            case 0:
                if (cm.itemQuantity(itemid) < 50) {
                    cm.sendOk("#fs11##b#i" + itemid + "##z" + itemid + "##r 50개#k를 모으면\r\n#i" + reward + "##b#z" + reward + "# #r" + basecount1 + "개#k로 교환 할 수 있습니다.");
                } else {
                    cm.gainItem(itemid, -50);
                    cm.gainItem(reward, basecount1);
                }
                    cm.dispose();
                    return;
            case 1:
                if (cm.itemQuantity(itemid) < 500) {
                    cm.sendOk("#fs11##b#i" + itemid + "##z" + itemid + "##r 500개#k를 모으면\r\n#i" + reward + "##b#z" + reward + "# #r" + basecount2 + "개#k로 교환 할 수 있습니다.");
                } else {
                    cm.gainItem(itemid, -500);
                    cm.gainItem(reward, basecount2);
                }
                    cm.dispose();
                    return;
            reutrn;
        }
    }
}