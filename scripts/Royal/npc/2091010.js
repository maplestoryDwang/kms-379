importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);
importPackage(java.awt);


var status = -1;

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
	cm.showDojangRanking();
	cm.getPlayer().dropMessage(5, "무릉도장 기록이 순위표에 반영되기까지 시간이 걸릴 수 있습니다. 정산 포인트는 매주 랭킹 정산 이후 전체랭킹 기준으로 지급됩니다.");
	cm.dispose();
        }
    }
}