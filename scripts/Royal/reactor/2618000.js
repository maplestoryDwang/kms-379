
function act() {
    if (rm.getReactor().getState() >= 7) {
	rm.mapMessage(6, "한 개의 비커를 가득 채웠습니다.");
	var em = rm.getEventManager(rm.getMapId() == 926100100 ? "Romeo" : "Juliet");
	if (em != null && rm.getReactor().getState() >= 7) {
	    var react = rm.getMap().getReactorByName(rm.getMapId() == 926100100 ? "rnj2_door" : "jnr2_door");
	    em.setProperty("stage3", parseInt(em.getProperty("stage3")) + 1);
	    react.forceHitReactor(react.getState() + 1, rm.getPlayer().getId());
            if (parseInt(em.getProperty("stage3")) == 3) {
                //clear
                rm.gainPartyExpPQ(20000, "rnj", 70);
                rm.getMap().setSpawns(false);
                rm.getMap().killAllMonsters(true);
                rm.clearEffect();
            }
	}
    }
}