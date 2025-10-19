importPackage(Packages.objects.utils);

// 이벤트매니저 초기화할 내용(채널별로 적용됨)
function init() {
    //입장 가능한 맵 갯수 총8개씩(이지와 노말은 통합)
    em.setProperty("status0", "0");

}

function playerRevive(eim, player) {
    return true;
}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("VonLeon_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("VonLeon_" + a);
    eim.startEventTimer(60000 * 30);
    return eim;
}

function playerEntry(eim, player) {
    player.changeMap(parseInt(eim.getProperty("map")));
    player.setDeathCount(5);
}

function scheduledTimeout(eim) {
    var it = eim.getPlayers().iterator();
    var exitMap = 211070000;
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
    var exitMap = 211070000;
    if (dc != null) {
        if (dc > 0) {
            player.setAutoRespawn(30, 5);
            player.setDeathCount(dc - 1);
        } else {
            eim.unregisterPlayer(player);
            var tese = player.getWarpMap(exitMap);
            player.changeMap(tese, tese.getPortal(0));
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
    if (mapid != parseInt(eim.getProperty("map")) && mapid != 211070101 && mapid != 211070103 && mapid != 211070105 && mapid != 211070350 && mapid != 211070450 && mapid != 211070550) { //공중감옥!
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
    var exitMap = 211070000;
    playerExit(eim, player);
    player.changeMap(exitMap);
}

function disbandParty(eim) {
    var exitMap = 211070000;
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
