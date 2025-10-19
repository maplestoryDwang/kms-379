function act() {
    rm.spawnMonster(8820008);
	if (!rm.getPlayer().isGM()) {
		rm.getMap().startSpeedRun();
	}
}