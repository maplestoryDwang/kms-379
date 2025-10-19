/*
Zakum Altar - Summons Zakum.
*/

function act() {
	if (rm.getMap().getId() == 280030100) {
    rm.getMap().spawnZakum(-10, 86);
    //rm.dropMessage(5,"원석의 힘으로 자쿰이 소환됩니다.");
    if (!rm.getPlayer().isGM()) {
        rm.getMap().startSpeedRun();
    }
	}
}
