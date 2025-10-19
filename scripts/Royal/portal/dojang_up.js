function enter(pi) {
	if (pi.getPlayer().getMapId() == 925070000) {
		pi.warp(pi.getPlayer().getMapId() + 100);
		return;
	}
	if (!pi.getPlayer().getMap().checkDojangClear()) {
		pi.getPlayer().dropMessage(5,"아직 문이 열리지 않았습니다.");
	} else {
		pi.getPlayer().addMulungPoint();

		pi.getPlayer().getMap().checkDojangStopClockTime();
		pi.getPlayer().getMap().updateDojangRanking();

		if (pi.getPlayer().getMapId() == 925078000) {
			pi.warp(925020003);
		} else {
			pi.warp(pi.getPlayer().getMapId() + 100);
		}
	}
}