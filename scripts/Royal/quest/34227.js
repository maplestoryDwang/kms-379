var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	if (status == 0) {
		qm.sendNext("어서오세요, #ho#님! 요리를 도와주러 오셨나요?");
	} else if (status == 1) {
		qm.sendNextPrev("요즘 재료가 어느 정도 모인 것 같아서, 앞으로 #ho#님께 부탁드릴 일이 조금 줄어들었답니다.\r\n#e#r*<배고픈 무토>의 즉시 완료 가능 횟수가 1회 늘어납니다.#k#n");
	} else if (status == 2) {
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
