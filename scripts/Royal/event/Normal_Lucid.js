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
    while (em.getInstance("Normal_Lucid" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Normal_Lucid" + a);
    eim.setInstanceMap(map).resetFully();
    eim.setInstanceMap(map + 50).resetFully();
    eim.setInstanceMap(map + 100).resetFully();
    eim.setInstanceMap(map + 150).resetFully();
    eim.setInstanceMap(map + 200).resetFully();
    return eim;
}

function playerEntry(eim, player) {
    eim.startEventTimer(1800000);
    eim.setProperty("stage", "0");
    var map = eim.getMapInstance(0);
    player.setDeathCount(10);
    player.changeMap(map, map.getPortal(0));
    playAnimation(player, eim);
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
    stage = parseInt(eim.getProperty("stage"));
    if (stage == 4) {
        WarptoNextStage(eim);
    } else {
        end(eim);
    }
}

function changedMap(eim, player, mapid) {
    stage = parseInt(eim.getProperty("stage"));
    if (mapid != 450004100 && mapid != 450004150 && mapid != 450004200 && mapid != 450004250 && mapid != 450004300) {
        player.setDeathCount(0);
        eim.unregisterPlayer(player);
        eim.disposeIfPlayerBelow(0, 0);
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    var stage = parseInt(eim.getProperty("stage"));
    if (mobId == 8880140 && stage == 1) {
        eim.setProperty("stage", "2");
        eim.schedule("WarptoNextStage", 5000);
    } else if (mobId == 8880150 && stage == 3) {
        eim.setProperty("stage", "4");
        eim.restartEventTimer(10000);
    }
    return 1;
}

function playAnimation(player, eim) {
    var stage = parseInt(eim.getProperty("stage"));
    if (stage == 0) {
        eim.setProperty("stage", "1");
        player.getClient().getSession().writeAndFlush(CField.showSpineScreen(true, false, true, "Map/Effect3.img/BossLucid/Lucid/lusi", "animation", 0, false, ""));
		player.getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.SetIngameDirectionMode(true, false, false, false));
    } else if (stage == 2) {
        eim.setProperty("stage", "3");
    }
    if (player.getParty().getLeader().getId() == player.getId()) {
        eim.schedule("WarptoNextStage", 9500);
    }
}

function WarptoNextStage(eim) {
    var stage = parseInt(eim.getProperty("stage"));
    var iter = eim.getPlayers().iterator();

    while (iter.hasNext()) {
        var pl = iter.next();
        map = eim.getMapInstance(stage);
        pl.changeMap(map.getId(), 0);
	if (stage == 2) {
            playAnimation(pl, eim);
        } else if (stage == 1) {
	    pl.getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.SetIngameDirectionMode(false, true, false, false));
		}
    }
    if (stage == 1) {
        spawnMonster(eim, stage, 8880140, 1021, 48);
        spawnMonster(eim, stage, 8880166, 1021, 48);
        spawnMonster(eim, stage, 8880165, 1021, 48);
        spawnMonster(eim, stage, 8880168, 1021, 48);
        spawnMonster(eim, stage, 8880169, 1021, 48);
    } else if (stage == 3) {
        spawnMonster(eim, stage, 8880150, 292, -125);
        spawnMonster(eim, stage, 8880175, 292, -125);
        spawnMonster(eim, stage, 8880178, 292, -125);
        spawnMonster(eim, stage, 8880179, 292, -125);
    } else if (stage == 4) {
        spawnMonster(eim, stage, 8880167, 80, 36)
        eim.restartEventTimer(300000);
        eim.setProperty("stage", "6");
    }
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