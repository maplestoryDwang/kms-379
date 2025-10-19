var status = -1;
var f = -1;
var reward = 0;
var v = "";

function start(flag) {
    status = -1;
    v = "";
    f = flag;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
	if (f == 0) {
		reward = 2630402;
		v = "reward0";
	} else if (f == 256) {
		reward = 2631878;
		v = "reward1";
	} else if (f == 512) {
		reward = 2439660;
		v = "reward2";
	}
	if (reward == 0) {
		cm.sendNext("알 수 없는 오류가 발생했습니다.");
		cm.dispose();
		return;
	}
	cm.sendYesNo("#b#i" + reward + "# #z" + reward + "##k을(를) 수령하시겠습니까?");
    } else if (status == 1) {
	if (!cm.canHold(reward, 1)) {
		cm.sendNext("인벤토리 공간을 확보하고 다시 시도해주시기 바랍니다.");
		cm.dispose();
		return;
	}
	if (cm.getPlayer().getOneInfoQuestInteger(501045, v) > 0) {
		cm.sendNext("이미 해당 등급에 보상을 수령받았습니다.");
		cm.dispose();
		return;
	}
	cm.getPlayer().updateOneInfo(501045, v, "1");
	cm.gainItem(reward, 1);
	cm.sendNext("#b#i" + reward + "# #z" + reward + "##k을(를) 획득했습니다.");
	cm.dispose();
    }
}