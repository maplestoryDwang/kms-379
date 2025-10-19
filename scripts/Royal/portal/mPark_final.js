var data;
var day;
var item = -1;
function enter(pi) {
	if (pi.getClient().getChannelServer().getMapFactory().getMap(pi.getPlayer().getMapId()).getNumMonsters() > 0) {
	    pi.getPlayer().dropMessage(5, "모든 몬스터를 처치하셔야 클리어가 가능합니다.");
	} else {
		if (pi.getPlayer().getQuestStatus(100573) == 1) {
			pi.getPlayer().updateOneInfo(100573, "monsterPark", String(pi.getPlayer().getOneInfoQuestInteger(100573, "monsterPark") + 1));
		}
	    pi.openNpc(9071000);
	}
}