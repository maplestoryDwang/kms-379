function enter(pi) {
    var random = java.lang.Math.random() * 10;
    var x = pi.getPlayer().getPosition().x;
    if (random <= 5) {
	pi.showEffect("monsterPark/clear");
	if (x < 850) {
		pi.instantMapWarp(5);
	} else {
		pi.instantMapWarp(6);
	}
	pi.getPlayer().updateOneInfo(15142, "Stage", "3");
    } else {
	pi.showEffect("killing/fail");
 	pi.openNpc(9001060);
    }
}