/*
 * 아카이럼
 */
function enter(pi) {
    var map = pi.getPlayer().getMap();
    if (pi.getMonsterCount(map.getId()) <= 0) {
        pi.warp(map.getId() - 100);
    } else {
        pi.getPlayer().message(5, "이동 할 수 없습니다.");
    }
    return false;
}
