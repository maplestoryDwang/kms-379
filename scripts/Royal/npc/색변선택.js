importPackage(Packages.database);
importPackage(java.lang);

var enter = "\r\n";

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

        msg = "#fs11##fUI/Basic.img/Zenia/SC/4#\r\n";
        msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#\r\n";
        msg += "             #L1##fUI/Basic.img/Zenia/SCBtn/110##l";
        msg += "#L2##fUI/Basic.img/Zenia/SCBtn/111##l";
        cm.sendSimple(msg);
    } else if (status == 1) {
        seld = sel;
        switch (sel) {
            case 1:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "색변캐시");
                break;
            case 2:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "색변캐시2");
                break;
        }
    }
}
