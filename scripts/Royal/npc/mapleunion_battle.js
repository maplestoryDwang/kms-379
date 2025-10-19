importPackage(Packages.objects.utils);
importPackage(Packages.scripting);

var status = -1;
var enter = "\r\n";

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
        var msg = "#r#e거대한 드래곤과의 결투#n#k가 용사님을 기다리고 있습니다!\r\n#b#e유니온 레이드에 입장#n#k 하시겠습니까?";
        cm.sendYesNo(msg);
    } else if (status == 1) {
	var a = Packages.objects.utils.Randomizer.rand(0, 1);
	var b = a == 1 ? 100 : 0;
	
	var find = false;
	var mapID = 0;
	for (var i = 0; i < 99; ++i) {
		mapID = 921172000 + b + i;
		
		if (cm.getPlayerCount(mapID) > 0) {
			continue;
		}
		find = true;
            	cm.resetMap(mapID);
		break;
	}
	if (!find) {
		cm.sendNext("현재 유니온 전투를 이용하는 용사가 많아 입장이 불가능합니다. 다른 채널을 이용하시거나 잠시 후 다시 시도해주시기 바랍니다.");
		cm.dispose();
		return;
	}

        	var em = cm.getEventManager("unionRaid");
        	var eim = em.readyInstance();
	if (eim == null) {
		cm.sendNext("알 수 없는 오류로 인하여 진행할 수 없습니다.");
		dispose();
		return;
	}
	eim.setProperty("MapID", mapID);
        	eim.registerPlayer(cm.getPlayer());
       	cm.dispose();
    }
}