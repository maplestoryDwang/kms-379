importPackage(Packages.client);
importPackage(Packages.constants);
importPackage(Packages.server.maps);
importPackage(Packages.tools.packet);
importPackage(Packages.server);
importPackage(java.lang);
importPackage(java.util);
importPackage(java.math);

var outmap = 100000000;
var time = 0;
var temphp = 0;
var mob;
function init() {}

function setup(mapid) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    map = parseInt(mapid);
    while (em.getInstance("Normal_Dusk" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Normal_Dusk" + a);
    eim.setInstanceMap(map).resetFully();
    eim.setInstanceMap(map + 30).resetFully();
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
}

function spawnMonsterDusk(eim, instance, mobid, x, y) {
    var map = eim.getMapInstance(instance);
    var mob = em.getMonster(mobid);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(x, y));
}

function spawnMonster(eim) {
    var map = eim.getMapInstance(0);
    var mob = em.getMonster(8644650);
	var mob2 = em.getMonster(8644658);
    var tick = 0;
    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-45, -157));
	map.spawnMonsterOnGroundBelow(mob2, new java.awt.Point(-45, -157));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {}

function changedMap(eim, player, mapid) {
    stage = parseInt(eim.getProperty("stage"));
    if (mapid != 450009400 && mapid != 450009430) {
        eim.unregisterPlayer(player);
        eim.disposeIfPlayerBelow(0, 0);
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    stage = parseInt(eim.getProperty("stage"));
    player = eim.getPlayers().get(0);
    if (mobId == 8644650) {
        eim.setProperty("stage", "1");
        eim.schedule("WarptoNextStage", 5000);
    }
    return 1;
}

function WarptoNextStage(eim) {
    var stage = parseInt(eim.getProperty("stage"));
    var iter = eim.getPlayers().iterator();
    while (iter.hasNext()) {
        var player = iter.next();
        map = eim.getMapInstance(stage);
        player.changeMap(map.getId(), 0);
    }
    if (stage == 1) {
        map.broadcastMessage(CField.BlackLabel("#fn나눔고딕 ExtraBold##fs22##e#r[노말 더스크]#k#fn나눔고딕 ExtraBold# 보상맵", 100, 3000, 3, -100, 50, 1, 4));
        spawnMonsterDusk(eim, stage, 8950116, -2672, -489); // 보상상자
        eim.restartEventTimer(300000);
    }
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    eim.disposeIfPlayerBelow(0, 0);
}

function end(eim) {
    var player = eim.getPlayers().get(0);
    eim.disposeIfPlayerBelow(100, outmap2);
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