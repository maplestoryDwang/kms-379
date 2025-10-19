function enter(pi) {
    if (!pi.haveItem(4031217, 1)) {
         pi.getPlayer().dropMessage(-1, "통로를 열 열쇠가 없어 지나갈 수 없습니다.");
         pi.getPlayer().dropMessage(5, "통로를 열 열쇠가 없어 지나갈 수 없습니다.");
        return false;
    } else {
        pi.gainItem(4031217, -1);
        pi.warp(304010300,0);
        pi.getPlayer().dropMessage(-1, "위험한 곳인 것 같군.. 조심히 앞으로 가보자..");
        pi.getPlayer().dropMessage(5, "위험한 곳인 것 같군.. 조심히 앞으로 가보자..");
        return true;
    }
}