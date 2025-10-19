function start() {
    status = -1;
    action(1, 0, 0);
    cm.sendSimple("잠깐, 어느 자쿰의 제단으로 이동하시겠소?\r\n#b" +
        "#L211042400#노말 자쿰#l\r\n" +
        "#L211042401#카오스 자쿰#l\r\n");
}

function action(M, T, S) {
    if (M != 1) {
        cm.dispose();
        return;
    }

    if (M == 1)
        status++;
    else
        status--;

    if (status == 1) {
        S0 = S;
        needItem = (S0 == 211042402) ? 4001796 : 4001017;
        getZakum = (S0 == 211042401) ? "카오스 " : "노말 ";
        if (!cm.haveItem(needItem)) {
            cm.getPlayer().dropMessage(5, getZakum + "자쿰에게 바칠 제물이 없어 이동할 수 없습니다.");
            cm.dispose();
            return;
        }
        cm.dispose();
        cm.warp(S0);
    }
}