var status;
var select = -1; // 무기제거 1213017,1292017,1212115,1222109,1232109,1242120,1262017,1302333,1312199,1322250,1332274,1342101,1362135,1372222,1382259,1402251,1412177,1422184,1432214,1442268,1452252,1462239,1472261,1482216,1492231,1522138,1532144,1582017,1272016,1282016,1592019,1214017,
var book  = new Array(1004422, 1004423, 1004424, 1004425, 1004426,1052882,1052887,1052888,1052889,1052890,1152174,1152176,1152177,1152178,1152179,1102775,1102794,1102795,1102796,1102797,1073030,1073032,1073033,1073034,1073035,1082636,1082637,1082638,1082639,1082640);

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
	    var text = "받고 싶은 앱솔랩스 아이템을 선택해줘#l\r\n\r\n#b";
		for (var i = 0; i < book.length; i++) {
		    text+="#L"+i+"##i"+book[i]+"# #z"+book[i]+"##l\r\n";
		}
		cm.sendSimple(text);
	} else if (status == 1) {
		select = selection;
		cm.sendYesNo("받을 앱솔랩스 아이템이 #b#z"+book[select]+"##k 맞아?");
	} else if (status == 2) {
	    if (cm.haveItem(2630704, 1)) {
		if (cm.canHold(2630704)) {
		    cm.sendOk("인벤토리를 확인하세요");
		    cm.gainItem(2630704, -1);
		    cm.gainSponserItem(book[select], "초기지원", 100, 50, 0);
		    //cm.gainItem(book[select], 1);
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






