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

boss = [
    [6130101, 100000000],
    [8220007, 123456789],
    [6300005, 11223344556677]
]

function init() {
}


function playerRevive(eim, player) {
    return true;
}

function spawnBoss(bossid, point, mapid, eim, hp) {
    var mobzz = em.getMonster(bossid);
	mobzz.setHp(10000000);
      eim.registerMonster(mobzz);
      eim.getMapFactory().getMap(mapid).spawnMonsterWithEffect(mobzz, 15, point);
}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("BossRaid_" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("BossRaid_" + a);
    return eim;
}


function playerEntry(eim, player) {
    player.warp(931050810);
    eim.restartEventTimer(10000);
    player.send(Packages.network.models.CField.addPopupSay(9073025, 3000, "10초 후에 #b1단계 보스#k가 소환됩니다.#k", ""));
}

function scheduledTimeout(eim) {
    stage = Integer.parseInt(eim.getProperty("stage"));
    if (stage >= boss.length) {
        var it = eim.getPlayers().iterator();
        while (it.hasNext()) {
            var chr = it.next();
            var tese = chr.getWarpMap(123456789);
            chr.changeMap(tese, tese.getPortal(0));
        }
        eim.unregisterAll();
        if (eim != null) {
            eim.dispose();
        }
    } else {
 	var it = eim.getPlayers().iterator();
        while (it.hasNext()) {
            var chr = it.next();
            chr.send(Packages.network.models.CField.addPopupSay(9073025, 3000, "#b" + (stage + 1) + "단계 보스#k가 소환되었습니다.\r\n체력은 #b" + NtoKr(boss[stage][1]) + "#k입니다.", ""));
        }
        spawnBoss(boss[stage][0], new Point(-2080, 30), 931050810, eim, boss[stage][1]);
    }
}


function playerDead(eim, player) {
    var it = eim.getPlayers().iterator();
    eim.unregisterAll();
    while (it.hasNext()) {
        var chr = it.next();
        var tese = chr.getWarpMap(123456789);
        chr.changeMap(tese, tese.getPortal(0));

    }
    if (eim != null) {
        eim.dispose();
    }
}


function monsterValue(eim, mobid) {
    return 0;
}


function onMapLoad(eim, player) {

}

function playerDisconnected(eim, player) {}


function changedMap(eim, player, mapid) {
    /*if (mapid == 123456789) {
        eim.unregisterPlayer(player);
    }
    if (Math.floor(mapid / 10000) == 92507) {
        mapid2 = ((mapid - 925070000) / 100)
        mobid = [9305600, 9305601, 9305602, 9305603, 9305604, 9305605, 9305606, 9305607, 9305608, 9305619,
            9305610, 9305611, 9305612, 9305613, 9305614, 9305615, 9305616, 9305617, 9305618, 9305609,
            9305620, 9305621, 9305622, 9305623, 9305624, 9305625, 9305626, 9305627, 9305628, 9305629,
            9305630, 9305631, 9305632, 9305633, 9305634, 9305635, 9305636, 9305637, 9305638, 9305639,
            9305656, 9305657, 9305658, 9305659, 9305660, 9305661, 9305662, 9305663, 9305664, 9305665,
            9305666, 9305667, 9305668, 9305669, 9305670, 9305671, 9305672, 9305673, 9305674, 9305675,
            9305677, 9305678, 9305640
        ];
        mobhp = [5200000, 5740800, 6307200, 6930000, 7549200, 12342000, 13923000, 15105000, 16846000, 123456789,
            40824000, 45404550, 48593250, 55350000, 61600500, 68121000, 72342000, 90011250, 97902000, 1500000000,
            130536000, 159138000, 190350000, 242424000, 405504000, 497040000, 596496000, 706176000, 824256000, 3000000000,
            2108240000, 2526520000, 2976000000, 3464920000, 3986640000, 4551000000, 5149760000, 6474960000, 7971840000, 8000000000,
            42000000000, 63000000000, 84000000000, 105000000000, 105000000000, 212345678900, 315000000000, 420000000000, 525000000000, 525000000000,
            630000000000, 735000000000, 840000000000, 945000000000, 1050000000000, 1155000000000, 1260000000000, 1365000000000, 1470000000000, 1575000000000,
            1680000000000, 1785000000000, 2123456789000
        ];
        spawnBoss2(mobid[mapid2 - 1], new Point(0, 7), mapid, eim, mobhp[mapid2 - 1]);
        if (mapid2 >= 21 && mapid2 <= 30) {
            mobid2 = [9305641, 9305642, 9305643, 9305644, 9305645, 9305646, 9305647, 9305648, 9305649, 9305680];
            spawnBoss(mobid2[mapid2 - 21], new Point(-350, 7), mapid, eim);
            spawnBoss(mobid2[mapid2 - 21], new Point(-181, 7), mapid, eim);
            spawnBoss(mobid2[mapid2 - 21], new Point(181, 7), mapid, eim);
            spawnBoss(mobid2[mapid2 - 21], new Point(350, 7), mapid, eim);
        }
        if (mapid2 >= 31 && mapid2 <= 36) {
            spawnBoss2(mobid[mapid2 - 1], new Point(-250, 7), mapid, eim, mobhp[mapid2 - 1]);
        }
        if ((mapid2 >= 37 && mapid2 <= 39) || mapid2 == 45 || mapid2 == 50) {
            spawnBoss2(mobid[mapid2 - 1], new Point(-250, 7), mapid, eim, mobhp[mapid2 - 1]);
            spawnBoss2(mobid[mapid2 - 1], new Point(250, 7), mapid, eim, mobhp[mapid2 - 1]);
        }
    }*/
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
    eim.setProperty("stage", Integer.parseInt(eim.getProperty("stage")) + 1);
    eim.restartEventTimer(10000);
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        if (Integer.parseInt(eim.getProperty("stage")) >= boss.length) {
            말 = "모든 단계를 클리어하였습니다.\r\n10초뒤에 마을로 이동됩니다.";
        } else {
            말 = "보스를 처치하셨습니다.\r\n10초뒤에 다음단계 보스가 소환됩니다."
        }
        chr.send(Packages.network.models.CField.addPopupSay(9073025, 3000, 말, ""));
    }
}


function leftParty(eim, player) {
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        var tese = chr.getWarpMap(123456789);
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
        var tese = chr.getWarpMap(123456789);
        chr.changeMap(tese, tese.getPortal(0));
        chr.dropMessage(5, "파티장이 파티를 그만둬서 원정대가 해체됩니다.");
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}

var unitLt = ['만', '억', '조', '경'];

function NtoKr(num) {
    var rslt = num;
    if (('' + num).length > 4) {
        rslt = '';
        var pos = 0;
        var nlt = ('' + num).split('');
        for (var i = 0; i < nlt.length; i++) {
            rslt = nlt[nlt.length - (i + 1)] + rslt;
            if ((i + 1) < nlt.length && (i % 4) == 3) {
                if (parseInt(rslt) != "0000") {
                    rslt = unitLt[pos++] + " " + rslt;
                } else {
                    rslt = unitLt[pos++]
                }
            }

        }
    }
    return rslt;
}
