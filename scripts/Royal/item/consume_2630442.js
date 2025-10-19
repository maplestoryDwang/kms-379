var status;
importPackage(Packages.server);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.server.items);
one = Math.floor(Math.random() * 5) + 1 // 최소 10 최대 35 , 혼테일
function start() {
    status = -1;
    action(1, 1, 0);
}

var 패키지명 = "접속보상";
var 패키지코드 = 2630442;
var enter = "\r\n";

var itemlist = [
{'itemid' : 4001715, 'qty' : 10}, // 10억
{'itemid' : 4031227, 'qty' : 50}, // 찬빛결
{'itemid' : 4310308, 'qty' : 500}, // 네오코어
{'itemid' : 4310237, 'qty' : 500}, // 사냥코인
{'itemid' : 4310266, 'qty' : 500}, // 승급코인
{'itemid' : 5062010, 'qty' : 300}, // 블랙큐브
{'itemid' : 5062500, 'qty' : 300}, // 에디큐브
{'itemid' : 5060048, 'qty' : 3}, // 골드애플

]

function action(mode, type, sel) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
        if (status == 0) {
		if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.EQUIP).getNumFreeSlot() < 5 || cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() < 5 || cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.ETC).getNumFreeSlot() < 5) {
		   cm.sendOk("#fs11#장비,소비,기타 칸을 5칸 이상 비워주세요");
		   cm.dispose();
		   return;
		}

		var msg = "#b#e[" + 패키지명 + "] #k#n에서\r\n다음과같은 #b보상#k을 지급 받으시겠습니까?\r\n" + enter + enter;
		for (i = 0; i < itemlist.length; i ++) {
			//cm.gainItem(itemlist[i]['itemid'],itemlist[i]['qty']);
			msg += "#b#e#i"+itemlist[i]['itemid']+"# #z"+itemlist[i]['itemid']+"# "+itemlist[i]['qty']+"개 \r\n";
		}
		msg += "#L1#지급받겠습니다.";
		msg += "#L2#다음에";

		cm.sendSimple(msg);

	} else if(status == 1) {
		if (sel == 1) {
			cm.dispose();
			for (i = 0; i < itemlist.length; i ++) {
			cm.gainItem(itemlist[i]['itemid'],itemlist[i]['qty']);
			}
			cm.gainItem(2630442, -1);
			//cm.getPlayer().AddStarDustPoint(100, cm.getPlayer().getTruePosition());
			cm.sendOkS("#b#e접속 보상 지급이 완료되었습니다.", 2);

			//로그작성
			Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[HOTTIME]" + 패키지명 + ".log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n사용한 아이템 : " + 패키지명 + " (" + 패키지코드 + ")" + "\r\n\r\n", true);

		} else if (sel == 2) {
			cm.dispose();
			cm.sendOk("네");
		}
	}
}

