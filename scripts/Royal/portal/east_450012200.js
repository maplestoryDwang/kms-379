function enter(pi) {
    if (pi.getPlayer().getLevel() >= 255) {
    pi.warp(450012300,"sp");
	return true;
	} else {
	pi.playerMessage("포탈을 이용하려면 레벨이 255이상 이여야합니다.");
	}
}