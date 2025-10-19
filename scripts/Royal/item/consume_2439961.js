var status;
var select = -1;
var book  = new Array(1009500, 1009501, 1009502, 1009503, 1009504, 1159500, 1159501, 1159502, 1159503, 1159504, 1109500, 1109501, 1109502, 1109503, 1109504, 1089500, 1089501, 1089502, 1089503, 1089504, 1079500, 1079501, 1079502, 1079503, 1079504);

function start() {    status = -1;
    action(1, 1, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    
    if (status == 0) {
        var text = "#fs11#받고 싶은 초월한 아케인셰이드 아이템을 선택해줘#l\r\n\r\n#b";
        for (var i = 0; i < book.length; i++) {
            text+="#L"+i+"##i"+book[i]+"##z"+book[i]+"##l\r\n";
        }
        cm.sendSimple(text);
    } else if (status == 1) {
        select = selection;
        cm.sendYesNo("#fs11#받고싶은 아케인셰이드 아이템이\r\n\r\n#b#i" + book[select] + "##z"+book[select]+"##k 맞아?");
    } else if (status == 2) {
        if (!cm.haveItem(2439961, 1)) {
            cm.sendOk("#fs11#상자가 부족합니다.");
            cm.dispose();
        }
        if (cm.canHold(book[select])) {
            cm.sendOk("#fs11##b#i" + book[select] + "##z" + book[select] + "# #k아이템이 지급되었습니다.");
            cm.gainItem(2439961, -1);
            cm.gainItem(book[select], 1);
            cm.dispose();
        } else {
            cm.sendOk("#fs11#장비칸에 빈 공간이 없습니다.");
            cm.dispose();
        }
    }
}