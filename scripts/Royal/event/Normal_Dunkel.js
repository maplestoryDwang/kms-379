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
var posx = [-650, -560, -470, -380, -290, -200, -110, -20, 70, 160, 250, 340, 430, 520, 610];
var addposx = [-650, -560, -470, 470, 560, 650];
function init() {}

function setup(mapid) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    map = parseInt(mapid);
    while (em.getInstance("Normal_Dunkel" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Normal_Dunkel" + a);
    eim.setInstanceMap(map).resetFully();
    eim.setInstanceMap(map + 40).resetFully();
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

function spawnMonsterDunkel(eim, instance, mobid, x, y) {
    var map = eim.getMapInstance(instance);
    var mob = em.getMonster(mobid);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(x, y));
}

function spawnMonster(eim) {
    var map = eim.getMapInstance(0);
    var mob = em.getMonster(8645009);
    var tick = 0;
    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(0, 20));
    var schedule = Timer.MapTimer.getInstance().register(function() {
	try {
        if (eim.getPlayers().get(0).getMapId() != 450012210) {
	eim.getPlayers().get(0).dropMessage(6, "cancel");
            schedule.cancel(true);
        } else {
            if (tick == 0) {
                for (i = 0; i < posx.length; i++) {
                    mon = em.getMonster(8645003);
                    eim.registerMonster(mon);
                    map.spawnMonsterOnGroundBelow(mon, new java.awt.Point(posx[i], 28));
                }
            } else if (tick % 5 == 0) {
                if (tick / 5 <= 5) {
                    rd = Math.floor(Math.random() * posx.length);
                    mon = em.getMonster(8645003 + (tick / 5));
                    eim.registerMonster(mon);
                    map.spawnMonsterOnGroundBelow(mon, new java.awt.Point(posx[rd], 28));
                }
            }
            if (tick != 0) {
		free = 30 - map.getNumMonsters() >= 6 ? 6 : 30 - map.getNumMonsters();
                for (i = 0; i < free; i++) {
                    mon = em.getMonster(8645003);
                    eim.registerMonster(mon);
                    map.spawnMonsterOnGroundBelow(mon, new java.awt.Point(addposx[i], 28));
                }

            }
            tick++;
        }
	} catch (e) {
	    e.printStackTrace();
	    schedule.cancel(true);
	}
    }, 6000);
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {}

function changedMap(eim, player, mapid) {
    stage = parseInt(eim.getProperty("stage"));
    if (mapid != 450012210 && mapid != 450012250) {
        eim.unregisterPlayer(player);
        eim.disposeIfPlayerBelow(0, 0);
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    player = eim.getPlayers().get(0);
    stage = parseInt(eim.getProperty("stage"));
    if (mobId == 8645009) {
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
if (stage == 1) {
        player.changeMap(map.getId(), 1);
} else {
        player.changeMap(map.getId(), 0);
}
    }
    if (stage == 1) {
        map.broadcastMessage(CField.BlackLabel("#fn나눔고딕 ExtraBold##fs22##e#r[노말 듄켈]#k#fn나눔고딕 ExtraBold# 보상맵", 100, 3000, 3, -100, 50, 1, 4));
        spawnMonsterDunkel(eim, stage, 8950118, 7, 28); // 보상상자
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