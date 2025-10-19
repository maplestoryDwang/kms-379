function init() {
}

function cancelSchedule() {
}

function setup(eim) {
    var eim = em.newInstance("MonsterParkExtreme");
    eim.startEventTimer(600000);
    eim.timeOut(600000,eim);
    eim.setProperty("MonsterParkExtreme","0");
    return eim;
}

function playerEntry(eim,player) {
    var rudy = eim.getMapFactory().getMap(eim.getProperty("StartMap"));
    player.changeMap(rudy,rudy.getPortal("sp"));
}

function changedMap(eim,player,mapid) {
}

function scheduledTimeout(eim) {
    var rudy = em.getChannelServer().getMapFactory().getMap(eim.getProperty("LeaveMap"));
    eim.stopEventTimer();
    eim.getPlayers().iterator().next().changeMap(rudy,rudy.getPortal(0));
    eim.dispose();
}

function monsterValue(eim,mobid) {
    return 1;
}

function allMonstersDead(eim) {
    eim.setProperty("MonsterParkExtreme","1");
}


function playerDisconnected(eim,player) {
    return -1;
}

function leftParty(eim,player) {
    eim.disposeIfPlayerBelow(6,eim.getProperty("LeaveMap"));
}

function disbandParty(eim) {
    eim.disposeIfPlayerBelow(6,eim.getProperty("LeaveMap"));
}

function playerDead(eim,player) {
    eim.unregisterPlayer(player);
    eim.dispose();
}

function playerExit(eim,player) {
    eim.unregisterPlayer(player);
    eim.dispose();
}