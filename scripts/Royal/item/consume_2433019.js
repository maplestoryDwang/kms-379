importPackage(java.lang);
importPackage(java.text);
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
	cm.sendYesNo("임팩트 메소 럭키백을 열어보시겠어요? #b100 메소에서 100억 메소#k 사이의 금액을 획득할 수 있어요. 당신의 운을 한 번 시험해보겠나요?"
		+"")
	}

	else if(Status == 1) {

		if(!cm.haveItem(2433019)) {
		cm.sendOk("#i2433019# #b#z2433019##k이 있다고 우겨서 되는게 아니야. 봐, 네 인벤토리에는 없잖아?");
		cm.dispose();
		} else {
  W = Packages.objects.utils.Randomizer.rand(10000000, 100000000);
  var rand = Packages.objects.utils.Randomizer.rand(0, 1000);
  if (rand < 5) {
  	W = 10000000000;
  } else if (rand < 20) {
  	W = Packages.objects.utils.Randomizer.rand(5000000000, 10000000000);
  } else if (rand < 40) {
  	W = Packages.objects.utils.Randomizer.rand(4000000000, 7000000000);
  } else if (rand < 60) {
  	W = Packages.objects.utils.Randomizer.rand(3000000000, 6000000000);
  } else if (rand < 80) {
  	W = Packages.objects.utils.Randomizer.rand(2000000000, 5000000000);
  } else if (rand < 100) {
  	W = Packages.objects.utils.Randomizer.rand(1000000000, 3000000000);
  } else if (rand < 150) {
  	W = Packages.objects.utils.Randomizer.rand(100000000, 1000000000);
  } else if (rand < 250) {
  	W = Packages.objects.utils.Randomizer.rand(100000000, 700000000);
  } else if (rand < 350) {
  	W = Packages.objects.utils.Randomizer.rand(100000000, 500000000);
  }
		if (W < 0) {
		cm.sendOk("에러 발생 다시 시도해주세요.");
		cm.dispose();
		return;
		}
		if (W == 10000000000) {
		cm.worldGMMessage(8, "[잭팟] " + cm.getPlayer().getName() + "님이 임팩트 메소 럭키백에서 100억 메소를 얻었습니다.");
		}

		cm.sendOk("축하드립니다. 임팩트 메소 럭키백에서 "+ nf.format(W) +"메소를 획득했습니다. 지금 바로 인벤토리를 확인해 보세요!#b\r\n\r\n "+ nf.format(W) +" 메소\r\n");
		cm.gainItem(2433019, -1);
		cm.gainMeso(W);
		cm.dispose();
		}
	}
	}
}
