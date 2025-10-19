importPackage(java.lang);
var status;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        var chat = "#e#r<로얄메이플 : 프리미엄 시즌 패스>#b\r\n\r\n\r\n";
        chat += "#L1#로얄메이플 : 프리미엄 시즌 패스 이용하기";
        cm.sendSimple(chat);
    } else if (status == 1) {
        if (selection == 1) {
            if (cm.haveItem(4001760, 1)) {
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9062284, "프리미엄패스");
            } else {
                cm.sendOk("#e#r#i4001760##z4001760#이 없습니다 입장하실수 없습니다#b");
                cm.dispose();
                return;
            }
        } else {
            cm.dispose();
            return;
        }
    }
}
