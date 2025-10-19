importPackage(java.awt);
importPackage(Packages.objects.utils);
importPackage(Packages.network.models);

// 이벤트매니저 초기화할 내용(채널별로 적용됨)
function init() {
    //입장 가능한 맵 갯수 총8개씩(이지와 노말은 통합)
    em.setProperty("status0", "0");
    em.setProperty("Cstatus0", "0");

}

function playerRevive(eim, player) {
    return true;
}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("RootAbyssVonbon_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("RootAbyssVonbon_" + a);
    eim.startEventTimer(60000 * 10);
    return eim;
}

function playerEntry(eim, player) {
    player.changeMap(parseInt(eim.getProperty("map")));
    player.setDeathCount(5);
}

function scheduledTimeout(eim) {
    //ㄱㄷㄱㄷ
    var it = eim.getPlayers().iterator();
    var exitMap = 105200000;
    while (it.hasNext()) {
        var chr = it.next();
        eim.unregisterPlayer(chr);
        var tese = chr.getWarpMap(exitMap);
        chr.changeMap(tese, tese.getPortal(0));
    }
    eim.unregisterAll();
    if (eim != null) {
        var mode = eim.getProperty("mode");
        if (mode == "normal") {
            eim.getEventManager().setProperty("status0", "0");
        } else if (mode == "chaos") {
            eim.getEventManager().setProperty("Cstatus0", "0");
        }
        eim.dispose();
    }
}

function playerDead(eim, player) {
    dc = player.getDeathCount();
    if (dc != null) {
        player.setAutoRespawn(30, 5);
        player.setDeathCount(dc - 1);
        if (player.getDeathCount() == 0) {
            player.setClock(30);
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
        if (eim.getPlayerCount() <= 0) {
            var mode = eim.getProperty("mode");
            if (mode == "normal") {
                eim.getEventManager().setProperty("status0", "0");
            } else if (mode == "chaos") {
                eim.getEventManager().setProperty("Cstatus0", "0");
            }
            eim.dispose();
        }
    }
}

function changedMap(eim, player, mapid) {
    if (mapid != 105200100 && mapid != 105200110 && mapid != 105200500 && mapid != 105200510 && mapid != 105200520) {
        eim.unregisterPlayer(player);
        if (eim != null) {
            if (eim.getPlayerCount() <= 0) {
                var mode = eim.getProperty("mode");
                if (mode == "normal") {
                    eim.getEventManager().setProperty("status0", "0");
                } else if (mode == "chaos") {
                    eim.getEventManager().setProperty("Cstatus0", "0");
                }
                eim.dispose();
            }
        }
    }
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    if (eim != null) {
        if (eim.getPlayerCount() <= 0) {
            var mode = eim.getProperty("mode");
            if (mode == "normal") {
                eim.getEventManager().setProperty("status0", "0");
            } else if (mode == "chaos") {
                eim.getEventManager().setProperty("Cstatus0", "0");
            }
            eim.dispose();
        }
    }
}

function allMonstersDead(eim) {}

function leftParty(eim, player) {
    // 탈퇴
    var exitMap = 105200000;
    playerExit(eim, player);
    player.changeMap(exitMap);
}

function disbandParty(eim) {
    var exitMap = 105200000;
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        eim.unregisterPlayer(chr);
        var tese = chr.getWarpMap(exitMap);
        chr.changeMap(tese, tese.getPortal(0));
    }
    eim.unregisterAll();
    if (eim != null) {
        var mode = eim.getProperty("mode");
        if (mode == "normal") {
            eim.getEventManager().setProperty("status0", "0");
        } else if (mode == "chaos") {
            eim.getEventManager().setProperty("Cstatus0", "0");
        }
        eim.dispose();
    }
}

function cancelSchedule() {}
