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
    while (em.getInstance("battlePvP_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("battlePvP_" + a);
    return eim;
}

function playerEntry(eim, player) {
	if (eim.getProperty("mob_setup") == null) {
            eim.setProperty("mob_setup", "1");
            // Aeos님 제공 감사합니다.

            var map = eim.getMapFactory().getMap(921174100);

            map.spawnMonsterOnBattlePvP(em.getMonster(9303208), new java.awt.Point(2003, 1472), player.getBattleUserInfo()); // 3층 우 원공
            map.spawnMonsterOnBattlePvP(em.getMonster(9303207), new java.awt.Point(-2725, 1472), player.getBattleUserInfo()); // 3층 우 원공
            
            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-772, -100), player.getBattleUserInfo()); // 3층 우 원공
            map.spawnMonsterOnBattlePvP(em.getMonster(9303203), new java.awt.Point(-1035, -100), player.getBattleUserInfo()); // 3층 우 천록
            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-1211, -100), player.getBattleUserInfo()); // 3층 우 원공

            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-2426, -99), player.getBattleUserInfo()); // 3층 좌 원공
            map.spawnMonsterOnBattlePvP(em.getMonster(9303203), new java.awt.Point(-2239, -99), player.getBattleUserInfo()); // 3층 좌 천록
            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-1979, -99), player.getBattleUserInfo()); // 3층 좌 원공

            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-2018, 259), player.getBattleUserInfo()); // 2층 원공
            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-1834, 259), player.getBattleUserInfo()); // 2층 원공
            map.spawnMonsterOnBattlePvP(em.getMonster(9303203), new java.awt.Point(-1507, 259), player.getBattleUserInfo()); // 2층 천록
            map.spawnMonsterOnBattlePvP(em.getMonster(9303203), new java.awt.Point(-1383, 259), player.getBattleUserInfo()); // 2층 천록

            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-2478, 634), player.getBattleUserInfo()); // 1층 원공
            map.spawnMonsterOnBattlePvP(em.getMonster(9303203), new java.awt.Point(-2157, 634), player.getBattleUserInfo()); // 1층 천록
            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-1751, 634), player.getBattleUserInfo()); // 1층 원공
            map.spawnMonsterOnBattlePvP(em.getMonster(9303203), new java.awt.Point(-1172, 634), player.getBattleUserInfo()); // 1층 천록
            map.spawnMonsterOnBattlePvP(em.getMonster(9303202), new java.awt.Point(-855, 634), player.getBattleUserInfo()); // 1층 원공
            map.spawnMonsterOnBattlePvP(em.getMonster(9303203), new java.awt.Point(-701, 634), player.getBattleUserInfo()); // 1층 천록

            map.spawnMonsterOnBattlePvP(em.getMonster(9303204), new java.awt.Point(680, 260), player.getBattleUserInfo()); // 2층 크로노스
            map.spawnMonsterOnBattlePvP(em.getMonster(9303205), new java.awt.Point(894, 260), player.getBattleUserInfo()); // 2층 파이렛
            map.spawnMonsterOnBattlePvP(em.getMonster(9303205), new java.awt.Point(1112, 260), player.getBattleUserInfo()); // 2층 파이렛
            map.spawnMonsterOnBattlePvP(em.getMonster(9303204), new java.awt.Point(1342, 260), player.getBattleUserInfo()); // 2층 크로노스
            map.spawnMonsterOnBattlePvP(em.getMonster(9303205), new java.awt.Point(1458, 260), player.getBattleUserInfo()); // 2층 파이렛

            map.spawnMonsterOnBattlePvP(em.getMonster(9303204), new java.awt.Point(263, 629), player.getBattleUserInfo()); // 1층 크로노스
            map.spawnMonsterOnBattlePvP(em.getMonster(9303204), new java.awt.Point(567, 629), player.getBattleUserInfo()); // 1층 크로노스
            map.spawnMonsterOnBattlePvP(em.getMonster(9303205), new java.awt.Point(838, 629), player.getBattleUserInfo()); // 1층 파이렛
            map.spawnMonsterOnBattlePvP(em.getMonster(9303205), new java.awt.Point(1070, 629), player.getBattleUserInfo()); // 1층 파이렛
            map.spawnMonsterOnBattlePvP(em.getMonster(9303205), new java.awt.Point(1261, 629), player.getBattleUserInfo()); // 1층 파이렛
            map.spawnMonsterOnBattlePvP(em.getMonster(9303204), new java.awt.Point(1533, 629), player.getBattleUserInfo()); // 1층 크로노스
            map.spawnMonsterOnBattlePvP(em.getMonster(9303204), new java.awt.Point(1796, 629), player.getBattleUserInfo()); // 1층 크로노스

            map.spawnMonsterOnBattlePvP(em.getMonster(9303200), new java.awt.Point(-1399, 1410), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303200), new java.awt.Point(-1145, 1352), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303200), new java.awt.Point(-508, 1352), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303200), new java.awt.Point(-373, 1352), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303200), new java.awt.Point(-86, 1234), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303200), new java.awt.Point(119, 1291), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303200), new java.awt.Point(-93, 1410), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303200), new java.awt.Point(125, 1410), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303201), new java.awt.Point(-1051, 1410), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303201), new java.awt.Point(-614, 1410), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303201), new java.awt.Point(-685, 1410), player.getBattleUserInfo());
            map.spawnMonsterOnBattlePvP(em.getMonster(9303201), new java.awt.Point(287, 1410), player.getBattleUserInfo());
            // 여기까지 헤네
	}
}

function scheduledTimeout(eim) {
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
    // 맵에 다시 접속시에 배틀 유저 로딩
}

function playerDisconnected(eim, player) {
}

function changedMap(eim, player, mapid) {
    if (mapid == 100000000) {
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

function disbandParty(eim) {
}