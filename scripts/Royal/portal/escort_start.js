/*

ep1.하얀마법사
제작 :: usf - 와쮜(uuff1234)
		nate - uuff1236@nate.com

*/

function enter(pi) {
	if (pi.isQuestActive(300)) {
    if (pi.getQuestStatus(300) == 1) {
		pi.warp(302010100,0);
		pi.getPlayer().dropMessage(-1,"[알림] 오른쪽 포탈로 이동하세요.");
		pi.getPlayer().dropMessage(5,"[알림] 오른쪽 포탈로 이동하세요.");
	} else {
		pi.getPlayer().dropMessage(-1,"[알림] 왼쪽의 핫사르에게 대화를 거세요.");
		pi.getPlayer().dropMessage(5,"[알림] 왼쪽의 핫사르에게 대화를 거세요.");
	//pi.forceCompleteQuest(3164);
	//pi.playerMessage("퀘스트 완료");
		}
    }
}