importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.awt);
importPackage(Packages.objects.wz.provider);
importPackage(Packages.objects.users);
importPackage(Packages.objects.item);
importPackage(Packages.objects.utils);
importPackage(Packages.network.models);
importPackage(Packages.objects.fields);
importPackage(Packages.objects.fields.gameobject);
importPackage(Packages.objects.fields.gameobject.lifes);


function init() {
}


function playerRevive(eim, player) {
	return true;
}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("HungryMuto_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("HungryMuto_" + a);
    return eim;
}


var randArea = [new Point(105, -354), new Point(2644, -345), new Point(-929, -356), new Point(3778, -343), new Point(-1045, -847), new Point(3935, -841), new Point(1434, -791), new Point(1405, -1637)];

function playerEntry(eim, player) {
    mapid = Integer.parseInt(eim.getProperty("StartMap"));
    var difficulty = Integer.parseInt(eim.getProperty("Difficulty"));
 
    if (player.isLeader()) {
        // 무토 몹 소환
        for (var i = 0; i < 6; ++i) {
                eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642000), new Point(105, -354));
                eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642002), new Point(2644, -345));
                eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642004), new Point(-929, -356));
                eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642006), new Point(3778, -343));
                eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642008), new Point(-1045, -847));
                eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642010), new Point(3935, -841));
                eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642012), new Point(1434, -791));
                eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642014), new Point(1405, -1637));	
        }

        // 열매 나무 보스는 랜덤 위치에 소환
        eim.getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(em.getMonster(8642016), randArea[Math.floor(Math.random() * randArea.length)]);
	eim.startHungryMuto(player, difficulty, mapid);
    }
}

function scheduledTimeout(eim) {
    var isClear = eim.getProperty("IsClear");
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        var tese = chr.getWarpMap(450002023); // 무토 대기실
        chr.setClock(0);
        chr.changeMap(tese, tese.getPortal(0));
        // 게임 오버 이펙트

    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
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
}


function changedMap(eim, player, mapid) {
    if (mapid == 450002023) {
        eim.unregisterPlayer(player);
    }
}

function removePlayer(eim, player) {
    eim.dispose();
}


function playerExit(eim, player) {
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