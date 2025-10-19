var status;
var book  = new Array(1004808, 1004809, 1004810, 1004811, 1004812, 1082695, 1082696, 1082697, 1082698, 1082699, 1053063, 1053064, 1053065, 1053066, 1053067, 1073158, 1073159, 1073160, 1073161, 1073162, 1102940, 1102941, 1102942, 1102943, 1102944, 1152196, 1152197, 1152198, 1152199, 1152200, 1152196, 1152197, 1152198, 1152199, 1152200, 1212120, 1222113, 1232113, 1242121, 1262039, 1302343, 1312203, 1322255, 1332279, 1342104, 1362140, 1372228, 1382265, 1402259, 1412181, 1422189, 1432218, 1442274, 1452257, 1462243, 1472265, 1482221, 1492235, 1522143, 1532150, 1582023, 1272017, 1282017, 1213018, 1292018, 1592020,1214018);

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
		var text = "#b#z2630782##k에서 랜덤으로 지급이 됩니다.\r\n";
		for (var i = 0; i < book.length; i++) {
		    text+="#i"+book[i]+":# #z"+book[i]+":##l\r\n";
		}
		text += "상자를 개봉하시겠습니까?";
		cm.sendYesNo(text);
	} else if (status == 1) {
		item = book[Math.floor(Math.random() * book.length)];
		cm.gainItem(item, 1);
		cm.gainItem(2630782, -1);
		if (item == 2631094) {
			cm.getPlayer().getMap().broadcastMessage(Packages.network.models.CWvsContext.serverNotice(3, cm.getClient().getChannel(), "페어리월드 : [정보] " + cm.getPlayer().getName() + "님이 몬스터파크에서 [놀라운 장비강화 주문서] 아이템을 획득하였습니다!", false));
		}
		cm.sendOk("#i" + item + ":##z" + item + ":# 아이템이 수령되었습니다.");
		cm.dispose();
    	}
}
