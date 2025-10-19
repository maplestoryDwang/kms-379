function enter(pi) {
    if (pi.getQuestStatus(2) == 2) {
	pi.warp(331002000);
	pi.playerMessage(5, "[알림] 주변 NPC들에게 말을 걸며 학교 조사를 하세요.");
	pi.playerMessage(-1, "[알림] 주변 NPC들에게 말을 걸며 학교 조사를 하세요.");
	} else {
	pi.playerMessage(5, "[알림] 선행 퀘스트를 진행 해주세요.");
	pi.playerMessage(-1, "[알림] 선행 퀘스트를 진행 해주세요.");
}
}