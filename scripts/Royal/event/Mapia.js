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

var mapid = 932200001;
var returnmap = 932200003;


function init() {

}

function setup(eim) {
    var a = Packages.objects.utils.Randomizer.nextInt();
    while (em.getInstance("Mapia" + a) != null) {
        a = Packages.objects.utils.Randomizer.nextInt();
    }
    var eim = em.newInstance("Mapia" + a);
    return eim;
}

function playerEntry(eim, player) {
    em.setProperty("Stage", "start");
    em.setProperty("select_kill", "0");
    em.setProperty("select_police", "0");
    player.changeMap(eim.getMapFactory().getMap(mapid), eim.getMapFactory().getMap(mapid).getPortal("sp"));
    em.setProperty("killed", "");
    if (player.isLeader()) {
         eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"30초후에 역할이 지정됩니다.", ""));
    }
}

function changedMap(eim, player, mapid) {

}

function scheduledTimeout(eim) {
    arr = [0,1,2,3,4,5];
    stage = em.getProperty("Stage");
    if (stage == "start") {
        for (i=0; i<6; i++) {
            d = Math.floor(Math.random() * arr.length);
            rd = arr[d]
            if (i < 2) {
                em.setProperty("mapia"+i, rd+"");
            } else if (i == 2) {
                em.setProperty("police", rd+"");
            } else if (i == 3) {
                em.setProperty("doctor", rd+"");
            } else {
                em.setProperty("man", rd+"");
            }
            arr.splice(d,1);
            var it = eim.getPlayers()
            chr = it.get(rd)
            var job = i<2 ? "마피아" : i == 2 ? "경찰" : i == 3 ? "의사" : "시민"
            chr.dropMessage(6, "당신의 직업은 "+job+"입니다.");
            em.setProperty("Stage","night");
            eim.restartEventTimer(60000);
        }
            eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"밤이 되었습니다. 각 역할군은 엔피시를 통해 일을 진행해 주세요.", ""));
    } else if (stage == "night") {
        if (em.getProperty("kill") < 0 || em.getProperty("kill") == null || (em.getProperty("kill") == em.getProperty("cure"))) {
            eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"아침이 되었습니다. 아무일도 일어나지 않았습니다. 자유롭게 토론해주세요.",""));
            em.setProperty("kill", "-1");
            em.setProperty("Stage", "morning");
            eim.restartEventTimer(90000);
        } else {
            em.setProperty("killed", em.getProperty("killed") + em.getProperty("kill"));
            if (isEnd(eim) == 0) {
                eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"간밤에 "+eim.getPlayers().get(parseInt(em.getProperty("kill"))).getName()+"이(가) 사망하였습니다. 시민의 승리입니다. 마피아는 "+eim.getPlayers().get(parseInt(em.getProperty("mapia0"))).getName()+", "+eim.getPlayers().get(parseInt(em.getProperty("mapia1"))).getName()+"였습니다.",""));
                em.setProperty("Stage","end");
                eim.restartEventTimer(10000);
            } else if (isEnd(eim) == 1) {
                eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"간밤에 "+eim.getPlayers().get(parseInt(em.getProperty("kill"))).getName()+"이(가) 사망하였습니다. 마피아의 승리입니다. 마피아는 "+eim.getPlayers().get(parseInt(em.getProperty("mapia0"))).getName()+", "+eim.getPlayers().get(parseInt(em.getProperty("mapia1"))).getName()+"였습니다.",""));
                em.setProperty("Stage","end");
                eim.restartEventTimer(10000);
            } else {
                eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"아침이 되었습니다. 간밤에 "+eim.getPlayers().get(parseInt(em.getProperty("kill"))).getName()+"이(가) 마피아의 습격을 받고 사망하였습니다. 자유롭게 토론해주세요.",""));
            em.setProperty("kill", "-1");
            em.setProperty("Stage", "morning");
            eim.restartEventTimer(90000);
            }

        }
    } else if (stage == "morning") {
        eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"투표시간이 되었습니다. 마피아인것 같은 사람을 투표해주세요.",""));
        em.setProperty("Stage", "vote");
        for (i=0; i<6; i++) {
            em.setProperty("vote_"+i, "0");
        }
        em.setProperty("votemember", "");
        eim.restartEventTimer(30000);
    } else if (stage == "vote") {
        kill = -1;
        vote = 0;
        for (i=0; i<6; i++) {
            if (Integer.parseInt(em.getProperty("vote_"+i)) > vote) {
                kill = i;
                vote = Integer.parseInt(em.getProperty("vote_"+i))
            } else if (Integer.parseInt(em.getProperty("vote_"+i)) == vote) {
                kill = -1;
            }
        }
        em.setProperty("kill", "" + kill);
        if (em.getProperty("kill") != "-1") {
            em.setProperty("killed", em.getProperty("killed") + em.getProperty("kill"));
            talk = "투표를 통해 "+eim.getPlayers().get(em.getProperty("kill")).getName()+"가 사망하였습니다."
            if (isEnd(eim) == 0) {
                talk += " 시민의 승리입니다. 마피아는 "+eim.getPlayers().get(parseInt(em.getProperty("mapia0"))).getName()+", "+eim.getPlayers().get(parseInt(em.getProperty("mapia1"))).getName()+"였습니다.";
                eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,talk,""));
                em.setProperty("Stage","end");
                eim.restartEventTimer(10000);
            } else if (isEnd(eim) == 1) {
                talk += " 마피아의 승리입니다. 마피아는 "+eim.getPlayers().get(parseInt(em.getProperty("mapia0"))).getName()+", "+eim.getPlayers().get(parseInt(em.getProperty("mapia1"))).getName()+"였습니다.";  
                eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,talk,""));
                em.setProperty("Stage","end");
                eim.restartEventTimer(10000);
            } else {
                em.setProperty("Stage","readynight");
                eim.restartEventTimer(30000);

            }
        } else {
            talk = "투표에서 동점자가 나오거나, 투표가 진행되지 않아 아무도 사망하지 않았습니다.";
            em.setProperty("Stage","readynight");
        }
        eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,talk,""));
            eim.restartEventTimer(30000);
    } else if (stage == "readynight") {
        eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"밤이 되었습니다. 각 역할군은 엔피시를 통해 일을 진행해 주세요.",""));
        em.setProperty("select_kill", "0");
        em.setProperty("select_police", "0");
        em.setProperty("Stage", "night");
        eim.restartEventTimer(60000);
    } else if (stage == "end") {
        MapiaEnd(eim);
    } else {
        eim.getMapFactory().getMap(mapid).broadcastMessage(CField.addPopupSay(9000233, 3000,"[오류] [CODE : "+stage+"] 예기치 못한 오류가 발생하였습니다. 운영자께 문의해주세요. 30초후 퇴장됩니다.",""));
        em.setProperty("Stage","end");
        eim.restartEventTimer(30000);
    }
}

function isAlive(chrid) {
    getKillNumber = em.getProperty("killed");
    if (getKillNumber == null) {
        return true;
    } else if (getKillNumber.contains(chrid+"")) {
        return false;
    } else {
        return true;
    }
}

function isEnd(eim) {
    mapia = 0;
    normal = 0;
    for (i=0; i<6; i++) {
        if (isAlive(i)) {
            if (Integer.parseInt(em.getProperty("mapia0")) == i || Integer.parseInt(em.getProperty("mapia1")) == i) {
                mapia++;
            } else {
                normal++;
            }
        }
    }
    if (mapia == 0) {
        return 0; // 시민 승리
    } else if (mapia >= normal) {
        return 1; // 마피아 승리
    } else {
        return 2; // 끝나지 않음
    }
}

function monsterKilled(eim, player, mobid) {
}



function playerDead(eim, player) {

}


function monsterValue(eim, mobid) {
    return 0;
}


function disbandParty(eim) {
var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        var tese = chr.getWarpMap(932200003);
        chr.changeMap(tese, tese.getPortal(0));
        chr.dropMessage(5, "파티장이 파티를 그만둬서 원정대가 해체됩니다.");
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
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

function onMapLoad(eim, player) {

}

function playerDisconnected(eim, player)
{
    if (eim.getProperty("Global_MinPerson") == null)
    {
        return -1;
    }
    return -Integer.parseInt(eim.getProperty("Global_MinPerson"));
}

function MapiaEnd(eim) {
        var exit = em.getChannelServer().getMapFactory().getMap(returnmap);
        var it = eim.getPlayers().iterator();
        while (it.hasNext())
        {
            var chr = it.next();
            chr.changeMap(exit, exit.getPortal(0));
        }
        eim.unregisterAll();
        if (eim != null)
        {
            eim.dispose();
        }
}

function leftParty(eim, player) {
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        var tese = chr.getWarpMap(932200003);
        chr.changeMap(tese, tese.getPortal(0));
        chr.dropMessage(5, "파티원이 파티를 그만둬서 원정대가 해체됩니다.");
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}

function disbandParty(eim)
{
    var exit = eim.getPlayers().get(0).getClient().getChannelServer().getMapFactory().getMap(returnmap);
    var it = eim.getPlayers().iterator();
    while (it.hasNext())
    {
        var chr = it.next();
        chr.changeMap(exit, exit.getPortal(0));
        chr.Message("파티장이 파티를 그만둬서 더이상 퀘스트를 진행할 수 없습니다.");
    }
    eim.unregisterAll();
    if (eim != null)
    {
        eim.dispose();
    }
}

function cancelSchedule() {
}