importPackage(Packages.client);
importPackage(Packages.constants);
importPackage(Packages.server.maps);
importPackage(Packages.tools.packet);
importPackage(Packages.server);
importPackage(java.lang);
importPackage(java.util);

var outmap = 100000000;
var time = 0;

function init() {}

function setup(mapid) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    map = parseInt(mapid);
    while (em.getInstance("Teng" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Teng" + a);
    eim.setInstanceMap(map).resetFully();
    return eim;
}

function playerEntry(eim, player) {
    eim.startEventTimer(1800000);
    eim.setProperty("stage", "0");
    var map = eim.getMapInstance(0);
    player.setDeathCount(5);
    player.changeMap(map, map.getPortal(0));
    if (player.getParty().getLeader().getId() == player.getId()) {
        spawnMonster(eim);
    }
	map.broadcastMessage(CField.BlackLabel("#fn나눔고딕 ExtraBold##fs22##e#r[보스]#k#fn나눔고딕 ExtraBold# 텐구의 까마귀 시험", 100, 3000, 3, -100, 50, 1, 4));
}

function spawnMonsterTeng(eim, instance, mobid, x, y) {
    var map = eim.getMapInstance(instance);
    var mob = em.getMonster(mobid);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(x, y));
}

function spawnMonster(eim) {
    var map = eim.getMapInstance(0);
    var mob = em.getMonster(9400080);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(479, -28));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid != 813000000) {
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
    stage = parseInt(eim.getProperty("stage"));
    player = eim.getPlayers().get(0);
    if (mobId == 9400080) {
        spawnMonsterTeng(eim, stage, 9500365, 453, -29); // 보상상자
        eim.restartEventTimer(300000);
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