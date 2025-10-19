 status;
importPackage(Packages.server);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.server.items);

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
	if (status == 0) {
	cm.sendYesNo("#fs11##i5062005##z5062005# #b0개 ~ 3개#k\r\n#i5062503##z5062503# #b0개 ~ 3개#k\r\n가 랜덤으로 지급됩니다 상자를 여시겠습니까?");
	} else if (status == 1) {
a = Packages.objects.utils.Randomizer.rand(0,3);
b = Packages.objects.utils.Randomizer.rand(0,3);
cm.sendOk("#fs11##i5062005##z5062005# #b" + a + "개#k\r\n#i5062503##z5062503# #b" + b + "개#k\r\n위 아이템들이 지급되었습니다.");
	cm.gainItem(2430026, -1);
	cm.gainItem(5062005, a);
	cm.gainItem(5062503, b);
	cm.dispose();
	}
}
}