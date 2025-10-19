var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
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
	cm.sendYesNo("#fs11##fc0xFF6600CC#전투를 종료하고 마을로 돌아가시겠습니까?");
    } else if (status == 1) {
	cm.warp(272020110, 0);
	cm.dispose();
    }
}