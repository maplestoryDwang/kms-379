importPackage(Packages.client.inventory);
importPackage(Packages.client.stats);
importPackage(java.lang);

function start() {
	status = -1;
	action(1, 0, 0);
}

var enter = "\r\n";

var item1 = [2430783, "#z2430783#", "1"];
var ac1 = [4036660, "[R] 아이스 박스", "2"];
var ac2 = [4032036, "[R] 캐시 추첨 티켓", "1"];
var 별 = "#fUI/FarmUI.img/objectStatus/star/whole#";

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
cm.sendOk("#fn나눔고딕#이벤트 기간이 종료되었습니다.");
cm.dispose();
return;;

			var chat = "#fs11##fn나눔고딕##fc0xFF7401DF#< 교환시스템 :: 여름이벤트 아이스박스 교환 >#k" + enter + "" + enter;
			chat += "#r#e"+별+"인벤토리 공간을 꼭 확보해주신후 사용해주시길 바랍니다"+ enter + enter + "" ;
			chat += "#L1#" +  별 + ac1[1] + " 교환" 
			//+ enter + "#L2##i" + ac2[0] + "# " + ac2[1] + " 교환"
			//+ enter + "#L7##i" + ac2[0] + "# " + ac2[1] + " 교환"
			//+ enter + "#L4##i2437529#강화석 교환"
			cm.sendOkS(chat, 0x00);
		} else if (status == 1) {
			select = selection;
			if (select == 1) {
				var suk1 = Math.floor((cm.itemQuantity(2430783) / 1));
				stigmacoin = Math.min(suk1);
				cm.sendGetNumber("\r\n#fn나눔고딕##i" + item1[0] + "# " + item1[1] + " #fc0xFF7401DF#" + item1[2] + "#k 개를 주시면" + enter
					+ enter + "#i" + ac1[0] + "##z" + ac1[0] + " # #fc0xFF7401DF#" + ac1[2] + "#k 개와"
					+ enter + "#i" + ac2[0] + "##z" + ac2[0] + " # #fc0xFF7401DF#" + ac2[2] + "#k 개로 교환해 드립니다"
					+ enter + "#Cgray#(현재 교환 가능한 " + item1[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 100);
			}
			
		} else if (status == 2) {
			var cost = selection;
			if (cost > 100) {
				cm.dispose();
				return;
			}

			if (select == 1) {
				if (cm.haveItem(item1[0], item1[2] * cost)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.ETC).getNumFreeSlot() >= 2) {
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(ac1[0], ac1[2] * cost);
						cm.gainItem(ac2[0], ac2[2] * cost);
						cm.sendOk("#fn나눔고딕#교환완료\r\n");
						cm.dispose();

						//로그작성
						Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/여름이벤트.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n사용한 아이템 : [R] 아이스 박스 (2430783)\r\n사용 개수 : " + cost + "\r\n\r\n", true);
					} else {
						cm.sendOk("#fn나눔고딕##r기타칸을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r#i" +  item1[0] + "#" + item1[1]  + "가 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}
			
		}
	}
}