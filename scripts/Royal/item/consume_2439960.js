var status;
var book  = new Array(1009500, 1009501, 1009502, 1009503, 1009504, 1159500, 1159501, 1159502, 1159503, 1159504, 1109500, 1109501, 1109502, 1109503, 1109504, 1089500, 1089501, 1089502, 1089503, 1089504, 1079500, 1079501, 1079502, 1079503, 1079504);

function start() {
    status = -1;
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
        var text = "#fs11##b#z2439960##k에서\r\n아래의 아이템이 랜덤으로 지급이 됩니다.\r\n";
        for (var i = 0; i < book.length; i++) {
            text+="#b#i"+book[i]+":##z"+book[i]+":##l\r\n";
        }
        text += "\r\n#k상자를 개봉하시겠습니까?";
        cm.sendYesNo(text);
    } else if (status == 1) {
        item = book[Math.floor(Math.random() * book.length)];
        cm.gainItem(item, 1);
        cm.gainItem(2439960, -1);
                        cm.sendOk("#fs11##b#i" + item + "##z" + item + "# #k아이템이 지급되었습니다.");
        cm.dispose();
    }
}