importPackage(Packages.client.items);
importPackage(Packages.server.items);
importPackage(Packages.tools);
importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.awt);
importPackage(Packages.server);
importPackage(Packages.constants);
importPackage(Packages.tools.packet);
importPackage(Packages.server.life);
importPackage(Packages.server.maps.obtacle);

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

    if ((eim.getProperty("BossName") == "노말 스우" || eim.getProperty("BossName") == "하드 스우") && eim.getProperty("KillCount") == 0) {
        eim.startSpawnSelfDestructMob(mobzz, map);
    }
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
    mobid = Integer.parseInt(eim.getProperty("Boss_ID"));
    x = Integer.parseInt(eim.getProperty("Boss_x"));
    y = Integer.parseInt(eim.getProperty("Boss_y"));
    mapid = Integer.parseInt(eim.getProperty("StartMap"));
    player.warp(mapid);
    var deathCount = Integer.parseInt(eim.getProperty("DeathCount"));
    player.setDeathCount(deathCount);
    if (player.isLeader() && mobid != null) {
        var map = eim.getMapFactory().getMap(mapid);
        if (eim.getProperty("BossName") == "노말 스우" || eim.getProperty("BossName") == "하드 스우") {
            eim.registerEventSchedule("lotusBlueDebris", BossConstants.LOTUS_BLUE_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusBlueDebris, 1, BossConstants.LOTUS_BLUE_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_BLUE_ATOM_AMOUNT, 
                eim.getProperty("BossName") == "노말 스우" ? BossConstants.LOTUS_BLUE_ATOM_PROP : BossConstants.LOTUS_BLUE_ATOM_HARD_PROP);
            eim.registerEventSchedule("lotusPurpleDebris", BossConstants.LOTUS_PURPLE_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusPurpleDebris, 2, BossConstants.LOTUS_PURPLE_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_PURPLE_ATOM_AMOUNT, 
                eim.getProperty("BossName") == "노말 스우" ? BossConstants.LOTUS_PURPLE_ATOM_PROP : BossConstants.LOTUS_PURPLE_ATOM_HARD_PROP);
            eim.registerEventSchedule("lotusYellowDebris", BossConstants.LOTUS_YELLOW_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusYellowDebris, 3, BossConstants.LOTUS_YELLOW_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_YELLOW_ATOM_AMOUNT, 
                eim.getProperty("BossName") == "노말 스우" ? BossConstants.LOTUS_YELLOW_ATOM_PROP : BossConstants.LOTUS_YELLOW_ATOM_HARD_PROP);
            eim.registerEventSchedule("spawnSelfDestructMob", 10000, "spawnSelfDestructMob", map);
        }
        if (eim.getProperty("BossName") == "루시드") {
            map.spawnMonsterOnGroundBelow(em.getMonster(rucif), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(1439, 48));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(1439, 48));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880164), new Point(766, 48));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880164), new Point(766, 48));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(587, 48));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880171), new Point(587, 48));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880165), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880168), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8880169), new Point(x, y));
            spawnBoss(mobid, new Point(x, y), mapid, eim);
        } //else if (eim.getProperty("BossName") == "카오스 벨룸") {
        //spawnBoss(Integer.parseInt(eim.getProperty("Boss_Second")), new Point(x, y), mapid, eim);
        //} 
        else if (eim.getProperty("BossName") == "카오스 핑크빈") {
            map.spawnMonsterOnGroundBelow(em.getMonster(8820115), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8820116), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8820117), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8820118), new Point(x, y));
            map.spawnMonsterOnGroundBelow(em.getMonster(8820102), new Point(4, y));
            spawnBoss(8820210, new Point(x, y), mapid, eim);
        } else if (eim.getProperty("BossName") == "스우2") {
            mobid = Integer.parseInt(eim.getProperty("Boss_Second"));
            x = Integer.parseInt(eim.getProperty("Boss_x_2"));
            y = Integer.parseInt(eim.getProperty("Boss_y_2"));
            spawnBoss(mobid, new Point(x, y), mapid, eim);
        } else {
            if (eim.getProperty("BossName") != "진격의거인") {
                spawnBoss(mobid, new Point(x, y), mapid, eim);
            } else {
                spawnz(eim);
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
            var tese = chr.getWarpMap(100000000);
            chr.setClock(0);
            chr.changeMap(tese, tese.getPortal(0));
            chr.dropMessage(5, "이동되었습니다.");

        }
    } else {
        while (it.hasNext()) {
            var chr = it.next();
            var tese = chr.getWarpMap(100000000);
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
            player.setDeathCount(dc - 1);
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
                var tese = chr.getWarpMap(100000000);
                chr.changeMap(tese, tese.getPortal(0));

            }
            if (eim != null) {
                eim.dispose();
            }*/
            player.setClock(0);
            var tese = player.getWarpMap(100000000);
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
    } else if (eim.getProperty("BossName") == "노말 스우") {
        if (kc == 0) {
            eim.setProperty("KillCount", 1);
            var it = eim.getPlayers().iterator();
            var x = Integer.parseInt(eim.getProperty("Boss_x_2"));
            var y = Integer.parseInt(eim.getProperty("Boss_y_2"));
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(Integer.parseInt(eim.getProperty("SecondMap")));
                chr.deathCount();
            }
            var map = eim.getMapFactory().getMap(eim.getProperty("SecondMap"));
            spawnBoss(Integer.parseInt(eim.getProperty("Boss_Second")), new Point(x, y), Integer.parseInt(eim.getProperty("SecondMap")), eim);
            eim.registerEventSchedule("lotusBlueDebris", BossConstants.LOTUS_BLUE_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusBlueDebris, 1, BossConstants.LOTUS_BLUE_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_BLUE_ATOM_AMOUNT, BossConstants.LOTUS_BLUE_ATOM_PROP);
            eim.registerEventSchedule("lotusPurpleDebris", BossConstants.LOTUS_PURPLE_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusPurpleDebris, 2, BossConstants.LOTUS_PURPLE_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_PURPLE_ATOM_AMOUNT, BossConstants.LOTUS_PURPLE_ATOM_PROP);
            eim.registerEventSchedule("lotusRobotDebris", BossConstants.LOTUS_ROBOT_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusRobotDebris, 3, BossConstants.LOTUS_ROBOT_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_ROBOT_ATOM_AMOUNT, BossConstants.LOTUS_ROBOT_ATOM_PROP);
            eim.registerEventSchedule("lotusYellowDebris", BossConstants.LOTUS_YELLOW_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusYellowDebris, 4, BossConstants.LOTUS_YELLOW_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_YELLOW_ATOM_AMOUNT, BossConstants.LOTUS_YELLOW_ATOM_PROP);
        } else if (kc == 1) {
            eim.setProperty("KillCount", 99);
            var it = eim.getPlayers().iterator();
            var x = Integer.parseInt(eim.getProperty("Boss_x_3"));
            var y = Integer.parseInt(eim.getProperty("Boss_y_3"));
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(Integer.parseInt(eim.getProperty("ThirdMap")));
                chr.deathCount();
            }
            var map = eim.getMapFactory().getMap(eim.getProperty("ThirdMap"));
            spawnBoss(Integer.parseInt(eim.getProperty("Boss_Third")), new Point(x, y), Integer.parseInt(eim.getProperty("ThirdMap")), eim);
            eim.registerEventSchedule("lotusBlueDebris", BossConstants.LOTUS_BLUE_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusBlueDebris, 1, BossConstants.LOTUS_BLUE_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_BLUE_ATOM_AMOUNT, BossConstants.LOTUS_BLUE_ATOM_PROP);
            eim.registerEventSchedule("lotusPurpleDebris", BossConstants.LOTUS_PURPLE_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusPurpleDebris, 2, BossConstants.LOTUS_PURPLE_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_PURPLE_ATOM_AMOUNT, BossConstants.LOTUS_PURPLE_ATOM_PROP);
            eim.registerEventSchedule("lotusRobotDebris", BossConstants.LOTUS_ROBOT_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusRobotDebris, 3, BossConstants.LOTUS_ROBOT_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_ROBOT_ATOM_AMOUNT, BossConstants.LOTUS_ROBOT_ATOM_PROP);
            eim.registerEventSchedule("lotusYellowDebris", BossConstants.LOTUS_YELLOW_ATOM_EXECUTION_DELAY,
                "createObtacleAtom", map, ObtacleAtomEnum.LotusYellowDebris, 4, BossConstants.LOTUS_YELLOW_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                BossConstants.LOTUS_YELLOW_ATOM_AMOUNT, BossConstants.LOTUS_YELLOW_ATOM_PROP);
            eim.registerEventSchedule("lotusCrusherDebris", BossConstants.LOTUS_CRUSHER_ATOM_EXECUTION_DELAY,
		"createObtacleAtom", map, ObtacleAtomEnum.LotusCrusherDebris, 5, BossConstants.LOTUS_CRUSHER_ATOM_DAMAGE, BossConstants.LOTUS_OBSTACLE_ATOM_VELOCITY, 0,
                    BossConstants.LOTUS_CRUSHER_ATOM_AMOUNT, BossConstants.LOTUS_CRUSHER_ATOM_PROP);
        } else if (kc == 99) {
            eim.restartEventTimer(20000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "데미안2") {
        if (kc == 0) {
            var x = Integer.parseInt(eim.getProperty("Boss_x_2"));
            var y = Integer.parseInt(eim.getProperty("Boss_y_2"));
            eim.setProperty("KillCount", 99);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(Integer.parseInt(eim.getProperty("SecondMap")));
                chr.dropMessage(5, "2페이지로 넘어갑니다.");
            }
            spawnBoss(Integer.parseInt(eim.getProperty("Boss_Second")), new Point(x, y), Integer.parseInt(eim.getProperty("SecondMap")), eim);
        } else if (kc == 99) {
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(Integer.parseInt(eim.getProperty("Reward")), 1);
                chr.setClock(0);
                var tese = chr.getWarpMap(100000000);
                chr.changeMap(tese, tese.getPortal(0));
                chr.dropMessage(5, "클리어를 축하드립니다! 보상이 자동으로 지급됩니다.");
            }
            eim.unregisterAll();
        }
    } else if (eim.getProperty("BossName") == "데미안") {
        if (kc == 0) {
            var x = Integer.parseInt(eim.getProperty("Boss_x_2"));
            var y = Integer.parseInt(eim.getProperty("Boss_y_2"));
            eim.setProperty("KillCount", 99);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(Integer.parseInt(eim.getProperty("SecondMap")));
                chr.dropMessage(5, "2페이지로 넘어갑니다.");
            }
            spawnBoss(Integer.parseInt(eim.getProperty("Boss_Second")), new Point(x, y), Integer.parseInt(eim.getProperty("SecondMap")), eim);
        } else if (kc == 99) {
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "노말 윌") {
        if (kc == 0) { //1페 클리어
            var x = Integer.parseInt(eim.getProperty("Boss_x_2"));
            var y = Integer.parseInt(eim.getProperty("Boss_y_2"));
            eim.setProperty("KillCount", 1);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(Integer.parseInt(eim.getProperty("SecondMap")));
                chr.dropMessage(5, "2페이지로 넘어갑니다.");
            }
            eim.getMapFactory().getMap(eim.getProperty("SecondMap")).spawnMonsterOnGroundBelow(em.getMonster(eim.getProperty("Boss_unsuk1")), new Point(eim.getProperty("Boss_unsuk1_x"), eim.getProperty("Boss_unsuk1_y")));
            eim.getMapFactory().getMap(eim.getProperty("SecondMap")).spawnMonsterOnGroundBelow(em.getMonster(eim.getProperty("Boss_unsuk2")), new Point(eim.getProperty("Boss_unsuk2_x"), eim.getProperty("Boss_unsuk2_y")));
            spawnBoss(Integer.parseInt(eim.getProperty("Boss_Second")), new Point(x, y), Integer.parseInt(eim.getProperty("SecondMap")), eim);

            //spawnBoss(Integer.parseInt(eim.getProperty("Boss_unsuk1")), new Point(x, y), Integer.parseInt(eim.getProperty("SecondMap")), eim);
            //spawnBoss(Integer.parseInt(eim.getProperty("Boss_unsuk2")), new Point(x, y), Integer.parseInt(eim.getProperty("SecondMap")), eim);
        } else if (kc == 1) { //2페 클리어
            var x = Integer.parseInt(eim.getProperty("Boss_x_3"));
            var y = Integer.parseInt(eim.getProperty("Boss_y_3"));
            eim.setProperty("KillCount", 2);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(Integer.parseInt(eim.getProperty("ThirdMap")));
                chr.dropMessage(5, "3페이지로 넘어갑니다.");
            }
            spawnBoss(Integer.parseInt(eim.getProperty("Boss_Third1")), new Point(x, y), Integer.parseInt(eim.getProperty("ThirdMap")), eim);
            eim.getMapFactory().getMap(eim.getProperty("ThirdMap")).spawnMonsterOnGroundBelow(em.getMonster(eim.getProperty("Boss_unsuk1")), new Point(eim.getProperty("Boss_unsuk1_x"), eim.getProperty("Boss_unsuk1_y")));
            eim.getMapFactory().getMap(eim.getProperty("ThirdMap")).spawnMonsterOnGroundBelow(em.getMonster(eim.getProperty("Boss_unsuk2")), new Point(eim.getProperty("Boss_unsuk2_x"), eim.getProperty("Boss_unsuk2_y")));

        } else if (kc == 2) { //3페 클리어
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(Integer.parseInt(eim.getProperty("Reward")), 1);
                chr.dropMessage(5, "클리어를 축하드립니다! 보상이 자동으로 지급됩니다. 잠시 후 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "크로스") {
        if (kc == 0) {
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(Integer.parseInt(eim.getProperty("Reward")), 1);
                chr.dropMessage(5, "크로스 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 파풀라투스") {
        if (kc == 0) {
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(Integer.parseInt(eim.getProperty("Reward")), 1);
                chr.dropMessage(5, "카오스 파풀라투스 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else if (eim.getProperty("BossName") == "진 힐라") {
        if (kc == 0) {
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(Integer.parseInt(eim.getProperty("Reward")), 1);
                chr.dropMessage(5, "진 힐라 클리어를 축하드립니다! 자동으로 퇴장됩니다.");
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
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "클리어를 축하드립니다! 자동으로 퇴장됩니다.");
                chr.gainItem(4310064, 10);
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 반반") {
        if (kc == 0) {
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "클리어를 축하드립니다! 자동으로 퇴장됩니다.");
                chr.gainItem(4310064, 10);
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 벨룸") {
        if (kc == 0) {
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.dropMessage(5, "클리어를 축하드립니다! 자동으로 퇴장됩니다.");
                chr.gainItem(4310064, 10);
            }
        }
    } else if (eim.getProperty("BossName") == "카오스 퀸") {
        if (kc == 0) {
            var x = 48;
            var y = 135;
            eim.setProperty("KillCount", 1);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(105200711);
                chr.dropMessage(5, "2페이지로 넘어갑니다.");
            }
            eim.getMapFactory().getMap(105200711).resetFully();
            spawnBoss(8920001, new Point(x, y), 105200711, eim);
        } else if (kc == 1) {
            var x = 48;
            var y = 135;
            eim.setProperty("KillCount", 2);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(105200712);
                chr.dropMessage(5, "3페이지로 넘어갑니다.");
            }
            eim.getMapFactory().getMap(105200712).resetFully();
            spawnBoss(8920002, new Point(x, y), 105200712, eim);
        } else if (kc == 2) {
            var x = 48;
            var y = 135;
            eim.setProperty("KillCount", 99);
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.warp(105200713);
                chr.dropMessage(5, "4페이지로 넘어갑니다.");
            }
            eim.getMapFactory().getMap(105200713).resetFully();
            spawnBoss(8920003, new Point(x, y), 105200713, eim);
        } else if (kc == 99) {
            eim.restartEventTimer(10000);
            eim.setProperty("IsClear", "1");
            var it = eim.getPlayers().iterator();
            while (it.hasNext()) {
                var chr = it.next();
                chr.gainItem(4310064, 10);
                chr.dropMessage(5, "클리어를 축하드립니다! 자동으로 퇴장됩니다.");
            }
        }
    } else {
        eim.restartEventTimer(10000);
        eim.setProperty("IsClear", "1");
        var it = eim.getPlayers().iterator();
        while (it.hasNext()) {
            var chr = it.next();
            chr.dropMessage(5, "클리어를 축하드립니다! 자동으로 퇴장됩니다.");
        }
    }
}

function monsterKilled(eim, player, point) {}


function leftParty(eim, player) {
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        var tese = chr.getWarpMap(100000000);
        chr.setClock(0);
        chr.changeMap(tese, tese.getPortal(0));
        chr.dropMessage(5, "파티원이 파티를 그만둬서 원정대가 해체됩니다.");
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
        var tese = chr.getWarpMap(100000000);
        chr.changeMap(tese, tese.getPortal(0));
        chr.dropMessage(5, "파티장이 파티를 그만둬서 원정대가 해체됩니다.");
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}