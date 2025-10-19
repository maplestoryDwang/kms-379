importPackage(java.sql);
importPackage(java.lang);
importPackage(Packages.objects.utils);
importPackage(Packages.objects.item);

var status = -1;
var limit = 30; // 일일 30개 제한
var itemList = [2049153, 2049004, 2430441, 2048716, 2048717, 5680148, 5520001, 2436605, 2049100, 2432577, 2432576, 2432575, 2432578, 2432579];
var prop = [15, 15, 15, 3, 2, 2, 2, 30, 60, 7, 7, 7, 7, 7];

function start() {
    status = -1;
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
	cm.sendYesNo("희귀한 보물 상자를 개봉 하시겠습니까? 상자는 #b하루에 " + limit + "회#k만 개봉할 수 있습니다.");
    } else if (status == 1) {
    var invenuse = cm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot();
    var invencash = cm.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot();
      if (invenuse < 2 && invencash < 2) {
        cm.sendOk("인벤토리 공간을 비워주세요.");
        cm.dispose();
        return;
      }
      
	if (cm.getPlayer().CountCheck("elite_boss_reward", limit)) {
		var text = "상자에서 아래의 아이템이 나왔습니다.\r\n#b금일은 " + (limit - cm.getPlayer().GetCount("elite_boss_reward")) + "회#k 더 개봉할 수 있습니다.\r\n\r\n";
		var find = false;
		// 아이템 지급
		for (var i = 0; i < itemList.length; ++i) {
			var itemID = itemList[i];
			if (Packages.objects.utils.Randomizer.nextInt(100) <= prop[i]) {
				text += "#b#i" + itemID + "# #z" + itemID + "#\r\n";
				cm.gainItem(itemID, 1);
				find = true;
			}
		}
		if (!find) {
			text = "상자에서 아무런 아이템도 획득하지 못했습니다.";
		}
		cm.gainItem(2433834, -1);
		cm.sendNext(text);
		cm.dispose();
		cm.getPlayer().CountAdd("elite_boss_reward");
	} else {
		cm.sendNext("금일은 더 이상 개봉할 수 없습니다.");
		cm.dispose();
	}
    }
}