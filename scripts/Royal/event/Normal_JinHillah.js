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
    while (em.getInstance("Normal_JinHillah" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Normal_JinHillah" + a);
    eim.setInstanceMap(map).resetFully();
    eim.setInstanceMap(map + 50).resetFully();
    return eim;
}

function playerEntry(eim, player) {
    eim.startEventTimer(1800000);
    eim.setProperty("stage", "0");
    var map = eim.getMapInstance(0);

    if (player.getParty().getLeader().getId() == player.getId()) {
        eim.schedule("WarptoNextStage", 15500);
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
    stage = parseInt(eim.getProperty("stage"));
    if (mapid != 450010500 && mapid != 450010400 && mapid != 450010550) {
        player.cancelEffectFromBuffStat(Packages.client.MapleBuffStat.DebuffIncHp);
        player.setDeathCount(0);
        eim.unregisterPlayer(player);
        eim.disposeIfPlayerBelow(0, 0);
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {

    if (mobId == 8880405) {
        stage = parseInt(eim.getProperty("stage"));
        eim.setProperty("stage", "1");

        var iter = eim.getPlayers().iterator();
        while (iter.hasNext()) {
            var player = iter.next();
            player.getMap().killAllMonsters(true);
            player.getClient().getSession().writeAndFlush(CField.addPopupSay(3003771, 3000, "#face4#내… 내 시간이 흘러가고 있어!", ""));
            player.cancelEffectFromBuffStat(Packages.client.MapleBuffStat.DebuffIncHp);
        }
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
        player.resetDeathCounts();
        player.changeMap(map.getId(), 0);
if (stage == 0) {
        player.getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.SetIngameDirectionMode(false, true, false, false));
        player.getClient().getSession().writeAndFlush(CField.addPopupSay(3003771, 3000, "#face0#어디… 네 영혼은 다른 것들과 어떻게 다른지 한번 볼까!", ""));
}
    }
if (stage == 1) {
        map.broadcastMessage(CField.BlackLabel("#fn나눔고딕 ExtraBold##fs22##e#r[진 힐라]#k#fn나눔고딕 ExtraBold# 보상맵", 100, 3000, 3, -100, 50, 1, 4));
        spawnMonster(eim, stage, 8950121, 785, 205); // 보상상자
        eim.restartEventTimer(300000);
} else {
    spawnMonster(eim, stage, 8880405, 0, 266);
    spawnMonster(eim, stage, 8880406, 0, 266);
    spawnMonster(eim, stage, 8880407, 0, 266);
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