function enter(pi) {
    pi.warp(302010400,0);
	pi.forceCompleteQuest(300);
    pi.getPlayer().dropMessage(-1, "도착했다... 핫사르에게 말을 걸어보자.");
    pi.getPlayer().dropMessage(5, "도착했다... 핫사르에게 말을 걸어보자.");
    return true;
}