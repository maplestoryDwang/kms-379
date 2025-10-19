/*

채원

*/


검정 = "#fc0xFF191919#"
var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status --;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        말 = "#z2430001#로 원하는 아이템을 선택해보세요.\r\n\r\n";
        말 += "#e<선택 아이템>#n\r\n";
        말 += "#L0##i2430002# #b#z2430002##k\r\n";
        말 += "#L1##i2430003# #b#z2430003##k";
        cm.sendSimpleS(말, 0x04);
        return;
    } else if (status == 1) {
        if (selection == 0) {
            if (cm.haveItem(2430001, 10)) {
                if (cm.canHold(2430002)) {
                   cm.gainItem(2430001, -10);
                   cm.gainItem(2430002, 1);
                   cm.sendOk("#i2430002# #b#z2430002##k를 지급해드렸어요.");
                   cm.dispose();
                } else {
                   cm.sendOk("소비창에 공간이 없는거 같아요.");
                   cm.dispose();
                }
             } else {
                cm.sendOk("#b#z2430001##k 아이템이 #r10개#k가 있어야 교환이 가능합니다.");
                cm.dispose();
             }
        } else if (selection == 1) {
            if (cm.haveItem(2430001, 10)) {
                if (cm.canHold(2430003)) {
                   cm.gainItem(2430001, -10);
                   cm.gainItem(2430003, 1);
                   cm.sendOk("#i2430003# #b#z2430003##k를 지급해드렸어요.");
                   cm.dispose();
                } else {
                   cm.sendOk("소비창에 공간이 없는거 같아요.");
                   cm.dispose();
                }
            } else {
                cm.sendOk("#b#z2430001##k 아이템이 #r10개#k가 있어야 교환이 가능합니다.");
                cm.dispose();
            }
        }
    }
}
