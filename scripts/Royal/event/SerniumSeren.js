importPackage(Packages.objects.utils);
importPackage(Packages.network.models);

// 이벤트매니저 초기화할 내용(채널별로 적용됨)
function init() {
    em.setProperty("status0", "0");
}

function playerRevive(eim, player) {
    return true;
}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("SerniumSeren_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("SerniumSeren_" + a);
    eim.startEventTimer(60000 * 30);
    return eim;
}

function playerEntry(eim, player) {
    player.changeMap(parseInt(eim.getProperty("map")));
    player.setDeathCount(5);
    var bossMode = 0;
    if (eim.getProperty("BossMode") != null) {
	    bossMode = parseInt(eim.getProperty("BossMode"));
    }
    player.setBossMode(bossMode);
}

function scheduledTimeout(eim) {
    var it = eim.getPlayers().iterator();
    var exitMap = 410000670;
    while (it.hasNext()) {
        var chr = it.next();
        eim.unregisterPlayer(chr);
        var tese = chr.getWarpMap(exitMap);
        chr.changeMap(tese, tese.getPortal(0));
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.getEventManager().setProperty("status0", "0");
        eim.dispose();
    }
}

function playerDead(eim, player) {
    dc = player.getDeathCount();
    var exitMap = 410000670;
    if (dc != null) {
        if (dc > 0) {
            player.setAutoRespawn(30, 5);
            player.setDeathCount(dc - 1);
        } else {
            player.setClock(0);
            var tese = player.getWarpMap(exitMap);
            player.changeMap(tese, tese.getPortal(0));
            eim.unregisterPlayer(player);
            if (eim.getPlayerCount() <= 0) {
                eim.unregisterAll();
                eim.getEventManager().setProperty("status0", "0");
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

function playerDisconnected(eim, player) {
    eim.unregisterPlayer(player);
    if (eim != null) {
        eim.getEventManager().setProperty("status0", "0");
        eim.dispose();
    }
}

function changedMap(eim, player, mapid) {
    if (mapid != 410002000 && mapid != 410002020 && mapid != 410002040 && mapid != 410002060) {
        eim.unregisterPlayer(player);
        if (eim != null) {
            if (eim.getPlayerCount() <= 0) {
                eim.getEventManager().setProperty("status0", "0");
                eim.dispose();
            }
        }
    }
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    if (eim != null) {
        if (eim.getPlayerCount() <= 0) {
            eim.getEventManager().setProperty("status0", "0");
            eim.dispose();
        }
    }
}

function allMonstersDead(eim) {}

function leftParty(eim, player) {
    // 탈퇴
    var exitMap = 410000670;
    playerExit(eim, player);
    player.changeMap(exitMap);
}

function disbandParty(eim) {
    var exitMap = 410000670;
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        eim.unregisterPlayer(chr);
        var tese = chr.getWarpMap(exitMap);
        chr.changeMap(tese, tese.getPortal(0));
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.getEventManager().setProperty("status0", "0");
        eim.dispose();
    }
}

function cancelSchedule() {}
