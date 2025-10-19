
var status;
var select = -1;
var book  = new Array(1004808, 1004809, 1004810, 1004811, 1004812, 1082695, 1082696, 1082697, 1082698, 1082699, 1053063, 1053064, 1053065, 1053066, 1053067, 1073158, 1073159, 1073160, 1073161, 1073162, 1102940, 1102941, 1102942, 1102943, 1102944, 1152196, 1152197, 1152198, 1152199, 1152200, 1152196, 1152197, 1152198, 1152199, 1152200, 1212120, 1222113, 1232113, 1242121, 1262039, 1302343, 1312203, 1322255, 1332279, 1342104, 1362140, 1372228, 1382265, 1402259, 1412181, 1422189, 1432218, 1442274, 1452257, 1462243, 1472265, 1482221, 1492235, 1522143, 1532150, 1582023, 1272017, 1282017, 1213018, 1292018, 1592020,1214018);

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
	    var text = "받고 싶은 아케인셰이드 아이템을 선택해줘#l\r\n\r\n#b";
		for (var i = 0; i < book.length; i++) {
		    text+="#L"+i+"##i"+book[i]+"# #z"+book[i]+"##l\r\n";
		}
		cm.sendSimple(text);
	} else if (status == 1) {
		select = selection;
		cm.sendYesNo("받을 아케인셰이드 아이템이 #b#z"+book[select]+"##k 맞아?");
	} else if (status == 2) {
	    if (cm.haveItem(2630133, 1)) {
		if (cm.canHold(2630133)) {
		    cm.sendOk("인벤토리를 확인하세요");
		    cm.gainItem(2630133, -1);
		    cm.gainItem(book[select], 1);
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