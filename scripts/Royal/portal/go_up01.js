function enter(pi) {
    if (!pi.haveItem(4032743, 1)) {
        pi.getPlayer().dropMessage(-1,"카드키가 없어 탑승할 수 없습니다.");
        pi.getPlayer().dropMessage(5,"카드키가 없어 탑승할 수 없습니다.");
        return false;
    } else {
	if (pi.getPlayerCount(310030211) > 0) {
         pi.getPlayer().dropMessage(-1, "이미 누가 탑승중 이므로 잠시 기다려주세요.");
         pi.getPlayer().dropMessage(5, "이미 누가 탑승중 이므로 잠시 기다려주세요.");
	} else {
        pi.gainItem(4032743, -1);
        pi.TimeMoveMap(310030211, 304050000, 20);
        return true;
        }
    }
}