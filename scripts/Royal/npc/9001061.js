importPackage(java.lang);

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 0 && sel == -1 && type == 6) {
	cm.dispose();
	return;
    }
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status--;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {	
	cm.sendPlayerToNpcS("드래곤의 알을 발견했다!");
	cm.showEffect("killing/clear");
    } else {
	cm.warp(993000601);
	cm.dispose();
    }
}