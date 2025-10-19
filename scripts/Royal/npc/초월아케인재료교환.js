var enter = "\r\n";
var seld = -1;

별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#"
별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#"
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#"
별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#"
별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#"
별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#"
별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#"
별 = "#fUI/FarmUI.img/objectStatus/star/whole#";
S = "#fUI/CashShop.img/CSEffect/today/0#"
색 = "#fc0xFF6600CC#"

var need = [
	{'itemid' : 4038001, 'qty' : 10},
	{'itemid' : 4038000, 'qty' : 1}
]
var tocoin = 4318000, toqty = 1;

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
		var msg = "#fs11#저희 시간의 신관들은 무지개빛으로 물든 로얄메이플 지역을 조사하고 있습니다. 혹시 무지개빛 으로 물든 구슬과 조각을 가지고 계신가요?"+enter;

		for (i = 0; i < need.length; i++) {
			if (i != need.length - 1) msg += "#i"+need[i]['itemid']+"##z"+need[i]['itemid']+"# "+need[i]['qty']+"개와"+enter;
			else msg += "#fs11##i"+need[i]['itemid']+"##z"+need[i]['itemid']+"# "+need[i]['qty']+"개를 주신다면 조사에 큰 도움이 될겁니다. 대신 제가 가진 #b#i"+tocoin+"##z"+tocoin+"##k을 드리죠\r\n" + 별 + 색 + " 구슬은 일일퀘스트 , 조각은 상위 보스에서 나올거 같은데요?"+enter;
		}
		
		if (haveNeed(1))
			cm.sendNext(msg);
		else {
			msg += enter+enter+"허나.. 당신이 교환할 수 있는 아이템이 없군요..";
			cm.sendOk(msg);
			cm.dispose();
		}
	} else if (status == 1) {
		temp = [];
		for (i = 0; i < need.length; i++) {
			temp.push(Math.floor(cm.itemQuantity(need[i]['itemid']) / need[i]['qty']));
		}
		temp.sort();
		max = temp[0];
		cm.sendGetNumber("당신은 최대 #b"+max+"개를#k 교환할 수 있군요..\r\n몇 개를 교환하시겠습니까...?", 1, 1, max);
	} else if (status == 2) {
		if (!haveNeed(sel)) {
			cm.sendOk("당신이 소지한 아이템이 부족합니다.");
			cm.dispose();
			return;
		}
		for (i = 0; i < need.length; i++) {
			cm.gainItem(need[i]['itemid'], -(need[i]['qty'] * sel));
		}
		cm.gainItem(tocoin, (toqty * sel));
		cm.sendOk("#z4310249#을 지급해드렸습니다.");
		cm.dispose();
	}
}

function haveNeed(a) {
	var ret = true;
	for (i = 0; i < need.length; i++) {
		if (!cm.haveItem(need[i]['itemid'], (need[i]['qty'] * a)))
			ret = false;
	}
	return ret;
}
