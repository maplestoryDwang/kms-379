importPackage(Packages.constants);
importPackage(Packages.object);
importPackage(Packages.network.models);
importPackage(java.lang);
importPackage(java.util);

var outmap = 100000000;
var time = 0;

function init() {}

function setup(mapid) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    map = parseInt(mapid);
    while (em.getInstance("BossAMH" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("BossAMH" + a);
    eim.setInstanceMap(map).resetFully();
    return eim;
}

function playerEntry(eim, player) {
    eim.startEventTimer(1800000);
    var map = eim.getMapInstance(0);
    player.setDeathCount(5);
    player.changeMap(map, map.getPortal(0));
    if (player.getParty().getLeader().getId() == player.getId()) {
        spawnMonster(eim);
    }
	map.broadcastMessage(CField.BlackLabel("#fn나눔고딕 ExtraBold##fs22##e#r[보스]#k#fn나눔고딕 ExtraBold# 아케치 미츠히데", 100, 3000, 3, -100, 50, 1, 4));
}

function spawnMonster(eim) {
    var map = eim.getMapInstance(0);
    var mob = em.getMonster(9601510);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(479, -28));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid != 874004002) {
        player.setDeathCount(0);
        eim.unregisterPlayer(player);
        eim.disposeIfPlayerBelow(0, 0);
        eim.restartEventTimer(0);
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    player = eim.getPlayers().get(0);


    if (mobId == 9601510) {
	var map = eim.getMapInstance(0);
	var mob = em.getMonster(9601511);
	map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(479, -28));
    }
    if (mobId == 9601511) {
        eim.restartEventTimer(60000);
    }
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