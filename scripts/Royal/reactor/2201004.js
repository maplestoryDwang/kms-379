function act() {
    rm.mapMessage(5, "차원의 균열이 <차원의 균열 조각>으로 메꾸어졌습니다.");
    rm.changeMusic("Bgm09/TimeAttack");
    if (rm.getMap().getId() == 220080300) {
        rm.spawnMonster(8500020, 0, 179);
    } else if (rm.getMap().getId() == 220080200) {
        rm.spawnMonster(8500010, 0, 179);
    } else if (rm.getMap().getId() == 220080100) {
        rm.spawnMonster(8500000, 0, 179);
    }
    rm.getMap().setReactorState("crack1", 1);
    rm.getMap().setReactorState("crack2", 1);
    rm.getMap().setReactorState("crack3", 1);
    rm.getMap().setReactorState("crack4", 1);
    rm.getMap().setReactorState("crack5", 1);
    rm.getMap().setReactorState("crack6", 1);
}