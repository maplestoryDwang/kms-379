/*
Zakum Altar - Summons Zakum.
*/

function act() {
	if (rm.getMap().getId() == 280030000) {
    rm.getMap().spawnChaosZakum(-10, 86);
	if (!rm.getPlayer().isGM()) {
		rm.getMap().startSpeedRun();
	}
	}
}
