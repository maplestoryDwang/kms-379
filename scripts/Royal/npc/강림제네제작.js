var status = -1;

var nitem = [
    [4001878, 200],//사냥물떡
    [4001879, 200],//보스물떡
    [4310308, 3000],//네오코어
    [4031227, 3000],//찬빛결
    [4310266, 3000],//승급코인
    [4001715, 3000],//1억화폐
    [4001894, 30],
    [2591427, 3],
    [2591572, 3],
    [2591590, 3],
    [2591676, 3],
    [2591659, 3]
];
var allstat = 1000;
var 말 = "#fs11#"

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

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
        말 += "#fc0xFFB677FF##e[강림] 제네시스 무기 제작#n  \r\n\r\n"
        말 += "#fc0xFF990033#제네시스 무기 #fc0xFF191919#제작을 위해선 아래와 같은 재료를 가져오세요.\r\n"
        for (i = 0; i < nitem.length; i++) {
            말 += "#i" + nitem[i][0] + "#  #z" + nitem[i][0] + "# " + nitem[i][1] + "개\r\n"
        }
        말 += "\r\n";
        말 += "#fc0xFF990033#제네시스 무기#fc0xFF191919#는 #fc0xFFF15F5F#스타포스 강화불가#fc0xFF191919#입니다.\r\n\r\n"
        말 += "#fc0xFF990033#제네시스 무기#fc0xFF191919#는 #r매우 강력한 옵션#k이 부여됩니다.\r\n\r\n"
        말 += "#fc0xFF990033#제네시스 무기#fc0xFF191919#는 #b잠재 6줄 모두 선택#k할 수 있습니다.\r\n\r\n"
        말 += "#fc0xFF990033#제네시스 무기#fc0xFF191919#를 제작하려면 예 싫다면 아니오를 선택하세요.\r\n\r\n"
        cm.sendYesNo(말);
    } else if (status == 1) {
        말 = "";
        if (checkitem()) {
            말 += "#fs11##z2439959#을 제작하기 위한 재료가 부족하네요.\r\n\r\n"
            말 += "아래와 같은 아이템을 더 모아오시지요.\r\n\r\n"
            needitem();
            cm.sendOk(말);
            cm.dispose();
            return;
        }
        for (i = 0; i < nitem.length; i++) {
            cm.gainItem(nitem[i][0], -nitem[i][1]);
        }
        cm.gainItem(2439959, 1);
        cm.worldGMMessage(21, "[제네시스] " + cm.getPlayer().getName() + "님께서 [제네시스 무기]를 제작하였습니다! 축하해주세요!");
        cm.sendOk("#fs11##i2439959##z2439959# 를 제작하였습니다!");
        cm.dispose();
    }
}

function checkitem() {
    for (i = 0; i < nitem.length; i++) {
        if (!cm.haveItem(nitem[i][0], nitem[i][1])) {
            return true;
        }
    }
    return false;
}

function needitem() {
    for (i = 0; i < nitem.length; i++) {
        if (!cm.haveItem(nitem[i][0], nitem[i][1])) {
            말 += "#i" + nitem[i][0] + "#  #z" + nitem[i][0] + "# " + (nitem[i][1] - cm.itemQuantity(nitem[i][0])) + "개\r\n";
        }
    }
}