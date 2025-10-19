importPackage(java.lang);
importPackage(java.text);
importPackage(Packages.objects.wz.provider);
importPackage(Packages.objects.users);
importPackage(Packages.objects.item);
importPackage(Packages.objects.utils);
importPackage(Packages.network.models);

var status = -1;
var code = 2434290;


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
	cm.gainItem(code, -1);
	cm.getPlayer().addHonorExp(10000);
	cm.getPlayer().dropMessage(5, "10000 명성치를 획득했습니다.");
	cm.dispose();
	}
}