importPackage(java.lang);
importPackage(Packages.server);
importPackage(java.text);
importPackage(java.util);
importPackage(java.io);
importPackage(Packages.objects.item);
importPackage(Packages.objects.utils);
importPackage(Packages.network.models);

var nf = NumberFormat.getInstance();
	function start() { Status = -1; action(1, 0, 0); }

	function action(M, T, S) {

	if (M == -1) { cm.dispose(); } else {
		if (M == 0) {cm.dispose(); return; }
		if (M == 1) Status++; else Status--;

	if(Status == 0) {
	cm.sendYesNo("#i2436605# #b#z2436605##k을 사용하면 #r100~200개#k의 #b#z2711006##k를 랜덤으로 얻을 수 있습니다 사용하시겠어요?"
		+"")
	}

	else if(Status == 1) {
        var leftslot = cm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot();
        if (leftslot < 5) {
            cm.sendOk("소비 인벤토리 공간이 부족합니다.");
            cm.dispose();
            return;
        }
		if(!cm.haveItem(2436605)) {
		cm.sendOk("#i2436605# #b#z2436605##k이 있다고 우겨서 되는게 아니야. 봐, 네 인벤토리에는 없잖아?");
		cm.dispose();
		return;
		} else {
		cm.gainItem(2436605, -1);
		Rullet();
		cm.sendOk("#i2436605# #b#z2436605##k에서 #r"+ nf.format(W) +"개#k의 #b#z2711006##k을 획득했습니다. 지금 바로 인벤토리를 확인해 보세요!#b");
		cm.gainItem(2711006, W);
		cm.dispose();
		return;
		}
	}
	}
}



function Rullet() {
  W = Packages.objects.utils.Randomizer.rand(100, 200);
  var rand = Packages.objects.utils.Randomizer.rand(0, 100);
  if (rand < 1) {
  	W = 200;
  } else if (rand < 5) {
  	W = Packages.objects.utils.Randomizer.rand(160, 180);
  } else if (rand < 20) {
  	W = Packages.objects.utils.Randomizer.rand(140, 160);
  } else if (rand < 30) {
  	W = Packages.objects.utils.Randomizer.rand(120, 140);
  } else if (rand < 80) {
  	W = Packages.objects.utils.Randomizer.rand(100, 150);
  }
}
