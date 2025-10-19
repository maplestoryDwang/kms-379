importPackage(Packages.client.items);
importPackage(Packages.server.items);
importPackage(Packages.tools);
importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.awt);
importPackage(Packages.server);
importPackage(Packages.tools.packet);
importPackage(Packages.server.life);


var enter = "\r\n";
var data, date, day;


var bossid = 8880101;
//var secondboss = 100135;
var startmap = 350160140;
//var secondmap = 450004250;
var exitmap = 910001000;
var bossname = "노말 데미안"
var limit = 3;
var time = 10;
var x = 1091,
    y = 17;
var deathcount = 10;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var msg = "#e<노말 데미안 원정>#n\r\n" + "데미안에게 맞설 준비는 되셨습니까?\r\n";
        msg += "<제한 시간 : #r" + time + "분#k>\r\n#d<일일 입장 횟수#k #d: #b" + cm.GetCount(bossname + "c") + "회#k / #r" + limit + "회#k>#k" + enter;
        msg += "#d<데스 카운트 :#k #r10회#k>#k" + enter;
        msg += "<보스 보상 : 현재미정>" + enter;
        msg += "#L0##b 노말 데미안 입장을 신청한다.";
        cm.sendSimple(msg);
    } else if (status == 1) {
        if (!cm.CountCheck(bossname + "c", limit)) {
            cm.sendOk("하루에 " + limit + "번만 가능하답니다.");
            cm.dispose();
            return;
        }
        if (cm.getPlayer().getParty() == null) {
            cm.sendOk("파티를 꾸리고 도전하시길 바랍니다.");
            cm.dispose();
            return;
        }
        if (!isPartyLeader()) {
            cm.sendOk("파티장이 아니면 신청할 수 없습니다.");
            cm.dispose();
            return;
        }
        if (!cm.allMembersHere()) {
            cm.sendOk("파티원이 전원 이곳에 모여있어야 합니다.");
            cm.dispose();
            return;
        }
        if (cm.getPlayerCount(startmap) > 0) {
            cm.sendOk("이미 누군가가 도전중입니다.\r\n#b다른 채널을 이용해 주세요.#k");
            cm.dispose();
            return;
        }
        var it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
        var countPass = true;
        while (it.hasNext()) {
            var chr = it.next();
            if (!CC(chr, bossname + "c", limit)) {
                countPass = false;
                break;
            }
        }
        if (!countPass) {
            cm.sendOk("파티원 중 던전 입장 횟수가 남아있지 않은 파티원이 있습니다.");
            cm.dispose();
            return;
        } else {
            var it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
            var countPass = true;
            while (it.hasNext()) {
                var chr = it.next();
                AC(chr, bossname + "c");
            }
        }
        if (cm.getPlayerCount(startmap) > 0) {
            cm.sendOk("이미 누군가가 도전중입니다.\r\n#b다른 채널을 이용해 주세요.#k");
            cm.dispose();
            return;
        }
        cm.resetMap(startmap);
        var em = cm.getEventManager("Boss");
        var eim = em.readyInstance();
        eim.setProperty("StartMap", startmap);
        //eim.setProperty("SecondMap", secondmap);
        eim.setProperty("ExitMap", exitmap);
        eim.setProperty("BossName", bossname);
        eim.setProperty("Boss_ID", bossid);
        //eim.setProperty("Boss_Second", secondboss);
        eim.setProperty("Boss_x", x);
        eim.setProperty("Boss_y", y);
        //eim.setProperty("Boss_x_2", x_2);
        //eim.setProperty("Boss_y_2", y_2);
        eim.setProperty("KillCount", 0);
        eim.setProperty("Leader", cm.getPlayer().getParty().getLeader().getName());
        eim.setProperty("DeathCount", deathcount);
        eim.startEventTimer(60000 * time);
        eim.registerParty(cm.getPlayer().getParty(), cm.getPlayer().getMap());
        cm.dispose();
    }
}


function isPartyLeader() {
    if (cm.getPlayer().getParty().getLeader().getId() == cm.getPlayer().getId())
        return true;
    else
        return false;
}

function AC(player, boss) {
    player.CountAdd(boss);
}

function CC(player, boss, limit) {
    return player.CountCheck(boss, limit);
}