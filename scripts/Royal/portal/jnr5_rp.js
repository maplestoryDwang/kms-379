
function enter(pi) {
    var em = pi.getEventManager("Juliet");
    var name = pi.getPortal().getName();
    if (em != null && em.getProperty("stage6_" + (((pi.getMapId() % 10) | 0) - 1) + "_" + (name.substring(2, 3)) + "_" + (name.substring(3, 4)) + "").equals("1")) {
	pi.playPortalSE();
	pi.getMap().changeEnvironment("an" + pi.getPortal().getName().substring(2, 4), 2);
        pi.warp(-1, "np0" + name.substring(2, 3));
	if (name.substring(2, 3) != null && name.substring(2, 3).equals("4")) {
	    pi.warpS(pi.getMapId(), 5);
	}
    } else {
        pi.playPortalSE();
	pi.warp(-1, "npFail");
    }
}