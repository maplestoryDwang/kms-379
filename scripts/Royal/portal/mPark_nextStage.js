/*
 * 퓨어온라인 소스 스크립트 입니다.
 * 
 * 포탈위치 : 
 * 포탈설명 : 
 * 
 * 제작 : 주크블랙
 * 
 */

function enter(pi) {
	if (pi.getClient().getChannelServer().getMapFactory().getMap(pi.getPlayer().getMapId()).getNumMonsters() > 0) {
		pi.getPlayer().dropMessage(5, "모든 몬스터를 처치하셔야 다음 맵으로 이동하실 수 있습니다.");
	} else {
		pi.resetMap(pi.getPlayer().getMapId() + 100, true);
		pi.warp(pi.getPlayer().getMapId() + 100, 0);
	}
}