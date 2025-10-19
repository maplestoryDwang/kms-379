importPackage(Packages.client);
importPackage(Packages.constants);
importPackage(Packages.server.maps);
importPackage(Packages.tools.packet);
importPackage(Packages.server);
importPackage(java.lang);
importPackage(java.util);

var outmap = 100000000;
var time = 0;

function init() {
}

function setup(mapid) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    map = parseInt(mapid);
    while (em.getInstance("Normal_Dorothy" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Normal_Dorothy" + a);
    eim.setInstanceMap(map).resetFully();
    return eim;
}

function playerEntry(eim, player) {
    eim.startEventTimer(900000);
    var map = eim.getMapInstance(0);
    player.setDeathCount(3);
    player.changeMap(map, map.getPortal(0));
    if (player.getParty().getLeader().getId() == player.getId()) {
        spawnMonster(eim, 0, 9309207, 25, 150);
    }
}

function spawnMonster(eim, instance, mobid, x, y) {
    var map = eim.getMapInstance(instance);
    var mob = em.getMonster(mobid);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(x, y));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid != 992050000) {
        player.setDeathCount(0);
        eim.unregisterPlayer(player);
        player.setDeathCount(0);
        eim.disposeIfPlayerBelow(0, 0);
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    eim.disposeIfPlayerBelow(0, 0);
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, outmap);
}


function clearPQ(eim) {
    end(eim);
}


function disposeAll(eim) {
    var iter = eim.getPlayers().iterator();
    while (iter.hasNext()) {
        var player = iter.next();
        eim.unregisterPlayer(player);
        player.setDeathCount(0);
        player.changeMap(outmap, 0);
    }
    end(eim);
}

function allMonstersDead(eim) {
    //after ravana is dead nothing special should really happen
}

function leftParty(eim, player) {
    disposeAll(eim);
}

function disbandParty(eim) {
    disposeAll(eim);
}

function playerDead(eim, player) {}

function cancelSchedule() {}