
var status;
var select = -1;
var book  = new Array(1212990, 1222990, 1232990, 1242990, 1262990, 1302990, 1312990, 1322990, 1332990, 1342990, 1362990, 1372990, 1382990, 1402990, 1412990, 1422990, 1432990, 1442990, 1452990, 1462990, 1472990, 1482990, 1492990, 1522990, 1532990, 1582990, 1272990, 1282990, 1213990, 1292990, 1592990, 1214990);

function start() {    status = -1;
    action(1, 1, 0);
}


function action(mode, type, selection) {
    if (mode <= 0) {
        cm.dispose();
    	return;
    } else {
        if (mode == 1)
            status++;
        if (status == 0) {
	    var text = "받고 싶은 초월한 아케인셰이드 아이템을 선택해줘#l\r\n\r\n#b";
		for (var i = 0; i < book.length; i++) {
		    text+="#L"+i+"##i"+book[i]+"# #z"+book[i]+"##l\r\n";
		}
		cm.sendSimple(text);
	} else if (status == 1) {
		select = selection;
		cm.sendYesNo("받을 아케인셰이드 아이템이 #b#z"+book[select]+"##k 맞아?");
	} else if (status == 2) {
	    if (cm.haveItem(2439963, 1)) {
		if (cm.canHold(book[select])) {
		    cm.sendOk("인벤토리를 확인하세요");
		    cm.gainItem(2439963, -1);
			cm.gainZeniaItem(book[select], "[홍보]", 400, 250, 10, 0, 30, 20, 0, 9, 40070);
		    //cm.gainItem(book[select], 1, 1);
		    cm.dispose();
		} else {
		    cm.sendOk("장비칸에 빈 공간이 없습니다.");
		    cm.dispose();
		}
            } else {
		cm.sendOk("부족합니다.");
		cm.dispose();

}
	}
    }
}