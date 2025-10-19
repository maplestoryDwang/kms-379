importPackage(Packages.constants);
importPackage(Packages.objects.item);

var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        var chat = "봉인의 수호자들을 만나기 위해선 #b고목나무 열쇠#k가 필요한건 알고있나? 필요한것이 있다면 나에게 말하게.#b\r\n\r\n";
        chat += "#L1# #i4033611# #z4033611#를 40만 메소에 구입하기\r\n";
        chat += "#L2# #i4033611# #z4033611# 10개를 360만 메소에 구입하기\r\n";
        //chat += "#L2# #i4310064# #z4310064#을 교환하기";
        cm.sendSimple(chat);

    } else if (status == 1) {
                   var leftslot = cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.ETC).getNumFreeSlot();
            if (leftslot < 3) {
                cm.sendOk("인벤토리 공간이 최소한 3칸은 필요합니다. 기타 탭의 공간을 3칸이상 비워주신 후 다시 열어주세요.");
                cm.dispose();
                return;
            }
    if (selection == 1) {
		if (cm.getMeso() >= 400000) {
			cm.gainItem(4033611,1);	
			cm.gainMeso(-400000);
			cm.sendOk("다음에도 또 찾아와 주게나");
			cm.dispose();
		} else {
			cm.sendOk("메소도 없는데 고목나무 열쇠를 살려고 한건가?");
			cm.dispose();
		}
    } else if (selection == 2) {
		if (cm.getMeso() >= 3600000) {
			cm.gainItem(4033611,10);	
			cm.gainMeso(-3600000);
			cm.sendOk("다음에도 또 찾아와 주게나");
			cm.dispose();
		} else {
			cm.sendOk("메소도 없는데 고목나무 열쇠를 살려고 한건가?");
			cm.dispose();
		}

	}
}
}