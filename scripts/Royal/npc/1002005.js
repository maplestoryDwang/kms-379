importPackage(java.lang);

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
S = "#fUI/CashShop.img/CSEffect/today/0#"
보상 = "#fUI/UIWindow2.img/Quest/quest_info/summary_icon/reward#"
획득 = "#fUI/UIWindow2.img/QuestIcon/4/0#"
색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

var enter = "\r\n";
var seld = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}
function action(mode, type, sel) {
	if (mode == 1) {
		status++;
	} else {
		cm.dispose();
		return;
    	}
	if (status == 0) {
		var msg ="#fs11#반갑네 #h #! 무엇이든 맡아주지.\r\n#fs11##d"+enter;
		msg += "#L1#" + 색 + "[일반창고] " + 검은색 + "이용하기"+enter;
		//msg += "#L2#" + 색 + "[캐시창고] " + 검은색 + "이용하기 (사용불가)";
		cm.sendSimple(msg);
		//cm.sendStorage();
		//cm.openNpcCustom(cm.getClient(), 9000213, "캐시창고");
	} else if (status == 1) {
		seld = sel;
		switch (seld) {
			case 1:
				cm.dispose();
				cm.sendStorage();
			break;
			case 2:
				cm.dispose();
				cm.openNpcCustom(cm.getClient(), 9000213, "캐시창고");
			break;
		}
	}
}