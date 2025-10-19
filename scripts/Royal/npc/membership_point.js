var status = -1;
var info = false;

function start() {
    status = -1;
    info = false;
    action(1, 0, 0);
}



function action(mode, type, selection) {
    if (mode == 0 && type == 3 && selection == -1) {
	info = true;
	cm.sendNext("#e#b<VIP 멤버십 등급>#n#k은 구매하신 #e#r<VIP 멤버십 포인트>#n#k에 따라 올라간답니다.\r\n특별한 혜택을 드리기 위한 #e최소한의 검증#n이랄까요.");
	return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
	var v0 = "안녕하세요. #e#b#h0##k#n님.\r\n무엇을 도와드릴까요?\r\n\r\n";
	v0 += "#L0##e#r<VIP 멤버십 포인트>#k#n를 구매하고 싶어.#l";
	cm.sendSimple(v0);
    } else if (status == 1) {
	cm.sendYesNo("네오스톤을 #e#r500개#k#n 지불하고 #e#b<VIP 멤버십 포인트>#k#n를\r\n구매하시겠어요?");
    } else if (status == 2) {
	if (info) {
		cm.sendNext("등급이 높을수록 #e#r더 좋은 혜택과 서비스#n#k가 준비되어 있다\r\n는 것을 알려드리고 싶군요.");
		cm.dispose();
	} else {
        var coinCount = cm.getPlayer().getStackEventGauge(0);
		if (coinCount <= 500) {
			cm.sendNext("네오스톤이 부족한거 같은데요? #b네오스톤 500개#k가 필요하답니다.");
			cm.dispose();
			return;
		} else {
			var lv = cm.getPlayer().getOneInfoQuestInteger(501045, "lv");
			if (lv >= 4) {
				cm.sendNext("#e#b#h0##k#n님은 이미 #e#rVIP PRESTIGE#k#n 등급이라 더 이상 #e#rVIP 멤버십 포인트#k#n를 구매할 수 없어요.");
				cm.dispose();
				return;
			}	
			if (cm.getPlayer().getOneInfoQuestInteger(501045, "mp") >= 1) {
				cm.sendNext("오늘은 더 이상 #e#rVIP 멤버십 포인트#k#n를 구매할 수 없어요.");
				cm.dispose();
				return;
			}
			cm.getPlayer().updateOneInfo(501045, "mp", "1");
			cm.bigScriptProgressMessage("VIP 멤버십 포인트를 구매했습니다!");
			cm.getPlayer().updateOneInfo(501045, "sp", cm.getPlayer().getOneInfoQuestInteger(501045, "sp") + 1);
			var point = cm.getPlayer().getOneInfoQuestInteger(501045, "point") + 1;
			var point2 = cm.getPlayer().getOneInfoQuestInteger(501053, "point") + 1;
			cm.getPlayer().updateOneInfo(501045, "point", point);
			cm.getPlayer().updateOneInfo(501053, "point", point2);
			//cm.getPlayer().gainKillPoint(-20000);
			cm.getPlayer().gainStackEventGauge(0, -500, true);
			if (checkLevelUp(point, lv)) {
				cm.getPlayer().updateOneInfo(501045, "lv", lv + 1);
				cm.getPlayer().updateOneInfo(501045, "point", "0");
				var grade = "VIP ";
				if (lv == 1) {
					grade += "ELITE";
				} else if (lv == 2) {
					grade += "PREMIUM";
				} else if (lv == 3) {
					grade += "PRESTIGE";
				}
				cm.addPopupSay("#e#r" + grade + "#k#n 등급이 된 것을 축하하네!", "", 9062294, 3000);
			}	
			cm.dispose();		
		}
	}
    }
}

function checkLevelUp(point, lv) {
	if (lv == 1) {
		if (point >= 5) {
			return true;
		}
	} else if (lv == 2) {
		if (point >= 10) {
			return true;
		}
	} else if (lv == 3) {
		if (point >= 20) {
			return true;
		}
	}
	return false;
}