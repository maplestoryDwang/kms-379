importPackage(java.sql);
importPackage(java.lang);
importPackage(Packages.database);
importPackage(Packages.handling.world);
importPackage(Packages.constants);
importPackage(java.util);
importPackage(java.io);
importPackage(Packages.client.inventory);
importPackage(Packages.client);
importPackage(Packages.server);
importPackage(Packages.tools.packet);

기본지급 = [1004404, 1052893, 1102799];
전사 = [1302334, 1402252, 1412178, 1422185, 1432215, 1442269, 1582021, 1232110, 1312200, 1322251, 1213023];
마법사 = [1212116, 1372223, 1382260, 1262027, 1282019];
궁수 = [1452253, 1462240, 1522139, 1592008, 1214020];
도적 = [1472262, 1332275, 1362136, 1272031, 1292023];
해적 = [1492232, 1482217, 1222110, 1532145];
제논 = [1242117];

var purple = "#fc0xFF7401DF#";

function start() {
    status = -1;
    action (1, 0, 0);
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
	var text = "#fn나눔고딕#" + purple + "제로 신규유저를 위한 지원장비 입니다.#k#n\r\n\r\n";
	text += "#d자신의 직업에 맞는 장비를 선택해주세요#k\r\n\r\n\r\n";
	text += "#b#L1#전사#k\r\n";
	text += "#fUI/UIWindow2.img/QuestIcon/3/0#\r\n";

		break;
	}
	cm.sendSimple(text);
	} else if (status == 1) {
	    var a = "#fn나눔고딕#" + purple + "<제로>#k#n\r\n\r\n";
	    a += "#e#b#h0##k#n 님을 위한 장비를 지급해드렸습니다\r\n즐거운 시간 되시길 바랍니다!";
	    for (i = 0; i < 기본지급.length; i++) {
	        a += "#b#i"+ 기본지급[i] +"# #z"+ 기본지급[i] +"# #k\r\n";
	        var inz =Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(기본지급[i]);
	        inz.setReqLevel(-90);
	    inz.setStr(80);
	    inz.setDex(80);
	    inz.setInt(80);
	    inz.setLuk(80);
                  inz.setState(4);
	        Packages.objects.item.MapleInventoryManipulator.addbyItem(cm.getClient(), inz);
	    }
	    a += "#b#i"+ selection +"# #z"+ selection +"##k #e#r(선택한 아이템)#k#n\r\n";
	    var inz =Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(selection);
	    inz.setReqLevel(-90);
	    inz.setStr(80);
	    inz.setDex(80);
	    inz.setInt(80);
	    inz.setLuk(80);
	    Packages.objects.item.MapleInventoryManipulator.addbyItem(cm.getClient(), inz);
	    cm.gainItem(2436226,-1);
	    cm.sendSimple(a);
	    cm.dispose();
	}
}