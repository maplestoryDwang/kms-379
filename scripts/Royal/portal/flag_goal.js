function enter(pi) {
	switch(pi.getPlayer().getMapId())
	{

		case 932200200:
		pi.teleport(5);
		break;

		default:
	        pi.teleport(3);
		break;
	}
}
