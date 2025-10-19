var enter = "\r\n";
var seld = -1;

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
        var msg = "#fs11#마음의 준비를 하는게 좋을거야~ #k\r\n\r\n";
        msg += "#L2# #d존의 진심이 담겨있는 꽃 #b(보상#i4021031# 500개)#l#k#n\r\n";
        msg += "#L3# #d슈미가 잃어버린 동전 #b(보상#i4021031# 500개)#l#k#n\r\n";
        msg += "#L4# #d크리슈라마의 만드라고라 #b(보상#i4021031# 500개)#l#k#n\r\n";
        msg += "#L5# #d작전 명! 폐광 탐사 작전 #b(보상#i4021031# 500개)#l#k#n\r\n";
        cm.sendSimple(msg);
    } else if (status == 1) {
        seld = sel;
        switch (sel) {
            case 2:
                cm.dispose();
                cm.openNpc(20000);
            break;
            case 3:
                cm.dispose();
                cm.openNpc(1052102);
            break;
            case 4:

                cm.dispose();
                cm.openNpc(1061000);
            break;
            case 5:
                cm.dispose();
                cm.openNpc(2010000);
            break;
        }
    }
}