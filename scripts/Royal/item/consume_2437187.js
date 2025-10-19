importPackage(Packages.objects.item);
importPackage(java.util);
importPackage(java.io);

var item =[
	[1113098, 1],
]
var day = 90;
var boxcode = 2437187;

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
		var msg = "#fs11#[R] 리스트레인트 링을 지급 받으시겠어요?\r\n\r\n#r※ 본 캐릭터에 수령 권장합니다. (교환 불가 아이템)\r\n※ 지급되는 아이템은 90일 기간제 아이템으로 지급됩니다.";
        msg += "\r\n\r\n#b < 교환 아이템 안내 >\r\n\r\n";
		msg += "해당 아이템은 스킬 쿨타임이 존재합니다.\r\n"+enter
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
		if(invenuse < 10 || invenetc < 10){
			cm.sendOk("소비창과 기타창을 10칸이상 비워주세요.");
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
		}
		getitem(item[0][0])
		msg += "\r\n아이템이 정상적으로 지급되었습니다."
		//cm.getClient().setKeyValue(keyname, "1");
		cm.gainItem(boxcode, -1);
		cm.sendOk(msg);
		cm.dispose();
	}
}

function getitem(itemcode){
	item = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(itemcode);
	item.setTheSeedRingLevel(4);
	item.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * day));
	Packages.objects.item.MapleInventoryManipulator.addbyItem(cm.getClient(), item, false);
	cm.dispose();
}
