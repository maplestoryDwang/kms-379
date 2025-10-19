function enter(pi) {
    var mapid = pi.getMapId();

    switch (mapid) {
	case 224000000:
	pi.warp(956100000,"gPark_Portal");
	break;
	case 956100000:
	pi.warp(224000000,"toGhostPark");
	break;

	default:
	    return;
    }
}