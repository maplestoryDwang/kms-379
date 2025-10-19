var status;
var book  = new Array(1213017,1292017,1212115,1222109,1232109,1242120,1262017,1302333,1312199,1322250,1332274,1342101,1362135,1372222,1382259,1402251,1412177,1422184,1432214,1442268,1452252,1462239,1472261,1482216,1492231,1522138,1532144,1582017,1272016,1282016,1592019,1214017,1152174,1152176,1152177,1152178,1152179,1102775,1102794,1102795,1102796,1102797,1073030,1073032,1073033,1073034,1073035,1052882,1052887,1052888,1052889,1052890,1082636,1082637,1082638,1082639,1082640,1004422, 1004423, 1004424, 1004425, 1004426);


function start() {
    status = -1;
    action(1, 1, 0);
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
		var text = "#z2630291#에서 랜덤으로 지급이 됩니다.\r\n";
		for (var i = 0; i < book.length; i++) {
		    text+="#i"+book[i]+":# #z"+book[i]+":##l\r\n";
		}
		text += "상자를 개봉하시겠습니까?";
		cm.sendYesNo(text);
	} else if (status == 1) {
		item = book[Math.floor(Math.random() * book.length)];
		cm.gainItem(item, 1);
		cm.gainItem(2630291, -1);
		if (item == 2631094) {
			cm.getPlayer().getMap().broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(3, cm.getClient().getChannel(), "페어리월드 : [정보] " + cm.getPlayer().getName() + "님이 몬스터파크에서 [놀라운 장비강화 주문서] 아이템을 획득하였습니다!", false));
		}
		cm.sendOk("#i" + item + ":##z" + item + ":# 아이템이 수령되었습니다.");
		cm.dispose();
    	}
}
