importPackage(Packages.objects.utils);
importPackage(Packages.objects.item);
importPackage(Packages.objects.fields.gameobject.lifes);
importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.awt);

function init() {
}


function playerRevive(eim, player) {
	return true;
}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("unionRaid_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("unionRaid_" + a);
    return eim;
}

function playerEntry(eim, player) {
    player.warp(Integer.parseInt(eim.getProperty("MapID")));

    player.beginRaid();
}

function scheduledTimeout(eim) {
}


function playerDead(eim, player) {
}


function monsterValue(eim, mobid) {
    return 0;
}


function onMapLoad(eim, player) {
}

function playerDisconnected(eim, player)
{
    player.endRaid();
}


function changedMap(eim, player, mapid) {
    player.endRaid();
    eim.unregisterPlayer(player);
}

function removePlayer(eim, player) {
    player.endRaid();
    eim.dispose();
}


function playerExit(eim, player) {
    player.endRaid();

    eim.unregisterPlayer(player);
    if (eim != null) {
        if (eim.getPlayerCount() <= 0) {
            eim.dispose();
        }
    }
}


function allMonstersDead(eim) {
}

function monsterKilled(eim, player, point) {
}


function leftParty(eim, player) {
    // 해당 파티원 맵이동 및 게임 오버
}



function disbandParty(eim) {
    eim.unregisterAll();
    if (eim != null)
    {
        eim.dispose();
    }
}

function cancelSchedule() {
}