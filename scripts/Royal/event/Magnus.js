importPackage(Packages.objects.utils);

// 이벤트매니저 초기화할 내용(채널별로 적용됨)
function init() {
    //입장 가능한 맵 갯수 총8개씩(이지와 노말은 통합)
    em.setProperty("status0", "0");
    //em.setProperty("status1", "0");
    //em.setProperty("status2", "0");
    //em.setProperty("status3", "0");
    //em.setProperty("status4", "0");
    //em.setProperty("status5", "0");
    //em.setProperty("status7", "0");

    em.setProperty("Nstatus0", "0");
    //em.setProperty("Cstatus1", "0");
    //em.setProperty("Cstatus2", "0");
    //em.setProperty("Cstatus3", "0");
    //em.setProperty("Cstatus4", "0");
    //em.setProperty("Cstatus5", "0");
    //em.setProperty("Cstatus7", "0");
    em.setProperty("Hstatus0", "0");

}

function playerRevive(eim, player) {
    return true;
}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("Magnus_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Magnus_" + a);
    eim.startEventTimer(60000 * 30);
    return eim;
}

function playerEntry(eim, player) {
    player.changeMap(parseInt(eim.getProperty("map")));
    player.setDeathCount(10);
}

function scheduledTimeout(eim) {
    //ㄱㄷㄱㄷ
    var it = eim.getPlayers().iterator();
    var exitMap = 401060399;
    if (eim.getProperty("mode") == "normal" || eim.getProperty("mode") == "hard") {
        exitMap = 401060000;
    }
    while (it.hasNext()) {
        var chr = it.next();
        eim.unregisterPlayer(chr);
        var tese = chr.getWarpMap(exitMap);
        chr.changeMap(tese, tese.getPortal(0));
    }
    eim.unregisterAll();
    if (eim != null) {
        var mode = eim.getProperty("mode");
        if (mode == "easy") {
            eim.getEventManager().setProperty("status0", "0");
        } else if (mode == "normal") {
            eim.getEventManager().setProperty("Nstatus0", "0");
        } else if (mode == "hard") {
            eim.getEventManager().setProperty("Hstatus0", "0");
        }
        eim.dispose();
    }
}

function playerDead(eim, player) {
    dc = player.getDeathCount();
    var exitMap = 401060399;
    if (eim.getProperty("mode") == "normal" || eim.getProperty("mode") == "hard") {
        exitMap = 401060000;
    }
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
                var mode = eim.getProperty("mode");
                if (mode == "easy") {
                    eim.getEventManager().setProperty("status0", "0");
                } else if (mode == "normal") {
                    eim.getEventManager().setProperty("Nstatus0", "0");
                } else if (mode == "hard") {
                    eim.getEventManager().setProperty("Hstatus0", "0");
                }
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
        var mode = eim.getProperty("mode");
        if (mode == "easy") {
            eim.getEventManager().setProperty("status0", "0");
        } else if (mode == "normal") {
            eim.getEventManager().setProperty("Nstatus0", "0");
        } else if (mode == "hard") {
            eim.getEventManager().setProperty("Hstatus0", "0");
        }
        eim.dispose();
    }
}

function changedMap(eim, player, mapid) {
    if (mapid != parseInt(eim.getProperty("map"))) {
        eim.unregisterPlayer(player);
        if (eim != null) {
            if (eim.getPlayerCount() <= 0) {
                var mode = eim.getProperty("mode");
                if (mode == "easy") {
                    eim.getEventManager().setProperty("status0", "0");
                } else if (mode == "normal") {
                    eim.getEventManager().setProperty("Nstatus0", "0");
                } else if (mode == "hard") {
                    eim.getEventManager().setProperty("Hstatus0", "0");
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
            if (mode == "easy") {
                eim.getEventManager().setProperty("status0", "0");
            } else if (mode == "normal") {
                eim.getEventManager().setProperty("Nstatus0", "0");
            } else if (mode == "hard") {
                eim.getEventManager().setProperty("Hstatus0", "0");
            }
            eim.dispose();
        }
    }
}

function allMonstersDead(eim) {}

function leftParty(eim, player) {
    // 탈퇴
    var exitMap = 401060399;
    if (eim.getProperty("mode") == "normal" || eim.getProperty("mode") == "hard") {
        exitMap = 401060000;
    }
    playerExit(eim, player);
    player.changeMap(exitMap);
}

function disbandParty(eim) {
    var exitMap = 401060399;
    if (eim.getProperty("mode") == "normal" || eim.getProperty("mode") == "hard") {
        exitMap = 401060000;
    }
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
        if (mode == "easy") {
            eim.getEventManager().setProperty("status0", "0");
        } else if (mode == "normal") {
            eim.getEventManager().setProperty("Nstatus0", "0");
        } else if (mode == "hard") {
            eim.getEventManager().setProperty("Hstatus0", "0");
        }
        eim.dispose();
    }
}

function cancelSchedule() {}
