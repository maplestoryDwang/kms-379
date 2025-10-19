importPackage(Packages.objects.item);

var item =[
	[3700514, 1],
	[2434628, 1],
	[4310237, 1000],
	[4310229, 1000],
	[4310266, 1000],
	[4310308, 500],
	[2435885, 1],
	[2437781, 1],
	[2431940, 20],
	[2450163, 10],
	[2024084, 10],
	[2024156, 10],
	[2439279, 10],
	[2438412, 1],
	[2632972, 1],
	[2437092, 7],
	[2438644, 5],
	[2434290, 50],
	[2633590, 4],
	[2633591, 2],
	[2430042, 5],
	[2634291, 1],
	[4001716, 30],
	[2436577, 50]
]

var boxcode = 2439542;

var keyname = "testbox";

var enter = "\r\n";

보라 = "#fMap/MapHelper.img/weather/starPlanet/7#";
파랑 = "#fMap/MapHelper.img/weather/starPlanet/8#";
별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#"
별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#"
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#"
별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#"
별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#"
별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#"
별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#"
별 = "#fUI/FarmUI.img/objectStatus/star/whole#"
보상 = "#fUI/UIWindow2.img/Quest/quest_info/summary_icon/reward#"
획득 = "#fUI/UIWindow2.img/QuestIcon/4/0#"
색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
핑크색 ="#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"

function start() {
	St = -1;
	action(1, 0, 0);
}

function action(M, T, S) {
	if(M != 1) {
		cm.dispose();
		return;
	}

	if(M == 1)
	    St++;

	if(St == 0) {
		var msg = "#fs11#[R] 요정의 지원 꾸러미를 사용 하시겠어요?\r\n\r\n#r※ 본 캐릭터에 수령 권장합니다. (교불 포함됨)\r\n※ 해당 상자는 이벤트 기간 내에 사용하시기 바랍니다.";
        msg += "\r\n\r\n#b < [R] 요정의 지원 꾸러미 보상 목록 >\r\n\r\n";
		msg += "이벤트에 참여 해주셔서 감사합니다. 모험가님!\r\n"+enter
		for(var i = 0; i<item.length; i++) {
			msg += "#i"+item[i][0]+"# #z"+item[i][0]+"# ("+item[i][1]+")"+enter
		}
		msg += "\r\n해당 아이템 리스트를 확인하고 지급을 받으시겠어요?"
		cm.sendYesNo(msg);
	} else if(St == 1) {
		// if (cm.getClient().getKeyValue(keyname) != null) {
		// 	cm.sendOk("오류 오류 박스 없음");
		// 	cm.gainItem(boxcode, -1);
		// 	cm.addCustomLog(99, boxcode+" / "+cm.getPlayer().getNamte()+"");
		// 	cm.dispose();
		// 	return;
		// }
		var invenuse = cm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot();
		var invenetc = cm.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot();
		if(invenuse < 30 || invenetc < 30){
			cm.sendOk("소비창과 기타창을 30칸이상 비워주세요.");
			cm.dispose();
			return;
		}
		if(!cm.haveItem(boxcode, 1)){
			cm.gainItem(boxcode, -1);
			cm.addCustomLog(99, boxcode+" / "+cm.getPlayer().getNamte()+"");
			cm.sendOk("오류 오류 박스 없음");
			cm.dispose();
			return;
		}
		var msg = "#r#fs11#더욱 더 강해지시길 응원합니다. 모험가님!\r\n#b"+enter
		for(var i = 0; i<item.length; i++) {
			msg += "#i"+item[i][0]+"# #z"+item[i][0]+"# ("+item[i][1]+")"+enter
			cm.gainItem(item[i][0], item[i][1]);
		}
		msg += "\r\n아이템이 정상적으로 지급되었습니다."
		//cm.getClient().setKeyValue(keyname, "1");
		cm.gainItem(boxcode, -1);
		cm.sendOk(msg);
		cm.dispose();
	}
}
