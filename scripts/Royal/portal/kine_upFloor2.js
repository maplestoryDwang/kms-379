function enter(pi) {
         if (pi.getQuestStatus(5) == 2 && pi.getQuestStatus(4) == 2 && pi.getQuestStatus(6) == 2) {
	pi.warp(331002100);
	pi.playerMessage(5, "[알림] 2-1반으로 가세요!");
	pi.playerMessage(-1, "[알림] 2-1반으로 가세요!");
        } else{
	pi.playerMessage(5, "[알림] 선행 퀘스트를 진행 해주세요.");
	pi.playerMessage(-1, "[알림] 선행 퀘스트를 진행 해주세요.");
	}
}