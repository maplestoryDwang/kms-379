var status;
importPackage(java.lang);
importPackage(java.text);
importPackage(java.util);
importPackage(java.io);
importPackage(Packages.server);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.server.items);
importPackage(Packages.scripting);
importPackage(Packages.constants);
importPackage(Packages.objects.wz.provider);
importPackage(Packages.objects.users);
importPackage(Packages.objects.item);
importPackage(Packages.objects.utils);
importPackage(Packages.network.models);

var AUTsymbolListCode = [1713000, 1713001, 1713002, 1713003, 1713004];

var itemcode = 2633591;

function start() {
	status = -1;
	action(1, 1, 0);
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
		var v0 = "#fs11##d#e획득하고 싶은 어센틱심볼을 선택해 주세요.#n#k\r\n\r\n"
		for (i = 0; i < AUTsymbolListCode.length; i++) {
			v0 += "#L" + i + "# #r#i" + AUTsymbolListCode[i] + "# #z" + AUTsymbolListCode[i] + "##k#l\r\n";
		}
		cm.sendOk(v0);
	} else if (status == 1) {
		sel = selection;
		var v0 = "#fs11#정말로 선택하신 아이템으로 교환하실 건가요?\r\n\r\n"
		v0 += "#r#i" + AUTsymbolListCode[sel] + "# #z" + AUTsymbolListCode[sel] + "##k"
		cm.sendYesNo(v0);
	} else if (status == 2) {
		var invenequip = cm.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot();
		if (invenequip < 2) {
			cm.sendOk("#fs11#인벤토리 공간이 부족합니다.");
			cm.dispose();
			return;
		}
		if (!cm.haveItem(itemcode, 1)) {
			cm.sendOk("#r#i" + itemcode + "# #z" + itemcode + "# 1개#k가 필요합니다.");
			cm.dispose();
			return;
		}
		cm.gainItem(itemcode, -1);
		jobId = cm.getPlayer().getJob();
		AUTSymbolList = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(AUTsymbolListCode[sel]);
		AUTSymbolList.setArcLevel(11);
		AUTSymbolList.setArc(110);
		if (jobId >= 100 && jobId < 200 || jobId == 512 || jobId == 1512 || jobId == 2512 || jobId >= 1100 && jobId < 1200 || GameConstants.isAran(jobId) || GameConstants.isBlaster(jobId) || GameConstants.isDemonSlayer(jobId) || GameConstants.isMichael(jobId) || GameConstants.isKaiser(jobId) || GameConstants.isZero(jobId) || GameConstants.isArk(jobId) || GameConstants.isAdele(jobId)) {
			AUTSymbolList.setStr(2500);
		} else if (jobId >= 200 && jobId < 300 || GameConstants.isFlameWizard(jobId) || GameConstants.isEvan(jobId) || GameConstants.isLuminous(jobId) || jobId >= 3200 && jobId < 3300 || GameConstants.isKinesis(jobId) || GameConstants.isIllium(jobId) || GameConstants.isLara(jobId)) {
			AUTSymbolList.setInt(2500);
		} else if (GameConstants.isKain(jobId) || jobId >= 300 && jobId < 400 || jobId == 522 || jobId == 532 || GameConstants.isMechanic(jobId) || GameConstants.isAngelicBuster(jobId) || jobId >= 1300 && jobId < 1400 || GameConstants.isMercedes(jobId) || jobId >= 3300 && jobId < 3400) {
			AUTSymbolList.setDex(2500);
		} else if (jobId >= 400 && jobId < 500 || jobId >= 1400 && jobId < 1500 || GameConstants.isPhantom(jobId) || GameConstants.isKadena(jobId) || GameConstants.isKhali(jobId) || GameConstants.isHoyoung(jobId)) {
			AUTSymbolList.setLuk(2500);
		} else if (GameConstants.isDemonAvenger(jobId)) {
			AUTSymbolList.setHp(5250);
		} else if (GameConstants.isXenon(jobId)) {
			AUTSymbolList.setStr(1200);
			AUTSymbolList.setDex(1200);
			AUTSymbolList.setLuk(1200);
		}
		Packages.objects.item.MapleInventoryManipulator.addbyItem(cm.getClient(), AUTSymbolList, false);
		cm.sendOk("지급되었습니다.");
		cm.dispose();
	}
}

