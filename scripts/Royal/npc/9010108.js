importPackage(java.lang);

importPackage(Packages.constants);

var status = -1;
var s = -1;
var coin = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status--;
        cm.dispose();
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
	var v = cm.getPlayer().getOneInfoQuest(18790, "coin");
	coin = 0;
	if (v != null && !v.isEmpty()) {
		coin = parseInt(v);
	}
	if (coin > 0) {		
		s = 0;
		cm.sendNext("#b#i4310229:##t4310229##k을 #b" + coin + "개#k나 모으셨군요? 대단해요~\r\n\r\n그럼 광장으로 보내 드릴게요. 안녕히 가세요~");
	} else {
		s = 1;
		cm.sendNext("음~ 아직 유니온 코인을 하나도 얻지 못하셨군요? 획득이 너무 어려우시다면 시간을 조금 가진 뒤 들어와 보세요. 유니온의 구성원들이 열심히 코인을 모아 둘 거예요.");
	}
    } else if (status == 1) {
   	if (s == 0) {
		var point = getUnionCoin() + coin;
		cm.getPlayer().gainItem(4310229, coin);
		cm.getPlayer().updateOneInfo(500629, "point", point + "");
		cm.getPlayer().updateOneInfo(18098, "coin", "0");
		cm.getPlayer().updateOneInfo(18790, "coin", "0");
		cm.warp(ServerConstants.TownMap, 0);
		cm.dispose();
	} else {
		cm.sendNext("그럼 광장으로 보내 드릴게요. 안녕히 가세요~");
	}
    } else if (status == 2) {
    	cm.warp(15);
	cm.dispose();
    }
}

function getUnionCoin() {
	var pv = cm.getPlayer().getOneInfoQuest(500629, "point");
	var point = 0;
	if (pv != null && !pv.isEmpty()) {
		point = parseInt(pv);
	}
	return point;
}