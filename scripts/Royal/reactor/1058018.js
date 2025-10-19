function destroy() {
    rm.spawnMonster(8920102, 37, 135);
}

function act() {
    if (rm.getMap().getId() == 105200310) {
        state = rm.getReactor().getState();
        if (state == 1) {
            rm.startMapEffect("어머, 귀여운 손님들이 찾아왔네.", 5120099, 3);
        } else if (state == 2) {
            rm.startMapEffect("무엄하다! 감히 대전을 함부로 드나들다니! ", 5120100, 3);
        } else if (state == 3) {
            rm.startMapEffect("킥킥, 여기가 죽을 자리인 줄도 모르고 왔구나.", 5120101, 3);
        } else if (state == 4) {
            rm.startMapEffect("흑흑, 당신의 죽음을 미리 슬퍼해드리지요.", 5120102, 5);
        }
    }
}
