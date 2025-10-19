importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);
importPackage(java.awt);

importPackage(Packages.objects.item);
importPackage(Packages.objects.users);
importPackage(Packages.objects.utils);
importPackage(Packages.constants);
importPackage(Packages.network.models);
importPackage(Packages.objects.wz.provider);
importPackage(Packages.objects.fields);
importPackage(Packages.objects.fields.obstacle);
importPackage(Packages.objects.fields.gameobject);
importPackage(Packages.objects.fields.gameobject.lifes);

var rucif = 8880166;

function init() {}


function playerRevive(eim, player) {
    return true;
}

function spawnBoss(bossid, point, mapid, eim) {
    var mobzz = em.getMonster(bossid);
    eim.registerMonster(mobzz);
    var map = eim.getMapFactory().getMap(mapid);

    map.spawnMonsterOnGroundBelow(mobzz, point);
    /*if (eim.getProperty("BossName").contains("스우") && eim.getProperty("KillCount") == 0) {
        eim.startSpawnSelfDestructMob(mobzz, map);
    }*/
}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("Boss_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Boss_" + a);
    return eim;
}



function playerEntry(eim, player) {
    if (eim.getProperty("Boss_ID") != null) {
        mobid = Integer.parseInt(eim.getProperty("Boss_ID"));
        x = Integer.parseInt(eim.getProperty("Boss_x"));
        y = Integer.parseInt(eim.getProperty("Boss_y"));
    }
    mapid = Integer.parseInt(eim.getProperty("StartMap"));
    player.warp(mapid);
    var deathCount = Integer.parseInt(eim.getProperty("DeathCount"));
    if (eim.getProperty("BossName") != "검은마법사") {
        player.setDeathCount(deathCount);
    }
    var bossMode = 0;
    if (eim.getProperty("BossMode") != null) {
	bossMode = Integer.parseInt(eim.getProperty("BossMode"));
    }
    player.setBossMode(bossMode);
    if (player.isLeader() && eim.getProperty("Boss_ID") != null) {
        var map = eim.getMapFactory().getMap(mapid);
        if (eim.getProperty("BossName") == "카오스 핑크빈") {
            map.spawnMonsterOnGroundBelow(em.getMonster(8820115), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8820116), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8820117), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8820118), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8820102), new Point(4, y));
            spawnBoss(8820210, new Point(x, y), mapid, eim);
        } else {
            if (!eim.getProperty("BossName").contains("스우") && !eim.getProperty("BossName").contains("블러디 퀸") && !eim.getProperty("BossName").contains("혼테일") && !eim.getProperty("BossName").contains("벨룸") && !eim.getProperty("BossName").contains("데미안") && !eim.getProperty("BossName").contains("루시드") && !eim.getProperty("BossName").contains("윌") && !eim.getProperty("BossName").contains("진힐라") && !eim.getProperty("BossName").contains("듄켈") && !eim.getProperty("BossName").contains("더스크") && !eim.getProperty("BossName").contains("파풀라투스")) {
                spawnBoss(mobid, new Point(x, y), mapid, eim);
            }
        }

    }

}

function spawnz(eim) {
    var mobzz = em.getMonster(9460026);
    mobzz.setFh(90);
    eim.registerMonster(mobzz);

    eim.getMapFactory().getMap(814031200).spawnMonsterOnGroundBelow(mobzz, new Point(96, -2377));
}


function scheduledTimeout(eim) {
    var isClear = eim.getProperty("IsClear");
    var it = eim.getPlayers().iterator();
    if (isClear == "1") {
        while (it.hasNext()) {
            var chr = it.next();
            var tese = chr.getWarpMap(ServerConstants.TownMap);
            chr.setClock(0);
            chr.changeMap(tese, tese.getPortal(0));
            chr.dropMessage(5, "이동되었습니다.");

        }
    } else {
        while (it.hasNext()) {
            var chr = it.next();
            var tese = chr.getWarpMap(ServerConstants.TownMap);
            chr.setClock(0);
            chr.changeMap(tese, tese.getPortal(0));
            chr.dropMessage(5, "시간이 지나, 파티가 해체됩니다.");

        }
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}


function playerDead(eim, player) {
    dc = player.getDeathCount();
    if (dc != null) {
        if (dc > 0) {
            if (!(player.getMapId() >= 450013000 && player.getMapId() <= 450013700)) {
		player.setDeathCount(dc - 1);
	}
            player.setAutoRespawn(30, 5);
            //player.warp(player.getMap().getId());
            //player.addHP(500000);
        } else {
            /*var it = eim.getPlayers().iterator();
            eim.unregisterAll();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "데스카운트를 모두 소모하였습니다.");
                chr.setClock(0);
                var tese = chr.getWarpMap(ServerConstants.TownMap);
                chr.changeMap(tese, tese.getPortal(0));

            }
            if (eim != null) {
                eim.dispose();
            }*/
            player.setClock(0);
            var tese = player.getWarpMap(ServerConstants.TownMap);
            player.changeMap(tese, tese.getPortal(0));
            eim.unregisterPlayer(player);

            if (eim.getPlayerCount() <= 0) {
                eim.unregisterAll();
                eim.dispose();
            }
        }
    }
}


function monsterValue(eim, mobid) {
    return 0;
}


function onMapLoad(eim, player) {
    player.deathCount();
}

function playerDisconnected(eim, player) {}


function changedMap(eim, player, mapid) {

    if (mapid == ServerConstants.TownMap) {
        eim.unregisterPlayer(player);
    }
}


function removePlayer(eim, player) {
    eim.unregisterPlayer(player);
    if (eim != null) {
        if (eim.getPlayerCount() <= 0) {
            eim.dispose();
        }
    }
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
    kc = Integer.parseInt(eim.getProperty("KillCount"));
    if (eim.getProperty("BossName") == "루시드") {
        if (kc == 0) {
            eim.setProperty("KillCount", 99);
            var it = eim.getPlayers().iterator();
            var x = Integer.parseInt(eim.getProperty("Boss_x_2"));
            var y = Integer.parseInt(eim.getProperty("Boss_y_2"));
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(Integer.parseInt(eim.getProperty("SecondMap")));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(297, -125));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(786, -194));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880164), new Point(964, -331));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880164), new Point(148, -550));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(353, -375));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(763, -490));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880165), new Point(1085, -619));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880168), new Point(525, -685));
                eim.getMapFactory().getMap(Integer.parseInt(eim.getProperty("SecondMap"))).spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(329, -855));
                chr.dropMessage(5, "2페이지로 넘어갑니다.");
            }
            spawnBoss(Integer.parseInt(eim.getProperty("Boss_Second")), new Point(x, y), Integer.parseInt(eim.getProperty("SecondMap")), eim);
        } else if (kc == 99) {
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(4033804, 1);
                chr.dropMessage(5, "클리어를 축하드립니다! 오르골이 지급되었으니, 입장 엔피시에게 가져다 주시길 바랍니다. 자동으로 퇴장됩니다.");
                //chr.worldGMMessage(7, "[클리어] 루시드보스가 클리어 됐습니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "진격의거인") {
        eim.restartEventTimer(10000);
        eim.setProperty("IsClear", "1");
        var it = eim.getPlayers().iterator();
        while (it.hasNext()) {
            var chr = it.next();
            chr.gainItem(Integer.parseInt(eim.getProperty("Boss_Reward")), 1);
            chr.dropMessage(5, "클리어를 축하드립니다! 토벌 증표가 지급되었으니, 진격의 거인 엔피시에게 가져다 주시길 바랍니다. 자동으로 퇴장됩니다.");
        }
    } else if (eim.getProperty("BossName") == "카오스 파풀라투스") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "파풀라투스 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "하드 힐라") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "힐라 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 핑크빈") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "카오스 핑크빈 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "반 레온") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "반 레온 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "매그 너스") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "매그너스 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "아카이럼") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(2434589, 1);
                chr.dropMessage(5, "아카이럼 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "시그 너스") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(2434588, 1);
                chr.dropMessage(5, "시그너스 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 혼테일") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "카오스 혼테일 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 피에르") {
        if (kc == 0) {
            var x = 491;
            var y = 551;
            eim.setProperty("KillCount", 1);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(105200611);
                chr.dropMessage(5, "2페이지로 넘어갑니다.");
            }
            eim.getMapFactory().getMap(105200611).resetFully();
            spawnBoss(8900001, new Point(x, y), 105200611, eim);
        } else if (kc == 1) {
            var x = 491;
            var y = 552;
            eim.setProperty("KillCount", 99);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(105200612);
                chr.dropMessage(5, "3페이지로 넘어갑니다.");
            }
            eim.getMapFactory().getMap(105200612).resetFully();
            spawnBoss(8900002, new Point(x, y), 105200612, eim);
        } else if (kc == 99) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "카오스 피에르 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 반반") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "카오스 반반 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 벨룸") {
        if (kc == 0) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "카오스 벨룸 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else {
        if (!eim.getProperty("BossName").contains("스우")) {
        	eim.restartEventTimer(20000);
        	eim.setProperty("IsClear", "1");
        	var it = eim.getPlayers().iterator();
        	while (it.hasNext()) {
            		var chr = it.next();
            		chr.dropMessage(5, "클리어를 축하드립니다! 자동으로 퇴장됩니다.");
        	}
        }
    }
}

function monsterKilled(eim, player, point) {}


function leftParty(eim, player) {
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        var tese = chr.getWarpMap(ServerConstants.TownMap);
        chr.setClock(0);
        chr.changeMap(tese, tese.getPortal(0));
        chr.dropMessage(5, "파티원 혹은 파티장이 파티를 그만두거나 맵을 이동하여 원정대가 해체됩니다.");
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}



function disbandParty(eim) {
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        chr.setClock(0);
        var tese = chr.getWarpMap(ServerConstants.TownMap);
        chr.changeMap(tese, tese.getPortal(0));
        chr.dropMessage(5, "파티원 혹은 파티장이 파티를 그만두거나 맵을 이동하여 원정대가 해체됩니다.");
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}